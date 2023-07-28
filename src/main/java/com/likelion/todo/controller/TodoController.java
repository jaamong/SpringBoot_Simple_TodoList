package com.likelion.todo.controller;

import com.likelion.todo.service.TodoService;
import com.likelion.todo.dto.TodoSaveDto;
import com.likelion.todo.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/home")
    public String homeView(Model model) {

        model.addAttribute("todo", new TodoDto());
        model.addAttribute("todos", todoService.findAll());

        return "todo";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute("todo") TodoSaveDto form,
                         BindingResult bindingResult,
                         Principal principal) {

        //검증 실패 시 다시 첫 화면으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "todo";
        }

        //성공 로직
        TodoDto todo = todoService.createTodo(form, principal.getName());
        log.info("todoDto : {}", todo);

        return "redirect:/todo/home";
    }

    @PutMapping("/{id}/done")
    public String updateDone(@PathVariable("id") Long id) {
        todoService.updateTodoDone(id);
        return "redirect:/todo/home";
    }

    @PutMapping("/{id}/content")
    public String updateContent(@PathVariable("id") Long id,
                                @Validated @ModelAttribute("todoDto") TodoSaveDto form,
                                BindingResult bindingResult) {

        //검증 실패 시 다시 첫 화면으로
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "todo";
        }

        //성공 로직
        todoService.updateTodoContent(id, form.getContent());
        return "redirect:/todo/home";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
        return "redirect:/todo/home";
    }
}
