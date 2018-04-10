package com.famnet.famnet.Model;

import java.util.UUID;

/**
 * Created by DungNguyen on 11/21/17.
 */

public class Family {
    private UUID id;
    private String name;
    private User[] memebers;

    public Family() {
        this.id = UUID.randomUUID();
    }

    public Family(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User[] getMemebers() {
        return memebers;
    }

    public void setMemebers(User[] memebers) {
        this.memebers = memebers;
    }
}
