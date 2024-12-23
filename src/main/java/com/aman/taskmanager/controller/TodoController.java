package com.aman.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aman.taskmanager.dto.TodoDto;
import com.aman.taskmanager.service.ToDoService;
import com.aman.taskmanager.util.CommonUtil;

// @CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {

    @Autowired
    private ToDoService toDoService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> saveTask(@RequestBody TodoDto todoDto) throws Exception {

        Boolean saveTodo = toDoService.saveTodo(todoDto);
        if (saveTodo)
            return CommonUtil.createBuildResponse(saveTodo, HttpStatus.CREATED);
        return CommonUtil.createErrorResponseMessage("Task cannot be created", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getTaskById(@PathVariable Integer id) throws Exception {
        TodoDto todoDto = toDoService.getTodoDtoById(id);
        return CommonUtil.createBuildResponse(todoDto, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getAllTaskByUser() throws Exception {

        List<TodoDto> todoList = toDoService.getToDoByUser();
        if (CollectionUtils.isEmpty(todoList))
            return ResponseEntity.noContent().build();
        return CommonUtil.createBuildResponse(todoList, HttpStatus.CREATED);
    }
}
