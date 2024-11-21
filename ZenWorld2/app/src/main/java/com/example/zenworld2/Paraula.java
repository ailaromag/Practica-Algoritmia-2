package com.example.zenworld2;

/*
 Classe que definex l'objecte Paraula
 */

import java.util.Objects;

public class Paraula implements Comparable {
    String paraulaNoAccent;
    String paraulaAccent;
    char [] lletresParaula;
    int length;
    String [] parts;
    int fila;
    final String delimiter = ";";
    boolean primeraLletra = false;

    public Paraula(){

    }
    public Paraula(String linea){
        dividirParaules(linea,delimiter);
        paraulaNoAccent = parts[1];
        paraulaAccent = parts[0 ];
        lletresParaula = paraulaNoAccent.toCharArray();
        length = lletresParaula.length;

    }

    public Paraula paraulaSola(String linea){
    Paraula pa = new Paraula();
        pa.paraulaNoAccent=    linea;
        pa.length = linea.length();
        return pa;
    }
    @Override
    public int hashCode() {
        return Objects.hash(paraulaNoAccent);
    }
    public void setFila(int fila){
    this.fila = fila;
    }

    public int getLength(){
        return length;
    }

    public void dividirParaules(String input, String delimiter) {
      //  Log.d("input"," "+input );
        parts = input.split(delimiter);
       // Log.d("parts[0]"," " +parts[0]);
       // Log.d("parts[1]"," " +parts[1]);

    }

    public String toString(){
        return paraulaNoAccent;
    }

    public char[] getLletresParaula() {
        return lletresParaula;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paraula paraula = (Paraula) o;
        return paraulaNoAccent.equals(o.toString());
    }

    // Mètode que serveix per posar a true la utilització de la primera lletra
    public void setPrimeraLletra(){
    this.primeraLletra = true;
    }
    public boolean getPrimeraLletra(){
    return primeraLletra;
    }


    @Override
    public int compareTo(Object o) {
    Paraula altrePa = (Paraula)o;
        return this.paraulaNoAccent.compareTo(o.toString());
    }


}
