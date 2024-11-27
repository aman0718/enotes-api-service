package com.aman.taskmanager.enums;

public enum TodoStatus {

    Not_STARTED(1, "Not Started"),
    IN_PROGRESS(2, "In Progress"),
    COMPLETED(3, "Completed");

    private Integer id;
    private String status;

    TodoStatus(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    // Getter for id
    public Integer getId() {
        return id;
    }

    // Setter for id
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for name
    public void setStatus(String status) {
        this.status = status;
    }

}
