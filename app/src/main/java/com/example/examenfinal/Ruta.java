package com.example.examenfinal;

public class Ruta {
    private String nombre;
    private String fecha;
    private String coordenadasIniciales;
    private String coordenadasFinales;

    public Ruta(String nombreRuta, String fechaRuta, String coordenadasIniciales, String coordenadasFinales, String distancia, String duracion) {
    }

    public Ruta(String nombre, String fecha, String coordenadasIniciales, String coordenadasFinales) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.coordenadasIniciales = coordenadasIniciales;
        this.coordenadasFinales = coordenadasFinales;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCoordenadasIniciales() {
        return coordenadasIniciales;
    }

    public void setCoordenadasIniciales(String coordenadasIniciales) {
        this.coordenadasIniciales = coordenadasIniciales;
    }

    public String getCoordenadasFinales() {
        return coordenadasFinales;
    }

    public void setCoordenadasFinales(String coordenadasFinales) {
        this.coordenadasFinales = coordenadasFinales;
    }
}