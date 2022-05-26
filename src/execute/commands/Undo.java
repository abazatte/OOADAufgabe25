package execute.commands;

import business.Rechner;

public class Undo implements Command {

    private int altAnzeige;
    private int altSpeicher;
    private Rechner rechner;

    public Undo(Rechner rechner) {
        this.altAnzeige = rechner.getAnzeige();
        this.altSpeicher = rechner.getSpeicher();
        this.rechner = rechner;
    }

    @Override
    public Command execute() {
        int neuAnzeige = this.rechner.getAnzeige();
        int neuSpeicher = this.rechner.getSpeicher();
        this.rechner.setAnzeige(altAnzeige);
        this.rechner.setSpeicher(altSpeicher);
        return new Redo(this.rechner, neuAnzeige, neuSpeicher);
    }
}
