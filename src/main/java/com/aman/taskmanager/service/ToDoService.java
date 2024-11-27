package com.aman.taskmanager.service;

import java.util.List;

import com.aman.taskmanager.dto.TodoDto;

public interface ToDoService {

    public Boolean saveTodo(TodoDto todo) throws Exception;

    public TodoDto getTodoDtoById(Integer id) throws Exception;

    public List<TodoDto> getToDoByUser();
}
