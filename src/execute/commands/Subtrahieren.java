package execute.commands;

import helper.language.Messages;
import business.Rechner;
import main.Eingabe;

public class Subtrahieren implements Command {
    private Rechner rechner;

    public Subtrahieren(Rechner rechner) {
        this.rechner = rechner;
    }

    @Override
    public Command execute() {
        Undo undo = new Undo(this.rechner);
        System.out.print(Messages.getString("Command.0"));
        this.rechner.subtrahieren(Eingabe.leseInt());
        return undo;
    }

    @Override
    public String toString() {
        return Messages.getString("Command.5");   //$NON-NLS-1$
    }

}