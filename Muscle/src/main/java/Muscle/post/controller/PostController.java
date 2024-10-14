package Muscle.post.controller;

import Muscle.auth.dto.ResponseAuth;
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

import java.io.IOException;
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


    @PostMapping("/like")
    public ResponseEntity<ResponseDto> likePost(HttpServletRequest request, @RequestBody RequestPost.SendPostIdDto sendPostIdDto){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.likePost(token, sendPostIdDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unlike")
    public ResponseEntity<ResponseDto> unlikePost(HttpServletRequest request, @RequestBody RequestPost.SendPostIdDto sendPostIdDto){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.unlikePost(token, sendPostIdDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post unliked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> savePost(HttpServletRequest request, @RequestBody RequestPost.SendPostIdDto sendPostIdDto){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.savePost(token, sendPostIdDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Post saved successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unSave")
    public ResponseEntity<ResponseDto> unSavePost(HttpServletRequest request, @RequestBody RequestPost.SendPostIdDto sendPostIdDto){
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        PostService.unSavePost(token, sendPostIdDto);
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


    //베스트 게시글 조회(전체 게시글 중 좋아요 많은 순 10걔) - 비로그인
    @GetMapping("/getBestPosts")
    public ResponseEntity<ResponseDto> getWrittenPost() {
        List<ResponsePost.GetPostDto> response = PostService.getBestPosts();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Best post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //투데이 베스트 게시글 조회(24시간이 지나지 않은 게시글 중 좋아요 많은 순 10개) - 비로그인
    @GetMapping("/getTodayBestPosts")
    public ResponseEntity<ResponseDto> getTodayBestPosts() {
        List<ResponsePost.GetPostDto> response = PostService.getTodayBestPosts();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Today best post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //각 게시판 최근 게시글 10개 - 비로그인
    @GetMapping("/geTopBoardNewPosts/{postRoleString}")
    public ResponseEntity<ResponseDto> geTopBoardNewPosts(@PathVariable("postRoleString") String postRoleString) {
        List<ResponsePost.GetPostSimpleDto> response = PostService.geTopBoardNewPosts(postRoleString);
        ResponseDto responseDto = ResponseDto.builder()
                .message("PostRole by Best post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //세부 게시판 베스트 게시글 조회(좋아요 많은 순 10개) - 로그인
    @GetMapping("/getBoardBestPosts/{board}")
    public ResponseEntity<ResponseDto> getBoardBestPosts(HttpServletRequest request, @PathVariable("board") String board) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getBoardBestPosts(token, board);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board by Best post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //세부 게시판 게시글 최신 순 조회 - 로그인
    @GetMapping("/getBoardNewPosts/{board}")
    public ResponseEntity<ResponseDto> getBoardNewPosts(HttpServletRequest request, @PathVariable("board") String board) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getBoardNewPosts(token, board);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Board by New post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    //특정 유저 게시글 조회 - 로그인
    @GetMapping("/getUserNewPosts/{targetId}")
    public ResponseEntity<ResponseDto> getUserNewPosts(HttpServletRequest request, @PathVariable("targetId") Long targetId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getUserNewPosts(token, targetId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User by New post list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    //내가 팔로우한 사람들의 최신 게시글 순 조회 - 로그인
    @GetMapping("/getFollowingPosts")
    public ResponseEntity<ResponseDto> getFollowingPosts(HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponsePost.GetPostDto> response = PostService.getFollowingPosts(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("User by Following post list retrieved successfully.")
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

    //게시글 좋아요한 회원 조회
    @GetMapping("/getLikeUser/{postId}")
    public ResponseEntity<ResponseDto> getPostLikeUsers (HttpServletRequest request, @PathVariable("postId") Long postId) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        List<ResponseAuth.SearchUserDto> response = PostService.getPostLikeUsers(token, postId);
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

    @PostMapping("/uploadImg/{postId}")
    public ResponseEntity<ResponseDto> uploadPostImg(@RequestParam("files") MultipartFile[] files,
                                                      @PathVariable("postId") Long PostId) throws IOException {
        List<String> url = PostService.uploadImg(files, PostId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .data(url)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
