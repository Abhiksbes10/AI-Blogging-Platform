package com.blog.insightblog.dto;

import lombok.Data;

@Data
public class ModerationRequest {
    private String text;
    private String user_id;
}