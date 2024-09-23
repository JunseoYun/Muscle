package Muscle.post.service;


import Muscle.auth.entity.Auth;
import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.post.dto.RequestPost;
import Muscle.post.dto.ResponsePost;
import Muscle.post.entity.LikedPost;
import Muscle.post.entity.Post;
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
//    private final S3Service s3Service;
//    private final ReviewRepository reviewRepository;

    public Long createPost(RequestPost.CreatePostDto createPostDto, Optional<String> token) {

        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByMuscleId(muscleId).getId();
        Post post = RequestPost.CreatePostDto.toEntity(createPostDto, writerId);
        postRepository.save(post);
        return post.getPostId();
    }




    public void likePost(Long postId, Optional<String> token){
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        LikedPost likedPost = LikedPost.builder()
                .postId(postId)
                .userId(userId)
                .build();
        likedPostRepository.save(likedPost);
        Post post = postRepository.findById(postId).get();
        post.increasePostLikeCount();
        postRepository.save(post);
    }

    public void unlikePost(Long postId, Optional<String> token){
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId);
        likedPostRepository.delete(likedPost);
        Post post = postRepository.findById(postId).get();
        post.decreasePostLikeCount();
        postRepository.save(post);
    }


    public void savePost(Long postId, Optional<String> token){
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        SavedPost savedPost = SavedPost.builder()
                .postId(postId)
                .userId(userId)
                .build();
        savedPostRepository.save(savedPost);
        Post post = postRepository.findById(postId).get();
        postRepository.save(post);
    }

    public void unSavePost(Long postId, Optional<String> token){
        String muscleId = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, postId);
        savedPostRepository.delete(savedPost);
        Post post = postRepository.findById(postId).get();
        postRepository.save(post);
    }



    public List<ResponsePost.GetPostDto> getAllPost(Optional<String> token) {
        String muscleId = null;
        List<Post> entityList = postRepository.findAll();
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Auth user = authRepository.findByMuscleId(muscleId);

            entityList.stream().forEach(post -> {
                Auth writer = authRepository.findById(post.getWriterId()).get();
                boolean isPostLiked = false;
                boolean isPostSaved = false;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
                if(likedPost != null)
                    isPostLiked = true;
                SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(user.getId(), post.getPostId());
                if(savedPost != null)
                    isPostSaved = true;
                dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved));
            });
        }

        return dtoList;
    }


    public ResponsePost.GetPostDto getPost(Long postId, Optional<String> token) {
        Post post = postRepository.findById(postId).get();
        Auth writer = authRepository.findById(post.getWriterId()).get();

        String muscleId = null;
        boolean isPostLiked = false;
        boolean isPostSaved = false;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByMuscleId(muscleId).getId();
            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId);
            if(likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, postId);
            if(savedPost != null)
                isPostSaved = true;
        }
        return ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved);
    }


    public List<ResponsePost.GetPostDto> getSavedPost(Optional<String> token){
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByMuscleId(muscleId).getId();
            List<SavedPost> entityList = savedPostRepository.findAllByUserId(userId);

            entityList.stream().forEach(savedPost -> {
                boolean isPostLiked = false;
                boolean isPostSaved = true;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, savedPost.getPostId());
                if(likedPost != null)
                    isPostLiked = true;


                Post post = postRepository.findById(savedPost.getPostId()).get();
                Auth writer = authRepository.findById(post.getWriterId()).get();
                dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved));
            });
        }

        return dtoList;
    }




    public List<ResponsePost.GetPostDto> getPostByBoard(String board, Optional<String> token) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByMuscleId(muscleId).getId();
            List<Post> entityList = postRepository.findByBoard(board);

            entityList.stream().forEach(post -> {
                boolean isPostLiked = false;
                boolean isPostSaved = false;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, post.getPostId());
                if(likedPost != null)
                    isPostLiked = true;
                SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, post.getPostId());
                if(savedPost != null)
                    isPostSaved = true;
                Auth writer = authRepository.findById(post.getWriterId()).get();

                dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved));
            });
        }


        return dtoList;
    }



    //내가 작성한 게시글
    public List<ResponsePost.GetPostDto> getByWriterPost(Optional<String> token) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Long writerId = authRepository.findByMuscleId(muscleId).getId();
            List<Post> entityList = postRepository.findAllByWriterId(writerId);

            entityList.stream().forEach(post -> {
                boolean isPostLiked = false;
                boolean isPostSaved = false;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
                if(likedPost != null)
                    isPostLiked = true;
                SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
                if(savedPost != null)
                    isPostSaved = true;

                Auth writer = authRepository.findById(post.getWriterId()).get();
                dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved));
            });
        }
        return dtoList;
    }

    //게시글 검색
    public List<ResponsePost.GetPostDto> searchPost(Optional<String> token, String title) {
        String muscleId = null;
        List<ResponsePost.GetPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
            Long writerId = authRepository.findByMuscleId(muscleId).getId();
            List<Post> entityList = postRepository.findByTitleContainingOrdered(title);

            entityList.stream().forEach(post -> {
                boolean isPostLiked = false;
                boolean isPostSaved = false;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
                if(likedPost != null)
                    isPostLiked = true;
                SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(writerId, post.getPostId());
                if(savedPost != null)
                    isPostSaved = true;

                Auth writer = authRepository.findById(post.getWriterId()).get();
                dtoList.add(ResponsePost.GetPostDto.toDto(writer, post, isPostLiked, isPostSaved));
            });
        }
        return dtoList;

    }








    public void updatePost(RequestPost.UpdatePostDto updatePostDto, Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(updatePostDto.getPostId()).get();
        if(Objects.equals(userId, post.getWriterId())) {
            post.update(updatePostDto.getTitle(), updatePostDto.getContent());
            postRepository.save(post);
        } else {
            throw new IllegalArgumentException("Isn't your post.");
        }
    }


    public void deletePost(Long postId, Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(postId).get();
        if(Objects.equals(userId, post.getWriterId())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("Isn't your post.");
        }
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

