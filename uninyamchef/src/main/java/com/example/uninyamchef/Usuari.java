package com.example.uninyamchef;

//Definició de la classe usuari.
public class Usuari {

    private String Nom;
    private String Email;

    public Usuari() {
        //Constructor necessari per Firebase.
    }

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

    public Usuari(String nom, String email, int despesa) {
        Nom = nom;
        Email = email;
    }

}