package com.nakqeeb.posts_app.aspect;

import com.nakqeeb.posts_app.dao.LoginCounterRepository;
import com.nakqeeb.posts_app.entity.LoginCounter;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Aspect
@Component
public class LoggingInCounterAspect {
    // private static final Logger logger = LoggerFactory.getLogger(LoggingInCounterAspect.class);

    private  final LoginCounterRepository loginCounterRepository;

    @Autowired
    public LoggingInCounterAspect(LoginCounterRepository loginCounterRepository) {
        this.loginCounterRepository = loginCounterRepository;
    }

    // Define a pointcut for the login method
    @Pointcut("execution(* com.nakqeeb.posts_app.service.AuthenticationService.authenticate(..))")
    public void loginMethod() {}

    // Advice to execute when the login is successful
    @AfterReturning(pointcut = "loginMethod()", returning = "result")
    public void logSuccess(Object result) {
        // Prepare a response message with status
        // Map<String, Object> response = new HashMap<>();
        // response.put("status", HttpStatus.OK.value());
        // response.put("result", result);
        // Log success with the appropriate status code
        // logger.info("Login successful. Status code: {}", response);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        Optional<LoginCounter> loginCounter = loginCounterRepository.findByDate(formattedDate);
        if (loginCounter.isPresent()) {
            loginCounter.get().setSuccess(loginCounter.get().getSuccess() + 1);
            loginCounter.get().setTotal(loginCounter.get().getTotal() + 1);
            loginCounterRepository.save(loginCounter.get());
        } else {
            LoginCounter newloginCounter = new LoginCounter();
            newloginCounter.setDate(formattedDate);
            newloginCounter.setSuccess(1L);
            newloginCounter.setFailed(0L);
            newloginCounter.setTotal(1L);
            loginCounterRepository.save(newloginCounter);
        }
    }

    // Advice to execute if the login method throws an exception (i.e., failed login)
    @AfterThrowing(pointcut = "loginMethod()", throwing = "exception")
    public void logFailure(Exception exception) {
        // Prepare a response message with status
        // Map<String, Object> response = new HashMap<>();
        // response.put("message", exception.getMessage());
        // response.put("status", HttpStatus.UNAUTHORIZED.value());
        // Log failure with the appropriate status code
        // logger.error("Login failed. Status code: {}", response);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);
        Optional<LoginCounter> loginCounter = loginCounterRepository.findByDate(formattedDate);
        if (loginCounter.isPresent()) {
            loginCounter.get().setFailed(loginCounter.get().getFailed() + 1);
            loginCounter.get().setTotal(loginCounter.get().getTotal() + 1);
            loginCounterRepository.save(loginCounter.get());
        } else {
            LoginCounter newloginCounter = new LoginCounter();
            newloginCounter.setDate(formattedDate);
            newloginCounter.setSuccess(0L);
            newloginCounter.setFailed(1L);
            newloginCounter.setTotal(1L);
            loginCounterRepository.save(newloginCounter);
        }
    }
}
