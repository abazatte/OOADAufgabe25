package execute.commands;

import helper.language.Messages;

public class ProgrammBeenden implements Command {
    @Override
    public Command execute() {
        System.exit(0);
        return this;
    }

    @Override
    public String toString() {
        return Messages.getString("Command.6");     //$NON-NLS-1$
    }
}
