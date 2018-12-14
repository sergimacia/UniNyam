package com.example.sergimacia.uninyam;

public class Usuari {

    private String Nom;
    private String Email;
    private int despesa;

    public Usuari() {
        //public no-arg constructor needed for FireBase
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

    public int getPreu() {
        return despesa;
    }

    public void setPreu(int despesa) {
        this.despesa = despesa;
    }

    public Usuari(String nom, String email, int despesa) {
        Nom = nom;
        Email = email;
        this.despesa = despesa;
    }

}
