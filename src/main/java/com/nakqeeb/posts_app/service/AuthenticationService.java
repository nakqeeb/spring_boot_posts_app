package com.nakqeeb.posts_app.service;

import com.nakqeeb.posts_app.dao.RoleRepository;
import com.nakqeeb.posts_app.dao.UserRepository;
import com.nakqeeb.posts_app.dto.LoginUserDto;
import com.nakqeeb.posts_app.dto.RegisterUserDto;
import com.nakqeeb.posts_app.entity.Role;
import com.nakqeeb.posts_app.entity.RoleEnum;
import com.nakqeeb.posts_app.entity.User;
import com.nakqeeb.posts_app.exception.InvalidCredentialException;
import com.nakqeeb.posts_app.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) throws UserAlreadyExistsException {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);

        if (optionalRole.isEmpty()) {
            return null;
        }
        Optional<User> existUser = this.userRepository.findByEmail(input.getEmail());
        if (existUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + existUser.get().getEmail() + " already exists.");
        }
        var user = new User();
                user.setName(input.getName());
                user.setEmail(input.getEmail());
                user.setPassword(passwordEncoder.encode(input.getPassword()));
                user.setRole(optionalRole.get());
                user.setActivated(false);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) throws Exception {

        Optional<User> user = this.userRepository.findByEmail(input.getEmail());

        if (user.isEmpty()) {
            throw new InvalidCredentialException("Invalid Credentials");
        } else if (!user.get().isActivated()) {
            throw new Exception("Your account hasn't been activated yet");
        }
//        if (!this.userService.isUserExist(input.getEmail())) {
//            throw new InvalidCredentialException("Invalid Credentials");
//        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return this.userRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}