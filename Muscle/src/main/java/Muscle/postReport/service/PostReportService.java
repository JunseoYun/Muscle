package Muscle.postReport.service;

import Muscle.auth.entity.Auth;
import Muscle.auth.entity.UserRole;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.post.entity.Post;
import Muscle.post.repository.PostRepository;
import Muscle.postReport.dto.RequestPostReport;
import Muscle.postReport.dto.ResponsePostReport;
import Muscle.postReport.entity.PostReport;
import Muscle.postReport.repository.PostReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReportService {
    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    //신고 생성
    public Long createPostReport(Optional<String> token, RequestPostReport.CreatePostReportDto createPostReportDto) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long reporterId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(createPostReportDto.getPostId()).get();
        if(post == null) {
            throw new EntityNotFoundException();
        }
        PostReport postReport = postReportRepository.findByPostAndReporterId(post, reporterId);
        if(postReport != null) {
            throw new IllegalArgumentException("중복 신고 불가능");
        }
        postReport = RequestPostReport.CreatePostReportDto.toEntity(createPostReportDto, reporterId, post);
        postReportRepository.save(postReport);
        post.addReport(postReport);
        postRepository.save(post);

        return postReport.getReportId();
    }

    //게시글 신고 조회
    public List<ResponsePostReport.GetPostReportDto> getPostReport(Optional<String> token, Long postId) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("신고 확인 권한 없음");
        }
        Post post = postRepository.findById(postId).get();
        List<PostReport> entityList = postReportRepository.findAllByPost(post);
        List<ResponsePostReport.GetPostReportDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(postReport -> {
            Auth reporter = authRepository.findById(postReport.getReporterId()).get();
            dtoList.add(ResponsePostReport.GetPostReportDto.toDto(reporter, postReport));
        });
        return dtoList;
    }


    //유저 신고 조회
    public List<ResponsePostReport.GetPostReportDto> getUserReport(Optional<String> token, String reporterMuscleId) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("신고 확인 권한 없음");
        }


        Auth reporter = authRepository.findByMuscleId(reporterMuscleId);

        List<PostReport> entityList = postReportRepository.findAllByReporterId(reporter.getId());
        List<ResponsePostReport.GetPostReportDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(postReport -> {
            dtoList.add(ResponsePostReport.GetPostReportDto.toDto(reporter, postReport));
        });
        return dtoList;
    }


    //신고 삭제
    public void deletePostReport(Optional<String> token, Long postReportId) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if(admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("신고 삭제 권한 없음");
        }
        PostReport postReport = postReportRepository.findById(postReportId).get();
        Post post = postReport.getPost();
        postReportRepository.delete(postReport);
        if(post.getReportCount() > 0) {
            post.setReportCount(post.getReportCount() - 1);
        }
        postRepository.save(post);
    }

}
