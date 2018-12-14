package com.example.sergimacia.uninyam;

public class Comanda {
    private String hamburguesa;
    private String beguda;
    private String postres;
    private int codi;
    private double data;
    private int preu;
    private int estat;
    private String mida;


    public Comanda() {
        //public no-arg constructor needed for FireBase
    }

    public Comanda(String hamburguesa, String beguda, String postres, int codi, double data, int preu, int estat, String mida) {
        this.hamburguesa = hamburguesa;
        this.beguda = beguda;
        this.postres = postres;
        this.codi = codi;
        this.data = data;
        this.preu = preu;
        this.estat = estat;
        this.mida = mida;
    }
    public String getMida() {
        return mida;
    }

    public String getHamburguesa() {
        return hamburguesa;
    }

    public String getBeguda() {
        return beguda;
    }

    public String getPostres() {
        return postres;
    }

    public int getCodi() {
        return codi;
    }

    public double getData() {
        return data;
    }

    public int getPreu() {
        return preu;
    }

    public int getEstat() {
        return estat;
    }
}