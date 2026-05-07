package com.blog.insightblog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationResponse {

    private String status;
    private String message;

    @JsonProperty("cleaned_text")
    private String cleanedText;

    private Double score;
    private Integer strikes;

    private Double sentiment;
    private List<String> keywords;
}