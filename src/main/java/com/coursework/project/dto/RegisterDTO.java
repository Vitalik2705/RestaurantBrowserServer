package com.coursework.project.dto;

import com.coursework.project.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String name;
    private String surname;
    private String email;
    private String password;
    private Set<Role> roles;
}
