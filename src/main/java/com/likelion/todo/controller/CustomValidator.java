package com.likelion.todo.controller;

import com.likelion.todo.entity.Todo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Todo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Todo todo = (Todo) target;

        //검증 로직
        if(!StringUtils.hasText(todo.getContent())) {
            errors.rejectValue("content", "required");
        }

        //TODO : username, password, email not null 검증
    }
}
