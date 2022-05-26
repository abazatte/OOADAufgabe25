package execute.commands;

import helper.language.Messages;
import business.Rechner;
import main.Eingabe;

public class Subtrahieren implements Command {
  private Rechner rechner;
  
  public Subtrahieren(Rechner rechner){
    this.rechner = rechner;
  }

  @Override
  public void execute() {
    System.out.print(Messages.getString("Command.0"));
    this.rechner.subtrahieren(Eingabe.leseInt());
  }
  
  @Override
  public String toString(){
    return Messages.getString("Command.5");   //$NON-NLS-1$
  }

}