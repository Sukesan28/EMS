package com.example.ems.dto.response;

public class DepartmentNameResponseDTO {
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DepartmentNameResponseDTO() {
    }

    public DepartmentNameResponseDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
