package com.jaamong.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/todo-list")
    public String loginHome() {
        return "login";
    }

    @GetMapping("/todo-home")
    public String todoHome() {
        return "todo";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
}
