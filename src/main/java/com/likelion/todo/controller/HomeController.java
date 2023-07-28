package com.likelion.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/todo-list")
    public String home() {
        return "login";
    }
}
