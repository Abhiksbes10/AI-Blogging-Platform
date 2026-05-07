package com.blog.insightblog.service;

import com.blog.insightblog.dto.PostDTO;
import com.blog.insightblog.dto.ModerationResponse;
import com.blog.insightblog.model.Post;
import com.blog.insightblog.model.User;
import com.blog.insightblog.repository.PostRepository;
import com.blog.insightblog.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       RestTemplate restTemplate) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    //  CREATE POST (FIXED USER FROM JWT)
    public PostDTO createPost(PostDTO dto) {

        //  GET USER FROM SECURITY CONTEXT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        String url = "http://127.0.0.1:8000/analyze";

        ModerationResponse res = null;

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("text", dto.getContent());
            request.put("user_id", username);

            res = restTemplate.postForObject(
                    url,
                    request,
                    ModerationResponse.class
            );

        } catch (Exception e) {
            System.out.println("⚠ NLP API FAILED: " + e.getMessage());
        }

        if (res == null) {
            res = new ModerationResponse();
            res.setStatus("SAFE");
            res.setCleanedText(dto.getContent());
            res.setSentiment(0.0);
            res.setKeywords(new ArrayList<>());
        }

        if ("BLOCKED".equalsIgnoreCase(res.getStatus())) {
            throw new RuntimeException("Post rejected: Toxic content");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(res.getCleanedText());
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setSentiment(res.getSentiment());

        if (res.getKeywords() != null) {
            post.setKeywords(String.join(",", res.getKeywords()));
        }

        Post saved = postRepository.save(post);

        return convertToDTO(saved);
    }

    public Page<PostDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public Page<PostDTO> getPostsBySentiment(Double sentiment, Pageable pageable) {

        Page<Post> page = postRepository.findAll(pageable);

        List<PostDTO> filtered = page.getContent().stream()
                .map(this::convertToDTO)
                .filter(dto -> dto.getSentiment() != null && dto.getSentiment() >= sentiment)
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    private PostDTO convertToDTO(Post post) {

        PostDTO dto = new PostDTO();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setSentiment(post.getSentiment());

        if (post.getUser() != null) {
            dto.setUsername(post.getUser().getUsername());
        }

        if (post.getKeywords() != null && !post.getKeywords().isEmpty()) {
            dto.setKeywords(Arrays.asList(post.getKeywords().split(",")));
        } else {
            dto.setKeywords(new ArrayList<>());
        }

        return dto;
    }
}