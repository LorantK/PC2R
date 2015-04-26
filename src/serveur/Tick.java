package serveur;

import java.util.ArrayList;

/**
 * Classe Tick, on gere ici la synchro
 * @author Eric
 *
 */
public class Tick extends Thread {
	private int numero; // numero du tick
	private Server s;

	public Tick(int numero, Server s){
		this.numero = numero;
		this.s = s;
		this.start();
	}

	public void run(){
		while(true){
			ArrayList<float[]> listBuff = s.getTabBuffer().get(numero);
			if(listBuff != null){
				if(listBuff.size() == s.getJam().getNbConnecte()){ // Si tous les clients ont envoye leur buffer
					synchronized(this){
						this.notifyAll(); // on debloque la jam et les threads d'envoi d'audio
					}
					break;
				}
			}
		}
	}
}
