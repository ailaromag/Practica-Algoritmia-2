package com.example.zenworld2;
/*
Classe que realitza la LECTURA del fitxer de PARAULES i INICIALITZA el
CATÀLEG de PARAULES VÀLIDES i el CATÀLEG de LONGITUDS.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Lectura {
        private InputStream in;
        private Set<Paraula> paraulesValides = new HashSet<>();
    private int numParaulesValides;
        private Map<Integer,HashSet<Paraula>> CatalegLongituds= new HashMap<>();
    private int [] numParaulesCatalegLongituds = new int[7];
    private int numLinies = 0;


    public Lectura(InputStream in) {
        this.in = in;
        this.numParaulesValides = 0;
        }

    public Set<Paraula> getParaulesValides() {
        return paraulesValides;
    }

    public Map<Integer, HashSet<Paraula>> getCatalegLongituds() {
        return CatalegLongituds;
    }

    // Mètode que llegeix totes les linees del fitxer, crea el catàleg de paraules vàlides
    // i el catàleg de solucions
    public void readLines() {
        //Inicialitzam el cataleg de longituds:
        inicialitzarCatalegLongituds();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line= reader.readLine();

            while (line != null) {
                numLinies++;
                Paraula pa = new Paraula(line);
                int llargura = pa.getLength();
                if(llargura<8){
                paraulesValides.add(pa); // Agregam la paraula al HashSet
                numParaulesValides++;
                //Afegim la paraula al HashSet corresponent amb la llargura de la paraula
                // del Map CatalegLongituds
                HashSet<Paraula> llista = CatalegLongituds.get(llargura-1);
                // Añadir la palabra a la lista
                if (llista != null) {
                    llista.add(pa);
                    numParaulesCatalegLongituds[llargura-1]++;
                }}
                line= reader.readLine();

            }
        //    Log.d("S'han llegit:", " "+numLinies + " paraules" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicialitzarCatalegLongituds(){
        //Inicialitzam CATÀLEG LONGITUDS amb llistes buides:
        for(int i = 0; i< 7; i++){
            CatalegLongituds.put(i,new HashSet<Paraula>());
        }
    }

    public int[] getNumParaulesCatalegLongituds() {
        return numParaulesCatalegLongituds;
    }

    public int getNumParaulesValides(){
        return numParaulesValides;
    }

    }

