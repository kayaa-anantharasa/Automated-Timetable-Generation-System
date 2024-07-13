package com.example.automatedtimetablegenerationsystem;

public class prerequisitemodel {
    String Id,Name;
    public prerequisitemodel() {
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

    public prerequisitemodel(String id, String name) {
        Id = id;
        Name = name;
    }

}
