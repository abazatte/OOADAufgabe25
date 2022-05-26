package execute.commands;

import helper.language.Messages;
import business.Rechner;

public class SpeicherAddieren implements Command {
  private Rechner rechner;
  
  public SpeicherAddieren(Rechner rechner){
    this.rechner = rechner;
  }

  @Override
  public void execute() {
   this.rechner.speicherAddieren();
  }
  
  @Override
  public String toString(){
    return Messages.getString("Command.3");   //$NON-NLS-1$
  }

}