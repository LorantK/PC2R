package serveur;

public class Jam {
	private String style;
	private String tempo;
	private int nbConnecte = 0;
	private final int MAX;
	
	public Jam(int max){
		MAX = max;
	}
	
	public String getStyle(){
		return style;
	}
	
	public String getTempo(){
		return tempo;
	}
	
	public void setStyle(String style){
		this.style = style;
	}
	
	public void setTempo(String tempo){
		this.tempo = tempo;
	}
	
	public void addConnecte(){
		nbConnecte++;
	}
	
	public void disconnect() {
		nbConnecte--;
	}
	
	public int getNbConnecte(){
		return nbConnecte;
	}
	
	public int getMax(){
		return MAX;
	}
}
