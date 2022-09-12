package entity;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import springframework.beans.factory.annotation.Autowired;
import springframework.beans.factory.annotation.Qualifier;
import springframework.beans.factory.annotation.Value;
import springframework.context.annotation.Scope;
import springframework.stereotype.Component;

import java.util.List;

//@Data
@Component
public class User implements MethodTest {
    int age;
    @Value("woaini")
    @Autowired(required = false)
    String name;
    @Autowired
    @Qualifier("pet1")
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
