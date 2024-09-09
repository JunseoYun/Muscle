package Muscle.post.service;


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

        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        Post post = RequestPost.CreatePostDto.toEntity(createPostDto, writerId);
        postRepository.save(post);
        return post.getPostId();
    }




    public void likePost(Long postId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
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
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId);
        likedPostRepository.delete(likedPost);
        Post post = postRepository.findById(postId).get();
        post.decreasePostLikeCount();
        postRepository.save(post);
    }


    public void savePost(Long postId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        SavedPost savedPost = SavedPost.builder()
                .postId(postId)
                .userId(userId)
                .build();
        savedPostRepository.save(savedPost);
        Post post = postRepository.findById(postId).get();
        postRepository.save(post);
    }

    public void unSavePost(Long postId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, postId);
        savedPostRepository.delete(savedPost);
        Post post = postRepository.findById(postId).get();
        postRepository.save(post);
    }



    public List<ResponsePost.GetAllPostDto> getAllPost(Optional<String> token) {
        String email = null;
        List<Post> entityList = postRepository.findAll();
        List<ResponsePost.GetAllPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByEmail(email).getId();

            entityList.stream().forEach(post -> {
                boolean isPostLiked = false;
                boolean isPostSaved = false;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, post.getPostId());
                if(likedPost != null)
                    isPostLiked = true;
                SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, post.getPostId());
                if(savedPost != null)
                    isPostSaved = true;
                dtoList.add(ResponsePost.GetAllPostDto.toDto(post, isPostLiked, isPostSaved));
            });
        }

        return dtoList;
    }


    public ResponsePost.GetPostDto getPost(Long postId, Optional<String> token) {
        Post post = postRepository.findById(postId).get();

        String email = null;
        boolean isPostLiked = false;
        boolean isPostSaved = false;

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByEmail(email).getId();
            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId);
            if(likedPost != null)
                isPostLiked = true;
            SavedPost savedPost = savedPostRepository.findByUserIdAndPostId(userId, postId);
            if(savedPost != null)
                isPostSaved = true;
        }
        return ResponsePost.GetPostDto.toDto(post, isPostLiked, isPostSaved);
    }


    public List<ResponsePost.GetSavedPostDto> getSavedPost(Optional<String> token){
        String email = null;
        List<ResponsePost.GetSavedPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByEmail(email).getId();
            List<SavedPost> entityList = savedPostRepository.findAllByUserId(userId);

            entityList.stream().forEach(savedPost -> {
                boolean isPostLiked = false;
                boolean isPostSaved = true;

                LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, savedPost.getPostId());
                if(likedPost != null)
                    isPostLiked = true;


                Post post = postRepository.findById(savedPost.getPostId()).get();
                dtoList.add(ResponsePost.GetSavedPostDto.toDto(post, isPostLiked, isPostSaved));
            });
        }

        return dtoList;
    }




    public List<ResponsePost.GetAllPostDto> getPostByBoard(String board, Optional<String> token) {
        String email = null;
        List<ResponsePost.GetAllPostDto> dtoList = new ArrayList<>();

        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
            Long userId = authRepository.findByEmail(email).getId();
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

                dtoList.add(ResponsePost.GetAllPostDto.toDto(post, isPostLiked, isPostSaved));
            });
        }


        return dtoList;
    }



    public List<ResponsePost.GetAllPostDto> getByWriterPost(Optional<String> token) {
        String email = null;
        List<ResponsePost.GetAllPostDto> dtoList = new ArrayList<>();
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
            Long writerId = authRepository.findByEmail(email).getId();
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

                dtoList.add(ResponsePost.GetAllPostDto.toDto(post, isPostLiked, isPostSaved));
            });
        }
        return dtoList;
    }








    public void updatePost(RequestPost.UpdatePostDto updatePostDto, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        Post originalPost = postRepository.findById(updatePostDto.getPostId()).get();
        if(Objects.equals(writerId, originalPost.getWriterId())) {
            Post updatedPost = RequestPost.UpdatePostDto.toEntity(originalPost, updatePostDto);
            postRepository.save(updatedPost);
        }
    }

    //Delete permission exception handling required.
    public void deletePost(Long postId, Optional<String> token) {
        String email = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long writerId = authRepository.findByEmail(email).getId();
        Post post = postRepository.findById(postId).get();
        if(Objects.equals(writerId, post.getWriterId())) {
            postRepository.delete(post);
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

