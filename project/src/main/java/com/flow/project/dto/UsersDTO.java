package com.flow.project.dto;

import lombok.Data;

@Data
public class UsersDTO {
    
    private int no;
    private String id;
    private String name;
    private int role;
    private String pwd;
    private String created_date;

}
