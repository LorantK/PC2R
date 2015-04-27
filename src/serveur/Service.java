package serveur;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Classe permettant le traitement des messages envoyes par les clients sur le canal principal
 * @author Eric
 *
 */
public class Service extends Thread {
	protected Socket client; // socket entre le serveur et le client
	protected Socket Canalaudio; // Canal audio

	protected String nomClient;  // le nom du client, recu lors d'une connexion que ce soit connect/login/...

	protected DataInputStream in; 

	protected PrintStream out;

	protected Server serv; 

	private boolean fullSession = false;
	private boolean proprietaire = false; // Si ce client peut ou non regler les parametres de la JAM
	private boolean isConnected = false; // Si le client est connecte ou non (a passe l'etape du CONNECT)
	private boolean jamConnected = false; // Si le client est connecte au canal audio ou non

	public Service(Socket s, Server serv) {
		this.client = s;
		this.serv = serv;

		try {
			in = new DataInputStream(s.getInputStream());
			out = new PrintStream(s.getOutputStream());
			synchronized (this) {
				serv.addOut(out); // On ajoute l'outputstream dans la liste des output
			}
		} catch (IOException ex) {
			try {
				client.close();
			} catch (IOException e1) {
			}
			System.err.println(ex.getMessage());
			return;
		}
		this.start();
	}

