package com.blog.insightblog.controller;

import com.blog.insightblog.model.Comment;
import com.blog.insightblog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // CREATE COMMENT by user
    @PostMapping("/{postId}")
    public Comment createComment(@PathVariable Long postId,
                                 @RequestBody String content) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return commentService.createComment(postId, username, content);
    }

    // GET COMMENTS by user
    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }
}