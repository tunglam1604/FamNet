package com.famnet.famnet.Model;

import java.util.List;

/**
 * Created by DungNguyen on 11/21/17.
 */

public class Board {
    private List<Task> tasks;

    public Board() {
    }

    public Board(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
