package com.aman.taskmanager.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.aman.taskmanager.dto.TodoDto;
import com.aman.taskmanager.dto.TodoDto.StatusDto;
import com.aman.taskmanager.entity.Todo;
import com.aman.taskmanager.enums.TodoStatus;
import com.aman.taskmanager.exception.ResourceNotFoundException;
import com.aman.taskmanager.repository.TodoRepository;
import com.aman.taskmanager.service.ToDoService;
import com.aman.taskmanager.util.Validation;

@Service
public class TodoServiceImpl implements ToDoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Validation validation;

    @Override
    public Boolean saveTodo(TodoDto todoDto) throws Exception {

        // Validate todo Status
        validation.todoValidation(todoDto);

        Todo todo = mapper.map(todoDto, Todo.class);

        todo.setStatusId(todoDto.getStatus().getId());

        Todo saveTodo = todoRepository.save(todo);

        if (!ObjectUtils.isEmpty(saveTodo)) {
            return true;
        }
        return false;
    }

    @Override
    public TodoDto getTodoDtoById(Integer id) throws Exception {

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TodoDto todoDto = mapper.map(todo, TodoDto.class);
        setStatus(todoDto, todo);
        return todoDto;
    }

    private void setStatus(TodoDto todoDto, Todo todo) {
        for (TodoStatus st : TodoStatus.values()) {
            if (st.getId().equals(todo.getStatusId())) {

                StatusDto statusDto = StatusDto.builder()
                        .id(st.getId())
                        .name(st.getStatus())
                        .build();
                todoDto.setStatus(statusDto);
            }
        }
    }

    @Override
    public List<TodoDto> getToDoByUser() {

        Integer userId = 2;

        List<Todo> tasks = todoRepository.findByCreatedBy(userId);
        return tasks.stream().map(td -> {
            
            TodoDto todoDto = mapper.map(td, TodoDto.class);
            setStatus(todoDto, td);
            return todoDto;
        })
        .toList();
    }
}
