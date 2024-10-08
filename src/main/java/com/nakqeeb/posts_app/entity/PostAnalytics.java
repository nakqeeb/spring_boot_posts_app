package com.nakqeeb.posts_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "post_analytics")
@Data
public class PostAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @Column(name = "views_count", nullable = false)
    private int viewsCount = 0;

    @Column(name = "likes_count", nullable = false)
    private int likesCount = 0;

    // Method to update likes count
    public void updateLikesCount(int newCount) {
        this.likesCount = newCount;
    }
}