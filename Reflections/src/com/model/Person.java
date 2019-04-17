package com.model;

import com.annotation.PrimaryKey;
import com.annotation.Column;

// javaBean classic
public class Person {
    @PrimaryKey
    private long id;

    @Column
    private String name;

    @Column
    private int age;

    // public Person() {}

    public Person(String name, int age) {

        this.name = name;
        this.age = age;
    }

    public static Person of(String name, int age) {
        return new Person(name, age);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }
}
