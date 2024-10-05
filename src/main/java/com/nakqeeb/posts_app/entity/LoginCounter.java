package com.nakqeeb.posts_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "login_counter")
@Data
public class LoginCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", unique = true, nullable = false)
    private String date;

    @Column(name = "success")
    private Long success;

    @Column(name = "failed")
    private Long failed;

    @Column(name = "total")
    private Long total;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
