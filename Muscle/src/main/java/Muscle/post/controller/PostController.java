package Muscle.post.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.common.dto.ResponseDto;
import Muscle.post.dto.RequestPost;
import Muscle.post.dto.ResponsePost;
import Muscle.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")

public class PostController {

    private final PostService PostService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createPost(@RequestBody RequestPost.CreatePostDto createPostDto, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        Long PostId = PostService.createPost(createPostDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post created successfully.")
                .data(PostId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PostMapping("/like/{postId}")
    public ResponseEntity<ResponseDto> likePost(@PathVariable("postId") Long postId, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.likePost(postId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unlike/{postId}")
    public ResponseEntity<ResponseDto> unlikePost(@PathVariable("postId") Long postId, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.unlikePost(postId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post unliked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/save/{postId}")
    public ResponseEntity<ResponseDto> savePost(@PathVariable("postId") Long postId, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.savePost(postId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post saved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unSave/{postId}")
    public ResponseEntity<ResponseDto> unSavePost(@PathVariable("postId") Long postId, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.unSavePost(postId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post unSaved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllPost(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getAllPost(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<ResponseDto> getPost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponsePost.GetPostDto response = PostService.getPost(postId, token);;
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @GetMapping("/getSavedPost")
    public ResponseEntity<ResponseDto> getSavedPost(HttpServletRequest request){
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponsePost.GetPostDto> response = PostService.getSavedPost(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Saved Post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }




    @GetMapping("/getByBoard/{board}")
    public ResponseEntity<ResponseDto> getByBoard(@PathVariable("board") String board, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getPostByBoard(board, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }





    @GetMapping("/getWrittenPost") //내가 작성한 게시글
    public ResponseEntity<ResponseDto> getWrittenPost(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getByWriterPost(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<ResponseDto> searchPost(HttpServletRequest request, @PathVariable("title") String title) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.searchPost(token, title);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post searched successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //신고 게시글 목록 조회(count 이상)
    @GetMapping("/getReportPostList/{count}")
    public ResponseEntity<ResponseDto> getReportPostList(HttpServletRequest request, @PathVariable("count") Long count) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetReportPostListDto> response = PostService.getReportPostList(token, count);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //신고 게시글 조회
    @GetMapping("/getReportPost/{postId}")
    public ResponseEntity<ResponseDto> getReportPost(HttpServletRequest request, @PathVariable("postId") Long postId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        ResponsePost.GetReportPostDto response = PostService.getReportPost(token, postId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Report post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }




    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updatePost(@RequestBody RequestPost.UpdatePostDto updatePostDto, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.updatePost(updatePostDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ResponseDto> deletePost(@PathVariable("postId") Long postId, HttpServletRequest request){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.deletePost(postId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @PostMapping("/uploadImg")
//    public ResponseEntity<ResponseDto> uploadPostImg(@RequestPart(value = "file", required = false) MultipartFile file,
//                                                      @RequestPart(value = "uploadImgDto") RequestPost.UploadImgDto dto){
//        String url = PostService.uploadImg(file, dto.getPostId());
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Image uploaded successfully.")
//                .data(url)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

//    @PostMapping("/uploadImg/{PostId}")
//    public ResponseEntity<ResponseDto> uploadPostImg(@RequestPart(value = "file", required = false) MultipartFile file,
//                                                      @PathVariable("PostId") Long PostId){
//        String url = PostService.uploadImg(file, PostId);
//
//        ResponseDto responseDto = ResponseDto.builder()
//                .message("Image uploaded successfully.")
//                .data(url)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
}
