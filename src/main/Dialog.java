package main;

import execute.commands.*;
import helper.language.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import business.Rechner;

public class Dialog {

    private Rechner rechner = new Rechner();
    private Map<Integer, Command> aktionen = new HashMap<>();

    private Stack<Undo> undos = new Stack<>();
    private Stack<Redo> redos = new Stack<>();

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

    public void ausgabe() { // gibt Auswahlmoeglichkeiten aus
        //System.out.println("(0) Programm beenden");
        for (int tmp : this.aktionen.keySet()) {
            System.out.println("(" + tmp + ") " + this.aktionen.get(tmp));
        }
        if (undos.size() != 0) {
            System.out.println(Messages.getString("Dialog.0"));
        }
        if (redos.size() != 0) {
            System.out.println(Messages.getString("Dialog.1"));
        }
    }

    public int einSchritt() {
        this.ausgabe();
        int eingabe = Eingabe.leseInt();
        switch (eingabe) {
            case 98:
                if (undos.size() != 0) {
                    redos.add((Redo) undos.pop().execute());
                }
                break;
            case 99:
                if (redos.size() != 0) {
                    undos.add(new Undo(this.rechner)); //wichtig
                    redos.pop().execute();
                }
                break;
            default:
                Command com = this.aktionen.get(eingabe);
                if (com != null) {
                    redos.clear();
                    undos.add((Undo) com.execute());
                }
        }
        System.out.println(this.rechner);
        return eingabe;
    }
}
