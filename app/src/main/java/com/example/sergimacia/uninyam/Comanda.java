package com.example.sergimacia.uninyam;

public class Comanda {
    private String title;
    private String description;

    public Comanda(){
        //public no-arg constructor needed for FireBase
    }
    public Comanda(String title, String description){
        this.title = title;
        this.description = description;

    }
    public String getTitle() {
        return title;
    }

    public String getDescription(){
        return description;
    }
}
