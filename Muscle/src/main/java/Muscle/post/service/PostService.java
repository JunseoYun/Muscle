package Muscle.post.service;


import Muscle.auth.dto.ResponseAuth;
import Muscle.auth.entity.Auth;
import Muscle.auth.entity.Follow;
import Muscle.auth.entity.UserRole;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.repository.FollowRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.post.dto.RequestPost;
import Muscle.post.dto.ResponsePost;
import Muscle.post.entity.LikedPost;
import Muscle.post.entity.Post;
import Muscle.post.entity.PostRole;
import Muscle.post.entity.SavedPost;
import Muscle.post.repository.LikedPostRepository;
import Muscle.post.repository.PostRepository;
import Muscle.post.repository.SavedPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class PostService {
    private final PostRepository postRepository;
    private final LikedPostRepository likedPostRepository;
    private final SavedPostRepository savedPostRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final FollowRepository followRepository;
//    private final S3Service s3Service;
//    private final ReviewRepository reviewRepository;

    public Long createPost(RequestPost.CreatePostDto createPostDto, Optional<String> token) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth writer = authRepository.findByMuscleId(muscleId);
        PostRole postRole = getPostRole(createPostDto.getPostRole());
        if (postRole == PostRole.PRO && (writer.getRole() != UserRole.PRO && writer.getRole() != UserRole.ADMIN)) {
            throw new IllegalArgumentException("PRO 글 작성 권한 없음.");
        }
        Post post = RequestPost.CreatePostDto.toEntity(createPostDto, writer.getId(), postRole);
        postRepository.save(post);
        return post.getPostId();
    }




    public void likePost(Optional<String> token, RequestPost.SendPostIdDto sendPostIdDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();

        Post post = postRepository.findById(sendPostIdDto.getPostId()).get();
        if (post == null) {
            throw new IllegalArgumentException("게시글 없음");
        }
        LikedPost likedPost = LikedPost.builder()
                .postId(post.getPostId())
                .userId(userId)
                .build();
        likedPostRepository.save(likedPost);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public void unlikePost(Optional<String> token, RequestPost.SendPostIdDto sendPostIdDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(sendPostIdDto.getPostId()).get();
        LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, post.getPostId());

        likedPostRepository.delete(likedPost);

        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
        }
        postRepository.save(post);
    }


    public void savePost(Optional<String> token, RequestPost.SendPostIdDto sendPostIdDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(sendPostIdDto.getPostId()).get();
        if (post == null) {
            throw new IllegalArgumentException("게시글 없음");
        }
        SavedPost savedPost = SavedPost.builder()
                .postId(post.getPostId())
                .userId(userId)
                .build();
        savedPostRepository.save(savedPost);
        postRepository.save(post);
    }

    public void unSavePost(Optional<String> token, RequestPost.SendPostIdDto sendPostIdDto) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(sendPostIdDto.getPostId()).get();
        if (post == null) {
            throw new IllegalArgumentException("게시글 없음");
        }
        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, post.getPostId());
        savedPostRepository.delete(savedPost);
        postRepository.save(post);
    }


    public List<ResponsePost.GetPostDto> getAllPost(Optional<String> token) {
        String muscleId = null;
        List<Post> entityList = postRepository.findAll();
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);

        entityList.stream().forEach(post -> {
            Auth writer = authRepository.findById(post.getWriterId()).get();
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }

            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });


        return dtoList;
    }


    public ResponsePost.GetPostDto getPost(Long postId, Optional<String> token) {
        Post post = postRepository.findById(postId).get();
        Auth writer = authRepository.findById(post.getWriterId()).get();

        String muscleId = null;
        boolean isPostLiked = false;
        boolean isPostSaved = false;
        boolean isFollowed = false;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), postId);
        if (likedPost != null)
            isPostLiked = true;
        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), postId);
        if (savedPost != null)
            isPostSaved = true;
        Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
        if (follow != null) {
            isFollowed = true;
        }

        return ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed);
    }


    public List<ResponsePost.GetPostDto> getSavedPost(Optional<String> token) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<SavedPost> entityList = savedPostRepository.findAllByUserId(user.getId());

        entityList.stream().forEach(savedPost -> {
            boolean isPostLiked = false;
            boolean isPostSaved = true;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), savedPost.getPostId());
            if (likedPost != null)
                isPostLiked = true;


            Post post = postRepository.findById(savedPost.getPostId()).get();
            Auth writer = authRepository.findById(post.getWriterId()).get();

            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });


        return dtoList;
    }


    public List<ResponsePost.GetPostDto> getPostByBoard(String board, Optional<String> token) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<Post> entityList = postRepository.findByBoard(board);

        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;
            Auth writer = authRepository.findById(post.getWriterId()).get();
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });


        return dtoList;
    }


    //내가 작성한 게시글
    public List<ResponsePost.GetPostDto> getByWriterPost(Optional<String> token) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByMuscleId(muscleId).getId();
        List<Post> entityList = postRepository.findAllByWriterId(writerId);

        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
            if (savedPost != null)
                isPostSaved = true;

            Auth writer = authRepository.findById(post.getWriterId()).get();
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });

        return dtoList;
    }

    //베스트 게시글 조회(전체 게시글 중 좋아요 많은 순 10걔) - 비로그인
    public List<ResponsePost.GetPostDto> getBestPosts() {
        List<Post> entityList = postRepository.findTop10ByOrderByLikeCountDesc();
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        boolean isPostLiked = false;
        boolean isPostSaved = false;
        boolean isFollowed = false;

        entityList.stream().forEach(post -> {
            Auth writer = authRepository.findById(post.getWriterId()).get();
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });

        return dtoList;
    }

    //투데이 베스트 게시글 조회(24시간이 지나지 않은 게시글 중 좋아요 많은 순 10개) - 비로그인
    public List<ResponsePost.GetPostDto> getTodayBestPosts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        List<Post> entityList = postRepository.findTop10ByPostDateAfterOrderByLikeCountDesc(twentyFourHoursAgo);
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        boolean isPostLiked = false;
        boolean isPostSaved = false;
        boolean isFollowed = false;

        entityList.stream().forEach(post -> {
            Auth writer = authRepository.findById(post.getWriterId()).get();
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });

        return dtoList;
    }

    //각 게시판 최근 게시글 10개 - 비로그인
    public List<ResponsePost.GetPostSimpleDto> geTopBoardNewPosts(String postRoleString) {
        PostRole postRole = getPostRole(postRoleString);
        List<Post> entityList = postRepository.findTop10ByPostRoleOrderByPostDateDesc(postRole);
        List<ResponsePost.GetPostSimpleDto> dtoList = new ArrayList<>();

        entityList.stream().forEach(post -> {
            Auth writer = authRepository.findById(post.getWriterId()).get();
            dtoList.add(ResponsePost.GetPostSimpleDto.toDto(writer, post));
        });
        return dtoList;
    }

    //세부 게시판 베스트 게시글 조회(좋아요 많은 순 10개) - 로그인
    public List<ResponsePost.GetPostDto> getBoardBestPosts(Optional<String> token, String board) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<Post> entityList = postRepository.findTop10ByBoardOrderByLikeCountDesc(board);

        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;
            Auth writer = authRepository.findById(post.getWriterId()).get();
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });
        return dtoList;
    }

    //세부 게시판 게시글 최신 순 조회 - 로그인
    public List<ResponsePost.GetPostDto> getBoardNewPosts(Optional<String> token, String board) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<Post> entityList = postRepository.findByBoardOrderByPostDateDesc(board);

        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;
            Auth writer = authRepository.findById(post.getWriterId()).get();
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });
        return dtoList;
    }

    //특정 유저 게시글 조회 - 로그인
    public List<ResponsePost.GetPostDto> getUserNewPosts(Optional<String> token, Long targetId) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<Post> entityList = postRepository.findAllByWriterIdOrderByPostDateDesc(targetId);

        Auth writer = authRepository.findById(targetId).get();
        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });
        return dtoList;
    }


    //게시글 검색
    public List<ResponsePost.GetPostDto> searchPost(Optional<String> token, String title) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth user = authRepository.findByMuscleId(muscleId);
        List<Post> entityList = postRepository.findByTitleContainingOrdered(title);

        entityList.stream().forEach(post -> {
            boolean isPostLiked = false;
            boolean isPostSaved = false;
            boolean isFollowed = false;

            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
            if (savedPost != null)
                isPostSaved = true;

            Auth writer = authRepository.findById(post.getWriterId()).get();
            Follow follow = followRepository.findByFollowerAndFollowing(user, writer);
            if (follow != null) {
                isFollowed = true;
            }
            dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved, isFollowed));
        });

        return dtoList;

    }

    //신고 게시글 목록 조회(count 이상)
    public List<ResponsePost.GetReportPostListDto> getReportPostList(Optional<String> token, Long count) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("신고 확인 권한 없음");
        }
        List<ResponsePost.GetReportPostListDto> dtoList = new ArrayList<>();
        List<Post> entityList = postRepository.findByReportCountGreaterThanEqual(count);
        entityList.stream().forEach(post -> {
            Auth writer = authRepository.findById(post.getWriterId()).get();
            dtoList.add(ResponsePost.GetReportPostListDto.toDto(writer, post));
        });
        return dtoList;
    }

    //신고 게시글 조회
    public ResponsePost.GetReportPostDto getReportPost(Optional<String> token, Long postId) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("신고 확인 권한 없음");
        }
        Post post = postRepository.findById(postId).get();
        Auth writer = authRepository.findById(post.getWriterId()).get();

        return ResponsePost.GetReportPostDto.toDto(writer, post);
    }


    //게시글 좋아요한 회원 조회
    public List<ResponseAuth.SearchUserDto> getPostLikeUsers(Optional<String> token, Long postId) {
        String muscleId = null;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Auth admin = authRepository.findByMuscleId(muscleId);
        if (admin == null) {
            throw new IllegalArgumentException("비회원 접근 제어");
        }
        List<LikedPost> entityList = likedPostRepository.findAllByPostId(postId);
        List<Auth> userList = new ArrayList<>();
        List<ResponseAuth.SearchUserDto> dtoList = new ArrayList<>();

        entityList.stream().forEach(likedPost -> {
            Auth user = authRepository.findById(likedPost.getUserId()).get();
            userList.add(user);
        });

        userList.stream().forEach(auth -> {
            dtoList.add(ResponseAuth.SearchUserDto.toDto(auth));
        });
        return dtoList;
    }


    public void updatePost(RequestPost.UpdatePostDto updatePostDto, Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(updatePostDto.getPostId()).get();
        if (Objects.equals(userId, post.getWriterId())) {
            post.update(updatePostDto.getTitle(), updatePostDto.getContent());
            postRepository.save(post);
        } else {
            throw new IllegalArgumentException("Isn't your post.");
        }
    }


    public void deletePost(Long postId, Optional<String> token) {
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(postId).get();
        if (Objects.equals(userId, post.getWriterId())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("Isn't your post.");
        }
    }

    private static PostRole getPostRole(String postRoleString) {
        return switch (postRoleString) {
            case "PRO" -> PostRole.PRO;
            case "AMATEUR" -> PostRole.AMATEUR;
            case "FREE" -> PostRole.FREE;
            default -> throw new IllegalArgumentException("Invalid post role: " + postRoleString);
        };
    }

    //    public String uploadImg(MultipartFile file, long storeId){
//        Store store = storeRepository.findById(storeId).get();
//
////        if (!store.getImgUrl().isEmpty())
////            s3Service.deleteFile(store.getImgUrl());
//
//        String url = "";
//        try {
//            url = s3Service.upload(file,"store");
//        }
//        catch (IOException e){
//            System.out.println("S3 upload failed.");
//        }
//
//        store.setImgUrl(url);
//        storeRepository.save(store);
//        return url;
//    }

}

