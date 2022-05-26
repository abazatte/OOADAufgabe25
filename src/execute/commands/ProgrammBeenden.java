package execute.commands;

import helper.language.Messages;

public class ProgrammBeenden implements Command {
    @Override
    public void execute() {
        System.exit(0);
    }

    @Override
    public String toString() {
        return Messages.getString("Command.6");     //$NON-NLS-1$
    }
}
