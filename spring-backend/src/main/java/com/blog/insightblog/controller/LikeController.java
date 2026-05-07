
package com.blog.insightblog.controller;

import com.blog.insightblog.service.LikeService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // LIKE process by user
    @PostMapping("/{postId}")
    public String likePost(@PathVariable Long postId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return likeService.likePost(postId, username);
    }

    // UNLIKE process by user
    @DeleteMapping("/{postId}")
    public String unlikePost(@PathVariable Long postId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return likeService.unlikePost(postId, username);
    }

    // COUNT liked posts
    @GetMapping("/{postId}")
    public long countLikes(@PathVariable Long postId) {
        return likeService.countLikes(postId);
    }
}