	public void run() {
		String commande;
		String[] param;
		try {
			while (true) {
				commande = in.readLine(); // String recu
				if(commande == null){ // Gere le cas ou le client se deconnecte sans passer par exit
					disconnectUser();
					return;
				}
				param = commande.split("/");
				if(param[0].equals("EXIT")){ // Si le client veut se deconnecter
					disconnectUser();
					return;
				}
				if(param[0].equals("CONNECT")){ // Si le client veut se connecter
					if(param.length == 2){ // On verifie le nombre de parametres (doit etre CONNECT/nom/)
						if(!checkNameClient(param[1])) // Nom deja utilise
							continue;
						break;
					}	
					out.println("ERROR/Nombre d'arguments");
					out.flush();
				}

				if(param[0].equals("REGISTER")){ // Si l'on veut s'enregistrer
					if(param.length != 3){ 
						out.println("ERROR/Nombre d'arguments");
						out.flush();
					}
					else {
						if(serv.register(param[1], param[2])){ // Si on peut enregistrer
							if(!checkNameClient(param[1])) // Nom deja utilise
								continue;
							break;
						}
						else {
							out.println("ERROR/Nom d'utilisateur deja pris");
							out.flush();
						}
					}
				}
				if(param[0].equals("LOGIN")){ // Si lon veut se connecter avec un nom deja enregistre
					if(param.length != 3){
						out.println("ERROR/Nombre d'arguments");
						out.flush();
					}
					else {
						if(serv.login(param[1], param[2])) { // 
							break;
						}
						else {
							out.println("ERROR/Login. Mot de passe invalide ou compte inexistant");
							out.flush();
						}
					}
					out.println("ERROR/Commande Invalide. Entrez une commande pour vous connecter ou vous deconnecter");
					out.flush();
				}
			}
			System.out.println("Nouvelle connexion");

			nomClient = param[1]; 
			serv.addConnectedUser(nomClient); // On ajoute le nom du client dans la liste des clients connectes.
			isConnected = true; 

			out.println("WELCOME/" + nomClient); 
			out.flush();
			out.println("AUDIO_PORT/2015/"); // On envoie le port (2015 par defaut)
			out.flush();
			Canalaudio = serv.getcAudio().accept(); 
			jamConnected = true;
			ServiceAudioReception s = new ServiceAudioReception(Canalaudio, nomClient, serv);
			out.println("AUDIO_SYNC/" + serv.getJam().getTickActuel() + "/"); // On envoie le tick actuel
			out.println("AUDIO_OK");
			out.flush();
			out.println("LIST/"+serv.getConnectedUserString()); // On envoie la liste des personnes connectes (pour le chat)
			out.flush();
			userConnected();
			gestionJam();
			chat();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fonction de gestion du chat
	 */
	public void chat(){
		String commande;
		String [] param;
		try{
			while(true){
				commande = in.readLine();
				if(commande == null){ // Cas ou le client se deconnecte
					disconnectUser();
					return;
				}
				System.out.println(commande);
				param = commande.split("/");
				switch (param[0]) { // Selon le type de la commande

				case "EXIT":
					disconnectUser();
					return;

				case "SET_OPTIONS":
					if(proprietaire){ // Commande de modification des parametres de la JAM
						if(param.length == 3) {
							serv.getJam().setStyle(param[1]);
							serv.getJam().setTempo(param[2]);

							out.println("ACK_OPTS");
							out.flush();
						}
					}
					break;
				case "TALK": // CHAT, commande pour parler
					for (int i = 0; i < serv.out.size(); i++) {
						serv.out.get(i)
						.println("LISTEN/" + nomClient + "/"+param[1]);
						serv.out.get(i).flush();
					}
					break;	
				default:
					out.println("ERROR/Commande Invalide");
					out.flush();
					break;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verifie si le pseudo du client existe deja (Extension compte utilisateur)
	 * @param n pseudo du client
	 * @return true s'il n'existe pas, false sinon
	 */
	public boolean checkNameClient(String n){
		if(!serv.checkNameClient(n)){
			out.println("ACCESSDENIED/Reconnectez-vous avec un autre nom.");
			out.flush();
			return false;
		}

		return true;
	}

	/**
	 * Signifie a tous les clients connectes la connexion de USER (COMMANDE CONNECTED)
	 */
	public void userConnected(){
		for (int i = 0; i < serv.out.size(); i++) {
			serv.out.get(i).println("CONNECTED/" + nomClient + "/");
			serv.out.get(i).flush();
			serv.out.get(i).println("LIST/"+serv.getConnectedUserString()); // Envoi la nouvelle liste des clients connectes
			serv.out.get(i).flush();	
		}
	}

	/**
	 * Deconnecte l'utilisateur et signifie a tous les clients connectes la deconnexion de USER (COMMANDE EXITED)
	 */
	public void disconnectUser(){
		try{
			serv.disconnectOut(out);
			serv.deleteUser(nomClient);
			client.close();
			serv.disconnectOut(out);
			if(jamConnected){
				serv.getJam().disconnect();
				Canalaudio.close();
			}
			if(isConnected)
				for (int i = 0; i < serv.out.size(); i++) {
					serv.out.get(i).println("EXITED/" + nomClient + "/");
					serv.out.get(i).flush();
					serv.out.get(i).println("LIST/"+serv.getConnectedUserString());  // Envoi la nouvelle liste des clients connectes
					serv.out.get(i).flush();	
				}
			if(proprietaire){
				for (int i = 0; i < serv.out.size(); i++) {
					serv.out.get(i).println("Musicien originel parti. Arret de la jam...");
					serv.out.get(i).flush();	
				}
				serv.setJam(new Jam(serv.getMax(), serv));
					if(serv.out.size() != 0){
						serv.out.get(0).println("Vous etes le nouveau proprietaire de la Jam. Veuillez choisir les parametres");
						serv.out.get(0).flush();	
					}
			}


		}catch(IOException e){
			System.err.println("Probleme dans la deconnexion du client");
		}
	}

	/**
	 * Fonction de gestion de connexion a la JAM
	 */
	public synchronized void gestionJam(){
		String commande;
		String [] param;
		synchronized (this) {
			if (serv.getJam().getNbConnecte() < serv.getJam() // Si la JAM n'est pas complet
					.getMax()) {
				serv.getJam().addConnecte();
				if (serv.getJam().getNbConnecte() == 1) { // Si on est le 1er connecte  
					out.println("EMPTY_SESSION");
					out.flush();
					proprietaire = true;
				} else { // Sinon envoi de message avec le tempo/style/nbConnecte
					out.println("CURRENT_SESSION/"
							+ serv.getJam().getStyle() + "/"
							+ serv.getJam().getTempo() + "/"
							+ serv.getJam().getNbConnecte() + "/");
					out.flush();
				}
			} else { // Si la JAM est pleine
				out.println("FULL_SESSION");
				out.flush();

				fullSession = true;
			}
		}
	}
}
