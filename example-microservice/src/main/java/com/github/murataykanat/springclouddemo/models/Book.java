package com.github.murataykanat.springclouddemo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Book implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("author")
    private String author;
    @JsonProperty("message")
    private String message;

    public Book(){}
    public Book(String id, String name, String author){
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
