package entity;

import lombok.Data;

public class User {
    int age;

    public User() {
    }

    public User(int age) {
        this.age = age;
    }

    public void read() {
        System.out.println("hello");
    }
}
