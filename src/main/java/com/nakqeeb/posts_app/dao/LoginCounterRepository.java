package com.nakqeeb.posts_app.dao;

import com.nakqeeb.posts_app.entity.LoginCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface LoginCounterRepository extends JpaRepository<LoginCounter, Long> {
    Optional<LoginCounter> findByDate(String date);
}
