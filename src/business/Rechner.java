package business;

import helper.language.Messages;

public class Rechner {

	private int anzeige;
	private int speicher;
	
	public int getAnzeige() {
		return anzeige;
	}
	
	public void setAnzeige(int anzeige) {
		this.anzeige = anzeige;
	}
	
	public int getSpeicher() {
		return speicher;
	}
	
	public void setSpeicher(int speicher) {
		this.speicher = speicher;
	}

	public void addieren(int wert) {
		this.anzeige += wert;		
	}
	
	public void subtrahieren(int wert) {
		this.anzeige -= wert;		
	}
	
	public void speichern(){
		this.speicher = this.anzeige;
	}
	
	public void speicherAddieren(){
		this.anzeige += this.speicher;
	}
	
	public void speicherSubtrahieren(){
		this.anzeige -= this.speicher;
	}
	
	@Override
	public String toString(){
	  return Messages.getString("Rechner.0") + this.speicher   //$NON-NLS-1$
			  +Messages.getString("Rechner.1") + this.anzeige;   //$NON-NLS-1$
	}
}
