package execute.commands;

import business.Rechner;
import helper.language.Messages;

public class Rechnerzuruecksetzen implements Command {
    Rechner rechner;

    public Rechnerzuruecksetzen(Rechner rechner) {
        this.rechner = rechner;
    }

    @Override
    public Command execute() {
        Undo undo = new Undo(this.rechner);
        rechner.setAnzeige(0);
        rechner.setSpeicher(0);
        return undo;
    }

    @Override
    public String toString() {
        return Messages.getString("Command.7");     //$NON-NLS-1$
    }
}
