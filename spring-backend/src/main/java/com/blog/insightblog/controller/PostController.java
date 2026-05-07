package com.blog.insightblog.controller;

import com.blog.insightblog.dto.PostDTO;
import com.blog.insightblog.dto.ApiResponse;
import com.blog.insightblog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    private static final String[] BAD_WORDS = {
            "madarchod", "madharchod", "bhosdike", "chutiya", "gaand"
    };

    @PostMapping
    public ResponseEntity<ApiResponse<PostDTO>> createPost(@Valid @RequestBody PostDTO postDTO) {

        String content = postDTO.getContent().toLowerCase();

        for (String word : BAD_WORDS) {
            if (content.contains(word)) {
                return ResponseEntity.status(400).body(
                        new ApiResponse<>(400, "Post rejected: Abusive content detected", null)
                );
            }
        }

        //  NOW SERVICE WILL HANDLE USER + NLP
        PostDTO savedPost = postService.createPost(postDTO);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Post created successfully", savedPost)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostDTO>>> getAllPosts(
            @RequestParam(required = false) Double sentiment,
            Pageable pageable) {

        Page<PostDTO> posts;

        if (sentiment != null) {
            posts = postService.getPostsBySentiment(sentiment, pageable);
        } else {
            posts = postService.getAllPosts(pageable);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Posts fetched successfully", posts)
        );
    }
}