package serveur;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Service de broadcast pour le server (commentaires, etc.)
 * Nouveau protocole. Une commande BROADCAST du serveur aura le format suivant : LISTEN/broadcast/message a envoyer
 * @author Eric
 *
 */
public class ServiceChatEcriture extends Thread {
	private Server s;
	
	public ServiceChatEcriture(Server s){
		this.s = s;
		this.start();
	}
	
	public void run(){
		Scanner s1 = new Scanner(System.in);
		while(true){
			String command = s1.nextLine(); // On recuperer la commande a envoyer
			for(int i = 0; i < s.out.size(); i++){
				s.out.get(i).println("LISTEN/broadcast/Serveur:" + command + "/");
			}
		}
	}
}
