package com.nakqeeb.posts_app.dao;

import com.nakqeeb.posts_app.entity.Role;
import com.nakqeeb.posts_app.entity.RoleEnum;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Hidden
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}