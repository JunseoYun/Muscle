package Muscle.comment.controller;

import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.comment.dto.RequestComment;
import Muscle.comment.dto.ResponseComment;
import Muscle.comment.service.CommentService;
import Muscle.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private final CommentService commentService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createComment(@RequestBody RequestComment.CreateCommentDto createCommentDto, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        Long commentId = commentService.createComment(createCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment created successfully.")
                .data(commentId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllComment() {

        List<ResponseComment.GetAllCommentDto> response = commentService.getAllComment();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDto> getComment(@PathVariable("id") Long id) {

        ResponseComment.GetCommentDto response = commentService.getComment(id);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateComment(@RequestBody RequestComment.UpdateCommentDto updateCommentDto, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        commentService.updateComment(updateCommentDto, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        Optional<String> token = null;
        if (request != null) {
            token = jwtAuthTokenProvider.getAuthToken(request);
        }
        commentService.deleteComment(commentId, token);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Comment deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
