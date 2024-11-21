package com.example.zenworld2;

/* JOC DE ZENWORLD */
// Fet per Aila Romaguera Mezquida, DNI:  45189101N
// i Alba Aguilera Cabellos, DNI: 45612336R


import static android.graphics.Color.WHITE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.text.Html;


import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* COMPONENTS INTERFICIE GRÀFICA  */
    private ImageView mShuffle;
    private ImageView mHelp;
    private TextView mScoreNumber;
    private ImageButton mBonus;
    private ImageButton mReiniciar;
    private TextView mParaulesConstruides;
    private TextView[][] filesTextviews;
    private Button mLletra1, mLletra2, mLletra3, mLletra4, mLletra5, mLletra6, mLletra7;
    int startMargin; // Ajustar este valor para mover el primer TextView más a la derecha
    int widthDisplay;
    int heightDisplay;
    int lletraSize = 120;
    private Button mClear;
    private Button mSend;
    private TextView mParaula;
    private int color = Color.parseColor("#F97DA6"); //Color Tema Interfície


    /* COMPONENTS DEL JOC */
    private int wordLength;                 //Tamany màxim de la paraula
    private String message = "";             //Text del textView de la part superior del joc
    private Paraula paraulaGuanyadora;      //Paraula Guanyadora
    private String paraulaSolucio;          //String de la paraula solucio (guanyadora)
    private int[] botonsOff;               //Array que indicarà si els botons estan activats o no
    private Lectura lecLinea;          //Clase de lectura de l'arxiu de paraules vàlides
    private String paraulaEntrada = "";     //Contingut de la paraula d'entrada
    private char[] lletresDisponibles;      //Paraules disponibles a partir de les lletres de la
    //paraula guanyadora
    private boolean[] posicionsParaulesTrobades = new boolean[5]; //Array que indica si s'ha ocupat el textview de la posicio corresponent
    int bonus = 0;                           //Cada 5 bonus l'usuari té una pista
    int solucionsPosibles = 0;               //Nombre de solucions possibles
    int numParaulesTrobades = 0;             //Nombre de paraules trobades
    int numPrimeresLletres = 0;              //Nombre de primeres lletres inserides


    /*  CATÀLEGS  */
    private Map<Integer, HashSet<Paraula>> CatalegLongituds = new HashMap<>();    //Emmagatzema les paraules classificades
    //per longitud, (p.e. la posició 6 és per la longitud de paraula 7)
    private Map<Integer, HashSet<Paraula>> CatalegSolucions;                   //Emmagatzema les solucions posibles amb
    // la paraulaGuanyadora de distintes longituds
    private int[] numParaulesCatalegSolucions = new int[7];                    //Array que indica la quantitat de paraules solució
    // que hi ha de longitud corresponent
    private Set<Paraula> paraulesOcultes;                                       //Emmagatzema les paraules ocultes de la partida
    private Paraula[] paraulesOcultesArray;                                    //Array que emmagatzemarà les paraules ocultes de
    //manera ordenada
    private int numParaulesOcultes = 0;                                         //Nombre de paraules ocultes
    private Set<Paraula> paraulesTrobades;                                      //Emmagatzema les paraules trobades per l'usuari
    private Random random = new Random();                                       //Instància de la classe random


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        initJoc();
        initPartida();
    }

    /*
     Mètode que INICIALITZA la part GRÀFICA i els ATRIBUTS que NO ES MODIFIQUEN al llarg de l'execució.
     S'executa una sola vegada a l'inicia de l'execució.
     */
    public void initJoc() {
        //Determinam les dimensions del Display
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        mLletra1 = (Button) findViewById(R.id.lletra1);
        mLletra2 = (Button) findViewById(R.id.lletra2);
        mLletra3 = (Button) findViewById(R.id.lletra3);
        mLletra4 = (Button) findViewById(R.id.lletra4);
        mLletra5 = (Button) findViewById(R.id.lletra5);
        mLletra6 = (Button) findViewById(R.id.lletra6);
        mLletra7 = (Button) findViewById(R.id.lletra7);
        mLletra5.setVisibility(View.GONE);
        mLletra6.setVisibility(View.GONE);
        mLletra7.setVisibility(View.GONE);

        mClear = (Button) findViewById(R.id.clear);
        mSend = (Button) findViewById(R.id.send);
        mParaula = (TextView) findViewById(R.id.paraula);
        mShuffle = (ImageView) findViewById(R.id.shuffle);
        mHelp = (ImageView) findViewById(R.id.help);
        mScoreNumber = (TextView) findViewById(R.id.scoreNumber);
        mBonus = (ImageButton) findViewById(R.id.bonus);
        mReiniciar = (ImageButton) findViewById(R.id.reiniciar);
        mParaulesConstruides = (TextView) findViewById(R.id.paraulesConstruides);

        //Afegim els listeners
        mLletra1.setOnClickListener(this);
        mLletra2.setOnClickListener(this);
        mLletra3.setOnClickListener(this);
        mLletra4.setOnClickListener(this);
        mLletra5.setOnClickListener(this);
        mLletra6.setOnClickListener(this);
        mLletra7.setOnClickListener(this);
        resetColorsLletres();

        mClear.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mParaula.setOnClickListener(this);
        mShuffle.setOnClickListener(this);
        mHelp.setOnClickListener(this);
        mBonus.setOnClickListener(this);
        mReiniciar.setOnClickListener(this);

        //Inicalitzam els catàlegs del joc
        inicialitzarCatalegs();
    }

    /*
     Mètode que INICIALITZA el cataleg de PARAULESVALIDES i el cataleg CATALEGLONGITUDS
     a partir del fitxer de paraules. Només s'executa una vegada, ja aquests catalegs
     no es modifiquen.
     */
    public void inicialitzarCatalegs() {

        //Inicialitzam el  CATÀLEG de paraulesValides
        InputStream inputStream = getResources().openRawResource(R.raw.paraules2);
        lecLinea = new Lectura(inputStream);
        //Cridam al mètode que llegueix totes les línies del fitxer i les va afegint
        //una a una al catàleg de paraulesValides i al catàleg de CatalegLongituds
        lecLinea.readLines();
        //Inicialitzam el cataleg de longituds creat a mesura que s'han llegit dels paraules
        CatalegLongituds = lecLinea.getCatalegLongituds();
    }

    /*
    Mètode que INICIALITZA la PARTIDA
     */
    public void initPartida() {
        //1er Determinam el WordLength:
        wordLength = determinarWordLength();
        if (wordLength > 7) {
            wordLength = 7;
        }
        //Inicialitzam botonsOff:
        botonsOff = new int[wordLength];
        resetBotonsOff();
        resetPosicionsParaulesTrobades();

        //Inicialitzam el CATALEG SOLUCIONS de la partida
        CatalegSolucions = new HashMap<>();

        //Elegim la PARAULA SOLUCIÓ del CatalegLongituds amb el wordLength correcte:
        paraulaGuanyadora = getParaulaSolucio(wordLength);
        paraulaSolucio = paraulaGuanyadora.toString();
        Log.d("PARAULA GUANYADORA:", paraulaSolucio);
        //Cream el catàleg de paraules Solució:
        crearCatalegSolucions(paraulaSolucio);

        //Seleccionam les paraules ocultes
        seleccioParaulesOcultes();

        //Actualitzam l'array de lletres disponibles
        lletresDisponibles = new char[wordLength];
        for (int i = 0; i < wordLength; i++) {
            lletresDisponibles[i] = paraulaGuanyadora.getLletresParaula()[i];
        }

        //Inicialitzam les paraules trobades:
        paraulesTrobades = new TreeSet<>();

        filesTextviews = new TextView[paraulesOcultesArray.length][wordLength];
        //Cream les files de text views buides
        crearFilesTextViews();
        if(wordLength>=5){
            mLletra5.setVisibility(View.VISIBLE); // el mostra
        }
        if (wordLength >= 6) {
            mLletra6.setVisibility(View.VISIBLE); // el mostra
        }
        if (wordLength == 7) {
            mLletra7.setVisibility(View.VISIBLE); // el mostra
        }

        mParaulesConstruides.setText(" Has encertat " + numParaulesTrobades + " de " + solucionsPosibles + " : ");
        mParaula.setText("");
        mScoreNumber.setText("0");
        setLletraBotons();
    }


    //Mètode que gestiona quan s'apreta un botó LLETRA del CERCLE, l'escriu
    // al TextView corresponent i desactiva el botó.
    public void setLletra(View view) {
        //Hem de reconèixer quin botó ha estat
        int id = view.getId();
        if (view instanceof Button) {
            Button btn = (Button) view;
            String lletra = btn.getText().toString();

            //Ara hem de desactivar el botó trepitjat
            //si no està ja desactivat
            if ((id == R.id.lletra1) && (botonsOff[0] != 0)) {
                //Hem d'escriure la lletra en paraula:
                botonsOff[0] = 0;
                escriureLletra((String) btn.getText());
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra2) && (botonsOff[1] != 0) && (wordLength > 1)) {
                escriureLletra((String) btn.getText());
                botonsOff[1] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra3) && (botonsOff[2] != 0) && (wordLength > 2)) {
                escriureLletra((String) btn.getText());
                botonsOff[2] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra4) && (botonsOff[3] != 0) && (wordLength > 3)) {
                escriureLletra((String) btn.getText());
                botonsOff[3] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra5) && (botonsOff[4] != 0) && (wordLength > 4)) {
                escriureLletra((String) btn.getText());
                botonsOff[4] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra6) && (botonsOff[5] != 0) && (wordLength > 5)) {
                escriureLletra((String) btn.getText());
                botonsOff[5] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
            if ((id == R.id.lletra7) && (botonsOff[6] != 0) && (wordLength > 6)) {
                escriureLletra((String) btn.getText());
                botonsOff[6] = 0;
                btn.setTextColor(Color.parseColor("#808080"));
            }
        }
    }

    /*
     Mètode que SELECCIONA i retorna una PARAULA ALEATORIA a partir d'un wordlength
     passat per paràmetre.
     */
    public Paraula getParaulaSolucio(int wordLength) {
        Log.d("wordlength", wordLength + " ");

        HashSet<Paraula> set = CatalegLongituds.get(wordLength - 1);
        int randomNumber = random.nextInt(lecLinea.getNumParaulesCatalegLongituds()[wordLength - 1]);
        //  Log.d("NUMERO RANDOM ( "+lecLinea.getNumParaulesCatalegLongituds()[wordLength-1]+" ): ", "solucio: "+ randomNumber);

        Iterator<Paraula> iterator = set.iterator();
        int currentIndex = 0;
        Paraula randParaula = null;
        while (iterator.hasNext()) {
            randParaula = iterator.next();
            if (currentIndex == randomNumber) {
                return randParaula;
            }
            currentIndex++;
        }

        return randParaula;
    }

    /*
     Mètode que CREA el CATALEGSOLUCIONS a partir de la paraulaGuanyadora
     */
    public void crearCatalegSolucions(String paraulaGuanyadora) {
        //Cream CATÀLEG SOLUCIONS amb llistes buides:
        for (int i = 0; i < 7; i++) {
            CatalegSolucions.put(i, new HashSet<Paraula>());
        }

        //Hem de recorrer el CatalegLongituds i mirar per cada longitud quins son les posibles paraules guanyadores
        for (int i = 0; i < 7; i++) { //Recorr les distintes longituds:
            Set<Paraula> list = CatalegLongituds.get(i);
            if (list != null) {
                Iterator<Paraula> it2 = list.iterator();
                while (it2.hasNext()) {
                    Paraula pa = it2.next();
                    if (esParaulaSolucio(paraulaGuanyadora, pa.toString())) {
                        Log.d("PARAULA SOLUCIO:", pa.paraulaAccent);

                        Set<Paraula> listSolucions = CatalegSolucions.get(i); //Agafam la llista corresponent a les solucions de la llargaria i
                        //list.add(pa); //Afegim la paraula a la llista de paraules SOLUCIONS
                        listSolucions.add(pa);
                        //Augmentam el contador de paraules dels catalegs de solucions:
                        numParaulesCatalegSolucions[i]++;
                        solucionsPosibles++;
                    }
                }
            }
        }
    }

    /*
     Mètode que SELECCIONA 5 PARAULES OCULTES de les solucions posibles
     */
    public void seleccioParaulesOcultes() {
        int index = 0;
        boolean paTrobada;
        int numParaules = 0;
        paraulesOcultes = new HashSet();

        //Primer afegim la paraula Solucio ja que aquesta SEMPRE es paraula oculta
        paraulesOcultes.add(paraulaGuanyadora);
        numParaulesOcultes++;
        //La eliminam del catalegSolucions
        CatalegSolucions.get(wordLength - 1).remove(paraulaGuanyadora);
        numParaulesCatalegSolucions[wordLength - 1]--;
        //Agafam una paraula de cada llargària possible
        int n = wordLength - 1;
        for (int i = 2; i < n; i++) {
            Set<Paraula> list = CatalegSolucions.get(i);
            if (numParaulesCatalegSolucions[i] > 0 && list != null && numParaulesOcultes < 5) {
                int randomNumber = random.nextInt(numParaulesCatalegSolucions[i]);
                index = 0;

                Iterator<Paraula> it = list.iterator();
                paTrobada = false;

                while (it.hasNext() && !paTrobada) {
                    Paraula pa = it.next();
                    if (index == randomNumber) {
                        paraulesOcultes.add(pa);   //Afegim la paraula a la paraula oculta
                        //Disminuim el comptador de paraules solucio corresponent:
                        numParaulesCatalegSolucions[i]--;
                        Log.d("AFEGIM 1era PASADA:", " " + pa.toString());
                        numParaulesOcultes++;
                        //Eliminam la paraula de CatalegSolucions:
                        it.remove();
                        paTrobada = true;
                    }
                    index++;
                }
            }
        }

        // A continuació s'han d'afegir paraules de longitud 3 fins que el nombre
        // de paraules sigui 5 o no hi hagi més paraules
        Log.d("Nombre paraules ocultes:", " " + numParaulesOcultes);
        while (numParaulesOcultes < 5 && numParaulesCatalegSolucions[2] > 0) {
            Iterator<Paraula> ite = CatalegSolucions.get(2).iterator();
            Paraula aux = null;
            // Calculam un nombre random per elegir una paraula d'aquesta longitud
            if (numParaulesCatalegSolucions[2] != 1) {
                int numRand = random.nextInt(numParaulesCatalegSolucions[2]);
                int comptador = 0;
                while (ite.hasNext() && comptador != numRand) {
                    aux = ite.next();
                    comptador++;
                }
            } else {
                aux = ite.next();
            }
            if (aux != null) {
                paraulesOcultes.add(aux);
                numParaulesCatalegSolucions[2]--;
                numParaulesOcultes++;
                Log.d("AFEGIM SEGONA PASADA:", " " + aux.paraulaNoAccent);
                CatalegSolucions.get(2).remove(aux);
            }
        }

        //ORDENAM les PARAULES OCULTES
        paraulesOcultesArray = new Paraula[numParaulesOcultes];
        int ind = 0;
        Iterator<Paraula> it3 = paraulesOcultes.iterator();
        while (it3.hasNext()) {
            paraulesOcultesArray[ind] = it3.next();
            ind++;
        }

        //Ordenam les llargàries, ja que ja es troben ordenades alfabèticament perque estan a un TreeSet
        heapSort(paraulesOcultesArray);
        for (int i = 0; i < paraulesOcultesArray.length; i++) {
            Log.d("PARAULA ORDENADA:", " " + paraulesOcultesArray[i].toString());
        }
    }


    /*
     Mètode que CREA la MATRIU de TEXTVIEWS
     */
    public void crearFilesTextViews() {
        // Array de IDs de las guidelines
        int[] guidelinesIds = {R.id.guide1, R.id.guide2, R.id.guide3, R.id.guide4, R.id.guide5}; // Añade más IDs según sea necesario

        for (int fila = 0; fila < paraulesOcultesArray.length; fila++) {
            TextView[] tViews = crearFilaTextViews(paraulesOcultesArray[fila].getLength());
            filesTextviews[fila] = tViews;

            for (int i = 0; i < paraulesOcultesArray[fila].getLength(); i++) {
                startMargin = widthDisplay / 2 - paraulesOcultesArray[fila].getLength() * (lletraSize + 20) / 2;
                ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);

                TextView textView = tViews[i];
                constraintLayout.addView(textView);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                // Conectar la part superior del TextView a la guideline corresponent
                if (fila < guidelinesIds.length) {
                    constraintSet.connect(textView.getId(), ConstraintSet.TOP, guidelinesIds[fila], ConstraintSet.TOP);

                } else {
                    constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 10 * fila);
                }

                // Conectam l'inici del pare o el final del TextView anterior
                if (i == 0) {
                    constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, startMargin);
                } else {
                    constraintSet.connect(textView.getId(), ConstraintSet.START, tViews[i - 1].getId(), ConstraintSet.END, 20);
                }

                // m las restriccions
                constraintSet.applyTo(constraintLayout);
            }

        }
    }


    /*
     Mètode que passant per paràmetre el nombre de lletres CREA un ARRAY de TEXTVIEWS
     */
    public TextView[] crearFilaTextViews(int numLletres) {
        TextView textviews[] = new TextView[numLletres];

        for (int i = 0; i < numLletres; i++) {

            int id = View.generateViewId();
            TextView textView = new TextView(this);

            textView.setId(id);
            textView.setText(" ");
            textView.setWidth(lletraSize);
            textView.setHeight(lletraSize);
            textView.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            textView.setBackgroundColor(color);
            textView.setTextColor(WHITE);
            textView.setTextSize(20);
            textviews[i] = textView;
        }
        return textviews;
    }

    //Mètode que AFEGEIX una LLETRA al TEXTVIEW
    public void escriureLletra(String lletra) {
        paraulaEntrada = paraulaEntrada + lletra;
        mParaula.setText(paraulaEntrada);
    }


    /*
     Mètode que ACTUALITZA el MISSATGE que mostra les PARAULES TROBADES.
     */
    public void updateMessage(boolean paraulaRepetida, Paraula paraula) {
        Iterator it = paraulesTrobades.iterator();
        message = "";
        String red = "";
        while (it.hasNext()) {
            Paraula par = (Paraula) it.next();
            if (paraulaRepetida) {
                if (par.equals(paraula)) {
                    red = "<font color='#EE0000'>" + par.paraulaAccent + "</font>";
                    message += red + " ,";
                } else {
                    message += par.paraulaAccent + ", ";
                }
            } else {
                message += par.paraulaAccent + ", ";
            }
        }
    }

    // DIÀLEG que apareix amb pistes quan es clica el botó de BONUS
    public void dialegBonus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Encertades (" + numParaulesTrobades + " de " + solucionsPosibles + ") :");

        builder.setMessage(Html.fromHtml(message));

        //Un botó OK per tancar la finestra
        builder.setPositiveButton("OK", null);

        //Mostrar l'AlertDialog a la pantalla
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*
    Mètode que INICIALITZA les LLETRES del CERCLE
     */
    public void setLletraBotons() {
        lletresDisponibles = fisherYates(lletresDisponibles);   //Cridam al mètode de desordenació
        mLletra1.setText(String.valueOf(lletresDisponibles[0]));
        mLletra2.setText(String.valueOf(lletresDisponibles[1]));
        if (wordLength > 2) {
            mLletra3.setText(String.valueOf(lletresDisponibles[2]));

        }
        if (wordLength > 3) {
            mLletra4.setText(String.valueOf(lletresDisponibles[3]));
        }
        if (wordLength > 4) {
            mLletra5.setText(String.valueOf(lletresDisponibles[4]));
        }
        if (wordLength > 5) {
            mLletra6.setText(String.valueOf(lletresDisponibles[5]));
        }
        if (wordLength > 6) {
            mLletra7.setText(String.valueOf(lletresDisponibles[6]));
        }
    }

    //Mètode que DESORDENA un ARRAY passat per paràmetre
    public char[] fisherYates(char[] arr) {
        int j; //entre 0 i
        for (int i = arr.length - 1; i > 0; i--) {
            j = random.nextInt(i + 1);
            //Intercanviam arr[i] amb l'elemend al index random
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }


    /*
     Mètode que COMPROVA, si la paraula2 passada per paràmetre es pot
     escriure amb les lletres de la paraula Guanyadora passada també
     per paràmetre.
     */
    public boolean esParaulaSolucio(String paraulaGuanyadora, String paraula2) {
        if (paraulaGuanyadora == null || paraula2 == null || paraulaGuanyadora.isEmpty() || paraula2.isEmpty()) {
            return false;
        }

        HashMap<Character, Integer> charCountMap = new HashMap<>();
        for (char c : paraulaGuanyadora.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }

        for (char c : paraula2.toCharArray()) {
            if (!charCountMap.containsKey(c) || charCountMap.get(c) == 0) {
                return false;
            }
            charCountMap.put(c, charCountMap.get(c) - 1);
        }

        return true;
    }

    /*
    Mètode que COMPROVA si la paraula passada per paràmetre (s és el String de la paraula)
    es tracta de una paraula Oculta,una paraula Solució, si ja s'ha introduit o si és incorrecta:
    - Si és paraula Oculta: mostra la paraula a la posició corresponent
    - Si és paraula Solució: s'afegeix a la llista de paraules trobades
    - Si ja introduida: es posa en vermell
    - Si incorrecta: s'indica a l'usuari amb un missatge.
    */
    private void comprovaParaula(String s) {

        //1er Esborram la paraula introduida:
        mParaula.setText("");

        //2on Es tornen a activar les lletres
        resetBotonsOff();

        Paraula pa = new Paraula();

        pa = pa.paraulaSola(s);
        int index = 0;
        boolean paTrobada = false;
        boolean paRepetida = false;

        //3er Comprovam si la paraula introduida es una ParaulaOculta
        while (!paTrobada && index < paraulesOcultesArray.length) {
            paTrobada = paraulesOcultesArray[index].equals(pa);
            index++;
        }

        if (paTrobada && (!paraulesTrobades.contains(pa))) {
            pa = paraulesOcultesArray[index - 1]; //actualitzam la paraula per obtenir la paraula completa amb accent
            Log.d("paraula accent:", " " + pa.paraulaAccent);

            //La ficam a la llista de Paraules Trobades
            paraulesTrobades.add(pa);
            Log.d("AFEGIM", " " + pa.toString());
            numParaulesTrobades++;
            //Eliminam la paraula trobada de paraulesOcultes
            paraulesOcultes.remove(pa);

            //Afegeix la paraula Trobada als TextViews corresponents
            if (index <= paraulesOcultesArray.length) { //Es que la ha trobat
                for (int i = 0; i < pa.length; i++) {
                    filesTextviews[index - 1][i].setText("   " + String.valueOf(paraulesOcultesArray[index - 1].getLletresParaula()[i]).toUpperCase());
                    posicionsParaulesTrobades[index - 1] = true; //Indicam que ja s'ha afegit la paraula en aquesta fila de textviews
                    //Mostram la paraula a la linea que esta amagada
                }
            }
        } else if (paTrobada && (paraulesTrobades.contains(pa))) {
            mostraMissatge("Aquesta ja la tens", false);
            //Mostrar la paraula en vermell dins la llista de paraules trobades
            paRepetida = true;
        } else if (CatalegSolucions.get(pa.getLength() - 1).contains(pa)) { //Apareix un missatge si es troba en cataleg solucions
            Set set = CatalegSolucions.get(pa.getLength() - 1);
            Iterator it = set.iterator();
            boolean paraulaIgual = false;
            while (it.hasNext() && !paraulaIgual) {
                Paraula aux = (Paraula) it.next();
                if (pa.equals(aux)) {
                    pa = aux;
                    paraulaIgual = true;
                }
            }

            mostraMissatge("Paraula vàlida! Tens un bonus més", false);
            bonus++;
            mScoreNumber.setText("" + bonus);
            //Eliminam la paraula del catalegSolcuions
            CatalegSolucions.get(pa.getLength() - 1).remove(pa);
            //Afegim la paraula a la llista de paraules trobades
            paraulesTrobades.add(pa);
            numParaulesTrobades++;
            //Actualiztam el missatge
        } else if (paraulesTrobades.contains(pa) || (paTrobada && (paraulesTrobades.contains(pa)))) {
            mostraMissatge("Aquesta ja la tens", false);
            //Mostrar la paraula en vermell dins la llista de paraules trobades
            paRepetida = true;

        } else {
            mostraMissatge("Paraula no vàlida", false);
        }
        //Actualitzam el missatge de paraules trobades
        updateMessage(paRepetida, pa);
        mParaulesConstruides.setText(Html.fromHtml(" Has encertat " + numParaulesTrobades + " de " + solucionsPosibles + " : " + message));
        //Hem de valorar si ha guanyat:
        if (paraulesOcultes.isEmpty()) { //Ja no queden paraules ocultes per descobrir
            mostraMissatge("Enhorabona! Has guanyat", true);
            //Es desactiven tots els elements de la pantalla, excepte els botons de crear partida i de veure bonus
            disableViews(R.id.clear);
            disableViews(R.id.send);
            disableViews(R.id.shuffle);
            disableViews(R.id.help);
            disableViews(R.id.lletra1);
            disableViews(R.id.lletra2);
            disableViews(R.id.lletra3);
            disableViews(R.id.lletra4);
            disableViews(R.id.lletra5);
            disableViews(R.id.lletra6);
            disableViews(R.id.lletra7);
        }

    }

    /*
    Configuració del BOTO HELP, mostra la primera lletra d'una de les paraules que
    encara es troben ocultes (la paraula es tria de manera aleatoria)
    */
    private void help() {
        if (bonus >= 5) {
            Iterator it = paraulesOcultes.iterator();
            int num = 0;
            Paraula pa = null;
            boolean trobat = false;
            if (numPrimeresLletres < paraulesOcultesArray.length) {
                while (!trobat) {
                    num = random.nextInt(paraulesOcultesArray.length);
                    if (!posicionsParaulesTrobades[num]) {
                        Log.d("NUM", num + "");
                        pa = paraulesOcultesArray[num];
                        pa.setPrimeraLletra();
                        trobat = true;
                    }

                }
                mostraPrimeraLletra(pa.toString(), num);
                posicionsParaulesTrobades[num] = true;
                Log.d("PRIMERA LLETRA", num + " ");
                numPrimeresLletres++;
                //Es gasten 5 bonus per cada pista:
                bonus = bonus - 5;
                mScoreNumber.setText("" + bonus);
            } else {
                mostraMissatge("Ja no hi ha més pistes", false);
            }
        } else {
            mostraMissatge("No tens bonus suficients", false);

        }

    }

    /*
    Mètode que MOSTRA la PRIMERA LLETRA on s és un String que conté la paraula de la que es vol mostrar
    la primera lletra (en minúscula) i posició indica a quina de les línies corresponents a les
    paraules amagades es vol mostrar aquesta lletra
    */
    private void mostraPrimeraLletra(String s, int posicio) {
        char[] pa = s.toCharArray();
        //Primer obtenim la instancia al TextView corresponent
        filesTextviews[posicio][0].setText("   " + String.valueOf(pa[0]).toLowerCase());
    }

    /*
    Mètode que MOSTRA un MISSATGE, on s es un String que conte el missatge que es vol mostrar i llarg
    és una variable booleana que indica si el temps per mostrar el missatge es llarg  o curt.
    */
    private void mostraMissatge(String s, boolean llarg) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration;
        if (llarg) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /*Mètode que REINICIA la PARTIDA*/
    public void reiniciar() {
        // 1er Esborram de la pantalla les caselles de les lletres amagades actuals
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        for (int j = 0; j < paraulesOcultesArray.length; j++) {
            for (int i = 0; i < filesTextviews[j].length; i++) {
                TextView txtView = filesTextviews[j][i];
                constraintLayout.removeView(txtView);
            }
        }

        // 2on Canviam de color els elements de la pantalla (cercle i lletres amagades)
        ImageView imageViewIcon = (ImageView) findViewById(R.id.planetImage);

        color = getRandomSemiTransparentColor();
        imageViewIcon.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));

        // 3er Reiniciam totes les variables necessàries per començar una nova partida
        this.paraulaSolucio = "";
        clearParaula();
        this.lletresDisponibles = null;
        this.bonus = 0;
        this.solucionsPosibles = 0;
        this.numPrimeresLletres = 0;
        this.wordLength = 0;
        this.numParaulesTrobades = 0;
        this.message = "";
        this.numParaulesOcultes = 0;
        enableViews(R.id.clear);
        enableViews(R.id.send);
        enableViews(R.id.reiniciar);
        enableViews(R.id.help);
        enableViews(R.id.lletra1);
        enableViews(R.id.lletra2);
        enableViews(R.id.lletra3);
        enableViews(R.id.lletra4);
        enableViews(R.id.lletra5);
        enableViews(R.id.lletra6);
        enableViews(R.id.lletra7);

        resetNumParaulesCatalegSolucions();
        eliminarLletresCercle();

        // 4t Mostram les noves paraules amagades
        initPartida();
    }

    /*
     Mètode que a partir de l’identificador d’un element de la pantalla (el parent), ha d’-
     HABILITAR totes les seves COMPONENTS (enableViews)
     */
    private void enableViews(int parent) {
        View view = findViewById(parent);
        view.setEnabled(true);
    }

    // Mètode que a partir de l’identificador d’un element de la pantalla (el parent), ha de
    // DESHABILITAR totes les seves COMPONENTS
    private void disableViews(int parent) {
        View view = findViewById(parent);
        view.setEnabled(false);
    }


    /* GESTIÓ D'ESDEVENIMENTS */
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bonus) {
            dialegBonus();
        } else if (id == R.id.clear) {
            clearParaula();
        } else if (id == R.id.help) {
            help();
        } else if (id == R.id.shuffle) {
            setLletraBotons();
        } else if (id == R.id.send) {
            if (mParaula.getText() != "") {
                comprovaParaula(mParaula.getText().toString());
                clearParaula();
                resetColorsLletres();
            }
        } else if (id == R.id.reiniciar) {
            reiniciar();
        } else {
            setLletra(view);
        }
    }


    /* MÈTODES AUXILIARS */

    // Retorna un valor entre 4 i 7 (inclosos)
    public int determinarWordLength() {
        int randomNum = random.nextInt((7 - 5) + 1) + 4;
        return randomNum;
    }

    // Mètode que retorna un color amb transparencia (alpha, red, green, blue):
    public static int getRandomSemiTransparentColor() {
        Random random = new Random();
        int alpha = random.nextInt((128 - 65 + 1)) + 65; // 65-128
        int red = random.nextInt(256); // 0-255
        int green = random.nextInt(256); // 0-255
        int blue = random.nextInt(256); // 0-255
        return Color.argb(alpha, red, green, blue);
    }


    // Mètode que posa totes les lletres en color blanc
    public void resetColorsLletres() {
        mLletra1.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra2.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra3.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra4.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra5.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra6.setTextColor(Color.parseColor("#FFFFFF"));
        mLletra7.setTextColor(Color.parseColor("#FFFFFF"));
    }

    //Mètode que elimina les lletres del cercle i reinicia la visibilitat
    //de les lletres 6 i 7
    public void eliminarLletresCercle() {
        mLletra1.setText("");
        mLletra2.setText("");
        mLletra3.setText("");
        mLletra4.setText("");
        mLletra5.setText("");
        mLletra6.setText("");
        mLletra7.setText("");
        mLletra5.setVisibility(View.GONE);
        mLletra6.setVisibility(View.GONE);
        mLletra7.setVisibility(View.GONE);
    }

    //Mètode que reinicia l'array del nombre de paraules que hi ha de cada cataleg de solucions
    public void resetNumParaulesCatalegSolucions() {
        for (int i = 0; i < numParaulesCatalegSolucions.length; i++) {
            numParaulesCatalegSolucions[i] = 0;
        }
    }

    //Mètode que buida el textView de paraula Entrada (mParaula) i
    //reinicia els botons pitjats de les lletres del cercle.
    public void clearParaula() {
        paraulaEntrada = "";
        mParaula.setText(paraulaEntrada);
        //També s'ha de reiniciar les lletres utilitzades:
        resetBotonsOff();
        resetColorsLletres();
    }

    //Mètode que posa tots els botons del lletres del cercle a 1
    // indicant que NO s'han pitjat
    public void resetBotonsOff() {
        for (int i = 0; i < botonsOff.length; i++) {
            botonsOff[i] = 1;
        }
    }

    //Mètode que reinicia les posicions de les paraules trobades
    // a false, indicant que no s'ha escrit ninguna paraula trobada
    // a cap posició
    public void resetPosicionsParaulesTrobades() {
        for (int i = 0; i < 5; i++) {
            posicionsParaulesTrobades[i] = false;
        }
    }

    // Funció principal del HEAPSORT, recorr l'array del final
    // al principi, eliminant l'element màxim del heap en cada
    // iteració i reconstruint-lo fins que tots els elements
    // són ordenats.
    public static void heapSort(Paraula[] array) {
        buildMaxHeap(array);
        for (int i = array.length - 1; i > 0; i--) {
            swap(array, 0, i);
            adjustHeap(array, 0, i);
        }
    }

    // Mètode que construiex el heap maxim
    public static void buildMaxHeap(Paraula[] array) {
        int firstNonLeaf = (array.length - 1) / 2;
        for (int i = firstNonLeaf; i >= 0; i--) {
            adjustHeap(array, i, array.length);
        }
    }

    // Metode que "baixa" un element del heap fins la seva posició
    public static void adjustHeap(Paraula[] array, int root, int heapSize) {
        int leftChild = 2 * root + 1;
        if (leftChild < heapSize) {
            int rightChild = 2 * root + 2;
            if (rightChild < heapSize) {
                if (array[rightChild].getLength() > array[leftChild].getLength()) {
                    leftChild = rightChild;
                }
            }
            if (array[leftChild].getLength() > array[root].getLength()) {
                swap(array, root, leftChild);
                adjustHeap(array, leftChild, heapSize);
            }
        }
    }

    // Mètode que intercanvia dos elements d'un array.
    public static void swap(Paraula[] array, int i, int j) {
        Paraula temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}





