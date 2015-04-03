package serveur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Service extends Thread {
	protected Socket client;
	protected Socket Canalaudio;

	protected String nomClient;

	protected DataInputStream in;
	protected DataInputStream inAudio;

	protected PrintStream out;
	protected PrintStream outAudio;

	protected Server serv;
	private boolean start = false;
	private boolean fullSession = false;
	private boolean proprietaire = false;
	private boolean spectator = false;
	private boolean isConnected = false;
	private boolean jamConnected = false;

	public Service(Socket s, Server serv) {
		this.client = s;
		this.serv = serv;

		try {
			in = new DataInputStream(s.getInputStream());
			out = new PrintStream(s.getOutputStream());
			synchronized (this) {
				serv.addOut(out);
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
				commande = in.readLine();
				if(commande == null){ // Gere le cas ou le client se deconnecte sans passer par exit
					disconnectUser();
					return;
				}
				param = commande.split("/");
				if(param[0].equals("EXIT")){
					disconnectUser();
					return;
				}
				if(param[0].equals("CONNECT")){
					if(param.length == 2){
						break;
					}	
					out.println("ERROR/Nombre d'arguments");
					out.flush();
				}

				if(param[0].equals("REGISTER")){
					if(param.length != 3){
						out.println("ERROR/Nombre d'arguments");
						out.flush();
					}
					else {
						if(serv.register(param[1], param[2])){ 
							break;
						}
						else {
							out.println("ERROR/Nom d'utilisateur déjà pris");
							out.flush();
						}
					}
				}
				if(param[0].equals("LOGIN")){
					if(param.length != 3){
						out.println("ERROR/Nombre d'arguments");
						out.flush();
					}
					else {
						if(serv.login(param[1], param[2])) {
							break;
						}

						else {
							out.println("ERROR/Login. Mot de passe invalide ou compte inexistant");
							out.flush();
						}
					}
					out.println("ERROR/Commande Invalide. Entrez une commande pour vous connecter ou vous déconnecter");
					out.flush();
				}
			}
			System.out.println("Nouvelle connexion");
			start = true;

			if(!checkNameClient(param[1])) // Nom deja utilise
				return;

			out.println("WELCOME/" + nomClient);
			out.flush();
			out.println("AUDIO_PORT/2015/");
			out.flush();
			Canalaudio = serv.getcAudio().accept();
			jamConnected = true;
			ServiceAudio s = new ServiceAudio(Canalaudio, nomClient, serv);
			out.println("AUDIO_OK");
			out.flush();
			out.println("LIST/"+serv.getConnectedUserString());
			out.flush();
			userConnected();
			gestionJam();
			chat();

		} catch (IOException e) {
		}
	}

	public void chat(){
		String commande;
		String [] param;
		try{
			while(true){
				commande = in.readLine();
				if(commande == null){
					disconnectUser();
					return;
				}
				System.out.println(commande);
				param = commande.split("/");
				switch (param[0]) {

				case "EXIT":
					disconnectUser();
					return;

				case "SET_OPTIONS":
					if(proprietaire){
						if(param.length == 3) {
							serv.getJam().setStyle(param[1]);
							serv.getJam().setTempo(param[2]);

							out.println("ACK_OPTS");
							out.flush();

							for (int i = 0; i < serv.out.size(); i++) {
								serv.out.get(i)
								.println("BROADCAST Parametres JAM modifies. Nouveaux parametres Style/tempo : " +   serv.getJam().getStyle() + " "
										+ serv.getJam().getTempo());
								serv.out.get(i).flush();
							}
						}
					}
					break;
				case "TALK":
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
		}
	}


	/**
	 * A faire plus tard
	 */
	public void help(){
		out.println("LISTE COMMANDES DISPONIBLES :");
		if(proprietaire)
			out.println("SET_OPTIONS/style/tempo : Règle les paramètres de la JAM");
		out.println("TALK/message/ : ");
		out.flush();
	}

	public boolean checkNameClient(String n){
		if(!serv.checkNameClient(n)){
			out.println("ACCESSDENIED. Reconnectez-vous avec un autre nom.");
			out.flush();
			disconnectUser();
			return false;
		}
		nomClient = n;
		serv.addConnectedUser(nomClient);
		isConnected = true;
		return true;
	}

	/**
	 * Signifie à tous les clients connectés la connexion de USER (COMMANDE CONNECTED)
	 */
	public void userConnected(){
		for (int i = 0; i < serv.out.size(); i++) {
			serv.out.get(i).println("CONNECTED/" + nomClient + "/");
			serv.out.get(i).flush();
			serv.out.get(i).println("LIST/"+serv.getConnectedUserString());
			serv.out.get(i).flush();	
		}
	}

	/**
	 * Déconnecte l'utilisateur et signifie à tous les clients connectés la deconnexion de USER (COMMANDE EXITED)
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
					serv.out.get(i).println("LIST/"+serv.getConnectedUserString());
					serv.out.get(i).flush();	
				}
			
		}catch(IOException e){
			System.err.println("Problème dans la déconnexion du client");
		}
	}

	public synchronized void gestionJam(){
		String commande;
		String [] param;
		synchronized (this) {
			if (serv.getJam().getNbConnecte() < serv.getJam()
					.getMax()) {
				serv.getJam().addConnecte();
				if (serv.getJam().getNbConnecte() == 1) {
					out.println("EMPTY_SESSION");
					out.flush();
					proprietaire = true;

					//						while(true){
					//							commande = in.readLine();
					//							if(commande == null){
					//								disconnectUser();
					//								return;
					//							}
					//							param = commande.split("/");
					//							if(param[0].equals("SET_OPTIONS") && param.length == 3){
					//								break;
					//							}
					//							if(param[0].equals("EXIT")){
					//								disconnectUser();
					//								return;
					//							}
					//							out.println("Commande Invalide. Paramètres la JAM avec SET_OPTIONS/style/tempo");
					//							out.flush();
					//						}
					//						serv.getJam().setStyle(param[1]);
					//						serv.getJam().setTempo(param[2]);
					//
					//						out.println("ACK_OPTS");
					//						out.flush();
				} else {
					out.println("CURRENT_SESSION/"
							+ serv.getJam().getStyle() + "/"
							+ serv.getJam().getTempo() + "/"
							+ serv.getJam().getNbConnecte() + "/");
					out.flush();
				}
			} else {
				out.println("FULL_SESSION");
				out.flush();

				fullSession = true;
			}
		}
	}
}

