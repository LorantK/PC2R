package serveur;

import java.util.ArrayList;

public class Jam extends Thread {
	private String style;
	private String tempo;
	private int nbConnecte = 0;
	private final int MAX;
	private int tickActuel = 0;
	private boolean parametre = false;
	private Server s;
	private ArrayList<Tick> listTick;

	public Jam(int max, Server s){
		MAX = max;
		this.s = s;
		listTick = new ArrayList<>();
		this.start();
	}

	public void run(){ // Gestion du tick
		while(true){
			listTick.add(new Tick(tickActuel, s));
			try {
				listTick.get(tickActuel).wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tickActuel++;
		}
	}

	public String getStyle(){
		return style;
	}

	public String getTempo(){
		return tempo;
	}

	public void setStyle(String style){
		this.style = style;
		parametre = true;
	}

	public void setTempo(String tempo){
		this.tempo = tempo;
		parametre = true;
	}

	public synchronized void addConnecte(){
		nbConnecte++;
	}

	public void disconnect() {
		nbConnecte--;
	}

	public synchronized int getNbConnecte(){
		return nbConnecte;
	}

	public int getMax(){
		return MAX;
	}

	public synchronized int getTickActuel() {
		return tickActuel;
	}

	public void setTickActuel(int tickActuel) {
		this.tickActuel = tickActuel;
	}

	public ArrayList<Tick> getListTick() {
		return listTick;
	}

	public void setListTick(ArrayList<Tick> listTick) {
		this.listTick = listTick;
	}	
}
