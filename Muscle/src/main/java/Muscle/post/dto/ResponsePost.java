package Muscle.post.dto;

import Muscle.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResponsePost {

    @Getter
    @Builder
    public static class GetPostDto {
        private Long writerId;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalTime postDate;
        private Long postLikeCount;
        private Long postCommentCount;
        private Boolean isPostLiked;
        private Boolean isPostSaved;
//        private List<CommentListDto> commentList;


        public static GetPostDto toDto(Post post, Boolean isPostLiked, Boolean isPostSaved) {

//            List<CommentListDto> menuList = new ArrayList<>();
//            if(!post.getCommentList().isEmpty())
//                post.getCommentList().stream().forEach(comment -> commentList.add(CommentListDto.toDto(comment)));


            return GetPostDto.builder()
                    .writerId(post.getWriterId())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .postLikeCount(post.getPostLikeCount())
                    .postCommentCount(post.getPostCommentCount())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllPostDto {
        private Long writerId;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalTime postDate;
        private Long postLikeCount;
        private Long postCommentCount;
        private Boolean isPostLiked;
        private Boolean isPostSaved;


        public static GetAllPostDto toDto(Post post, Boolean isPostLiked, Boolean isPostSaved) {

            return GetAllPostDto.builder()
                    .writerId(post.getWriterId())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .postLikeCount(post.getPostLikeCount())
                    .postCommentCount(post.getPostCommentCount())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetSavedPostDto {
        private Long writerId;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalTime postDate;
        private Long postLikeCount;
        private Long postCommentCount;
        private Boolean isPostLiked;
        private Boolean isPostSaved;

        public static GetSavedPostDto toDto(Post post, Boolean isPostLiked, Boolean isPostSaved) {

            return GetSavedPostDto.builder()
                    .writerId(post.getWriterId())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .postLikeCount(post.getPostLikeCount())
                    .postCommentCount(post.getPostCommentCount())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class GetByBoardDto {
        private Long writerId;
        private Long postId;
        private String title;
        private String content;
        private String board;
        private LocalTime postDate;
        private Long postLikeCount;
        private Long postCommentCount;
        private Boolean isPostLiked;
        private Boolean isPostSaved;


        public static GetByBoardDto toDto(Post post, Boolean isPostLiked, Boolean isPostSaved) {

            return GetByBoardDto.builder()
                    .writerId(post.getWriterId())
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .board(post.getBoard())
                    .postDate(post.getPostDate())
                    .postLikeCount(post.getPostLikeCount())
                    .postCommentCount(post.getPostCommentCount())
                    .isPostLiked(isPostLiked)
                    .isPostSaved(isPostSaved)
                    .build();
        }
    }

}
