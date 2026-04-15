package Muscle.postReport.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.comment.dto.RequestComment;
import Muscle.comment.dto.ResponseComment;
import Muscle.common.dto.ResponseDto;
import Muscle.postReport.dto.RequestPostReport;
import Muscle.postReport.dto.ResponsePostReport;
import Muscle.postReport.service.PostReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postReport")
public class PostReportController {
    @Autowired
    private final PostReportService postReportService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    //신고 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createComment(HttpServletRequest request, @RequestBody RequestPostReport.CreatePostReportDto createPostReportDto) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        Long reportId = postReportService.createPostReport(token, createPostReportDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post reported successfully.")
                .data(reportId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //게시글 신고 조회
    @GetMapping("/getPostReport/{postId}")
    public ResponseEntity<ResponseDto> getPostReport(HttpServletRequest request, @PathVariable("postId") Long postId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePostReport.GetPostReportDto> response = postReportService.getPostReport(token, postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post report list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //유저 신고 조회
    @GetMapping("/getUserReport/{muscleId}")
    public ResponseEntity<ResponseDto> getUserReport(HttpServletRequest request, @PathVariable("muscleId") String muscleId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePostReport.GetPostReportDto> response = postReportService.getUserReport(token, muscleId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("user report list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<ResponseDto> deleteComment(HttpServletRequest request, @PathVariable("reportId") Long reportId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        postReportService.deletePostReport(token, reportId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Report deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
