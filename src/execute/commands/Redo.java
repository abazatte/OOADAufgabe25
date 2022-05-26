package execute.commands;

import business.Rechner;

public class Redo implements Command{
    private Rechner rechner;
    private int neuSpeicher;
    private int neuAnzeige;

    public Redo(Rechner rechner, int neuAnzeige, int neuSpeicher){
        this.rechner = rechner;
        this.neuAnzeige = neuAnzeige;
        this.neuSpeicher = neuSpeicher;
    }

    @Override
    public Command execute() {
        this.rechner.setAnzeige(this.neuAnzeige);
        this.rechner.setSpeicher(this.neuSpeicher);
        return this;
    }
}
