package entity;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

//@Data
public class User implements MethodTest {
    int age;
    String name;
    Pet pet;


    public User() {
    }

    public User(int age) {
        this.age = age;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, String vice) {
        this.name = name;
        System.out.println(vice);
    }


    public User(String name, Integer age, Pet pet) {
        this.name = name;
        this.age = age;
        this.pet = pet;
    }

    public void read() {
        System.out.println("hello");
    }
}
