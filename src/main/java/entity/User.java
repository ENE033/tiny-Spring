package entity;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class User {
    int age;
    String name;

    public User() {
    }

    public User(int age) {
        this.age = age;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

//    public User(String name, Integer age) {
//        this.name = name;
//        this.age = age;
//    }

    public void read() {
        System.out.println("hello");
    }
}
