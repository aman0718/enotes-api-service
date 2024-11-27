package com.aman.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// This class is made only for client-side purpose. To show user only meaningful content.
public class CategoryResponse {

    private Integer id;
    private String name;
    private String description;

}
