package execute.commands;

import business.Rechner;
import helper.language.Messages;

public class Rechnerzuruecksetzen implements Command {
    Rechner rechner;

    public Rechnerzuruecksetzen(Rechner rechner) {
        this.rechner = rechner;
    }

    @Override
    public void execute() {
        rechner.setAnzeige(0);
        rechner.setSpeicher(0);
    }

    @Override
    public String toString() {
        return Messages.getString("Command.7");     //$NON-NLS-1$
    }
}
