package com.blog.insightblog.repository;

import com.blog.insightblog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Post> findBySentimentBetween(Double min, Double max, Pageable pageable);

    //  ANALYTICS
    long countBySentimentGreaterThan(double value);
    long countBySentimentLessThan(double value);
}