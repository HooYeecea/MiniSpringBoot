package com.mvc.demo;

import com.mvc.validation.Max;
import com.mvc.validation.Min;
import com.mvc.validation.NotBlank;
import com.mvc.validation.NotNull;

/**
 * Request DTO for POST /api/users.
 */
public class CreateUserRequest {

    @NotNull
    @NotBlank
    private String name;

    @Min(1)
    @Max(150)
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
