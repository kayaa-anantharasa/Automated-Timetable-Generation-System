package com.example.automatedtimetablegenerationsystem;

public class programModel {
    String Id,Name;

    public programModel(String id, String name) {
        Id = id;
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public programModel() {
    }
}
