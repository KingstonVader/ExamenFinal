package com.example.examenfinal;

import com.google.firebase.database.IgnoreExtraProperties;

public class HistorialRuta {

    public String nombreRuta;
    public String fecha;
    public long duracion;
    public String posicionInicial;
    public String posicionFinal;
    public double distanciaTotal;

    public HistorialRuta() {

    }

    public HistorialRuta(String nombreRuta,String fecha,long duracion, String posicionInicial,String posicionFinal,double distanciaTotal ){
        this.nombreRuta = nombreRuta;
        this.fecha = fecha;
        this.duracion = duracion;
        this.posicionInicial = posicionInicial;
        this.posicionFinal = posicionFinal;
        this.distanciaTotal = distanciaTotal;

    }
}

