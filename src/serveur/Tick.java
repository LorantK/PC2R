package serveur;

import java.util.ArrayList;

/**
 * Classe Tick, on gere ici la synchro. Les threads de mixage se mettent en attente sur cette objet.
 * Tant que tout les clients n'ont pas envoye leur buffer correspond au tick numero, les threads d'emissions audios 
 * sont bloques. Lorsque le serveur a recu tout les buffers audios, on debloque les threads d'emission correspondant
 * au tick numero
 * @author Eric
 *
 */
public class Tick extends Thread {
	private int numero; // numero du tick
	private Server s; 

	/**
	 * Constructeur
	 * @param numero
	 * @param s
	 */
	public Tick(int numero, Server s){
		this.numero = numero;
		this.s = s;
		this.start();
	}

	public void run(){
		while(true){
			ArrayList<float[]> listBuff = s.getTabBuffer().get(numero); // On recupere la liste des buffers recuperes par le serveur
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
