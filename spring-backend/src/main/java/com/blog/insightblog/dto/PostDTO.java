package com.blog.insightblog.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {

    private Long id;

    private String title;

    private String content;

    private String username;

    private LocalDateTime createdAt;

    private Double sentiment;

    //  FIX: changed from String → List<String>
    private List<String> keywords;
}