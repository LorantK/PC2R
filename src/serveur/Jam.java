package serveur;

import java.util.ArrayList;

/**
 * Classe jam
 * @author Eric
 *
 */
public class Jam extends Thread {
	private String style;
	private String tempo;
	private int nbConnecte = 0;
	private final int MAX;
	private int tickActuel = 0; 
	private boolean parametre = false;
	private Server s;
	private ArrayList<Tick> listTick; // A chaque fois que l'on change de tick, on cree un nouveau objet tick

	public Jam(int max, Server s){
		MAX = max;
		this.s = s;
		listTick = new ArrayList<>();
		this.start();
	}

	public void run(){ // Gestion du tick
		try {

			while(true){

				if(!parametre){
					synchronized (this) {
						this.wait();
						break;		
					}

				}
			}
			while(true){
				listTick.add(new Tick(tickActuel, s));
				synchronized (listTick.get(tickActuel)) {
					listTick.get(tickActuel).wait(); // 
					tickActuel++;	
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		synchronized (this) {
			this.notify();	
		}

	}

	public void setTempo(String tempo){
		this.tempo = tempo;
		parametre = true;
		synchronized (this) {
			this.notify();	
		}
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

