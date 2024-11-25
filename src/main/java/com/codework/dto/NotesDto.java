package com.codework.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotesDto {

    private Integer id;
    private String title;
    private String description;
    private CategoryDto category;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private FileDto fileDetails;
    private Boolean isDeleted;
    private Date deletedOn;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDto {
        private Integer id;
        private String originalFileName;
        private String displayFileName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto {
        private Integer id;
        private String name;
    }
}
