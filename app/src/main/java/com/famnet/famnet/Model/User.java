package com.famnet.famnet.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by DungNguyen on 11/21/17.
 */

public class User {
    private String id;
    private String name;
    private Family family;
    private String email;
    private List<Task> tasks;

    public User() {
    }

    public User(String id, String name, Family family, String email) {
        this.id = id;
        this.name = name;
        this.family = family;
        this.email = email;
        tasks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
