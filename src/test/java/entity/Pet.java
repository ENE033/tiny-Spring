package entity;

import springframework.beans.factory.annotation.Autowired;
import springframework.stereotype.Component;

@Component
public class Pet {

    String name;
    int age;

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

    public Pet() {
    }

    public Pet(String name, int age) {
        this.name = name;
        this.age = age;
    }

//    @Override
//    public String toString() {
//        return "Pet{" +
//                "name='" + name + '\'' +
//                ", age=" + age +
//                '}';
//    }
}
