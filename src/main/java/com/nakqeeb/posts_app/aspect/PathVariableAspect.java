package com.nakqeeb.posts_app.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Parameter;

@Aspect
@Component
public class PathVariableAspect {

    @Before("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void checkPathVariableId(JoinPoint joinPoint) throws ResourceNotFoundException {
        System.out.println("====> Executing checkPathVariableId method");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        // Loop through all parameters and check if they are annotated with @PathVariable
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                Object pathVariableValue = args[i];

                // Check if the value is a valid number
                if (!isNumeric(pathVariableValue.toString())) {
                    throw new ResourceNotFoundException("PathVariable '" + pathVariableValue + "' is not a valid id.");
                }
            }
        }
    }

    // Helper method to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
