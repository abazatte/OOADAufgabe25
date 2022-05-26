package main;

import execute.commands.*;
import helper.language.Messages;

import java.util.HashMap;
import java.util.Map;

import business.Rechner;

public class Dialog {

    private Rechner rechner = new Rechner();
    private Map<Integer, Command> aktionen = new HashMap<>();
       
    public Dialog() {
        this.aktionen.put(0, new ProgrammBeenden());
        this.aktionen.put(1, new Rechnerzuruecksetzen(this.rechner));
        this.aktionen.put(2, new Addieren(this.rechner));
        this.aktionen.put(3, new Subtrahieren(this.rechner));
        this.aktionen.put(4, new AnzeigeSpeichern(this.rechner));
        this.aktionen.put(5, new SpeicherAddieren(this.rechner));
        this.aktionen.put(6, new SpeicherSubtrahieren(this.rechner));
    }

    public void dialog() {
        int eingabe = -1;
        while (eingabe != 0) {
            eingabe = einSchritt();
        }
    }
    
    public void ausgabe(){ // gibt Auswahlmoeglichkeiten aus
        //System.out.println("(0) Programm beenden");
        for (int tmp : this.aktionen.keySet()) {
            System.out.println("(" + tmp + ") " + this.aktionen.get(tmp));
        }       
    }

    public int einSchritt() {
        this.ausgabe();
        int eingabe = Eingabe.leseInt();
        Command com = this.aktionen.get(eingabe);
        if (com != null) {
            com.execute();
        }
        System.out.println(this.rechner);
        return eingabe;
    }
}
