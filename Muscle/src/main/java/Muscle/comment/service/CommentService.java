package Muscle.comment.service;


import Muscle.auth.repository.AuthRepository;
import Muscle.auth.security.JwtAuthToken;
import Muscle.auth.security.JwtAuthTokenProvider;
import Muscle.comment.dto.RequestComment;
import Muscle.comment.dto.ResponseComment;
import Muscle.comment.entity.Comment;
import Muscle.comment.repository.CommentRepository;
import Muscle.post.entity.Post;
import Muscle.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;

    public Long createComment(RequestComment.CreateCommentDto createCommentDto, Optional<String> token) {

        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long commentWriterId = authRepository.findByMuscleId(muscleId).getId();
        Post post = postRepository.findById(createCommentDto.getPostId()).get();
        if(post == null) {
            throw new EntityNotFoundException();
        }
        Comment comment = RequestComment.CreateCommentDto.toEntity(createCommentDto, commentWriterId, post);
        commentRepository.save(comment);
        post.addComment(comment);
        post.increasePostCommentCount();
        return comment.getCommentId();
    }

    public List<ResponseComment.GetAllCommentDto> getAllComment() {
        List<Comment> entityList = commentRepository.findAll();
        List<ResponseComment.GetAllCommentDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(comment -> dtoList.add(ResponseComment.GetAllCommentDto.toDto(comment)));
        return dtoList;
    }

    public ResponseComment.GetCommentDto getComment(Long id) {
        Comment comment = commentRepository.findById(id).get();
        return ResponseComment.GetCommentDto.toDto(comment);
    }

    public void updateComment(RequestComment.UpdateCommentDto updateCommentDto, Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long commentWriterId = authRepository.findByMuscleId(muscleId).getId();
        Comment originalComment = commentRepository.findById(updateCommentDto.getCommentId()).get();
        if(Objects.equals(commentWriterId, originalComment.getCommentWriterId())) {
            Comment updatedComment = RequestComment.UpdateCommentDto.toEntity(originalComment, updateCommentDto);
            commentRepository.save(updatedComment);
        } else {
            throw new IllegalArgumentException("Isn't your comment.");
        }

    }

    public void deleteComment(Long commentId, Optional<String> token) {
        String muscleId = null;
        if(token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            muscleId = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByMuscleId(muscleId).getId();
        Comment comment = commentRepository.findById(commentId).get();
        Long postWriterId = comment.getPost().getWriterId();

        if(Objects.equals(userId, comment.getCommentWriterId()) || Objects.equals(userId, postWriterId)) {
            commentRepository.delete(comment);
        } else {
            throw new IllegalArgumentException("Isn't your comment or post.");
        }
    }




}
