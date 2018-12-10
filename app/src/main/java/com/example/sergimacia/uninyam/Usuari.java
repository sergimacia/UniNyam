package com.example.sergimacia.uninyam;

public class Usuari {
    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuari(String nom, String email, int id) {

        Nom = nom;
        Email = email;
        this.id = id;
    }

    private String Nom;
    private String Email;
    private int id;


}
