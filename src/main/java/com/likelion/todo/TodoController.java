package com.likelion.todo;

import com.likelion.todo.model.TodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/home")
    public String createView(Model model) {
        model.addAttribute("todos", todoService.findAll());
        return "todo";
    }

    @PostMapping("/create")
    public String create(@RequestParam("content") String content) {
        TodoDto todoDto = todoService.createTodo(content);

        System.out.println("todoDto = " + todoDto);

        return "redirect:/todo/home";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id) {
        todoService.updateTodoDone(id);
        return "redirect:/todo/home";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
        return "redirect:/todo/home";
    }
}
