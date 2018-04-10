package com.famnet.famnet.Model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by DungNguyen on 11/21/17.
 */

public class Task implements Serializable {
    private String id;
    private String name;
    private String description;
    private String reward;
    private String deadline;
    private User taker;

    public Task(){
        this.id = UUID.randomUUID().toString();
    }

    public Task(String name, String description, String deadline){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    public Task(String name, String description, String reward, String deadline) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.reward = reward;
        this.deadline = deadline;
    }

    public Task(String name, String description, String reward, String deadline, User taker) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.reward = reward;
        this.deadline = deadline;
        this.taker = taker;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public User getTaker() {
        return taker;
    }

    public void setTaker(User taker) {
        this.taker = taker;
    }
}
