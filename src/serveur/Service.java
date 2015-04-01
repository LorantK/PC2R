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

	@Override
	public void run() {
		String commande;
		String[] param;
		try {
			while (true) {
				commande = in.readLine();
				param = commande.split("/");
				if(param[0].equals("CONNECT")){
					break;
				}
				out.println("Commande Invalide. Retapez une commande pour vous connecter");
				out.flush();
			}

			System.out.println("Nouvelle connexion");
			start = true;
			nomClient = param[1];

			out.println("WELCOME/" + nomClient);
			out.flush();
			out.println("AUDIO_PORT/2015/");
			out.flush();

			Canalaudio = serv.getcAudio().accept();
			ServiceAudio s = new ServiceAudio(Canalaudio, nomClient, serv);

			out.println("AUDIO_OK");
			out.flush();

			for (int i = 0; i < serv.out.size(); i++) {
				serv.out.get(i).println("CONNECTED/" + nomClient + "/");
				serv.out.get(i).flush();
			}

			synchronized (this) {
				if (serv.getJam().getNbConnecte() < serv.getJam()
						.getMax()) {
					serv.getJam().addConnecte();
					if (serv.getJam().getNbConnecte() == 1) {

						out.println("EMPTY_SESSION");
						out.flush();

						while(true){
							commande = in.readLine();
							param = commande.split("/");
							if(param[0].equals("SET_OPTIONS") && param.length == 3){
								break;
							}
							out.println("Commande Invalide. Retapez une commande pour paramètrer la JAM");
							out.flush();
						}
						serv.getJam().setStyle(param[1]);
						serv.getJam().setTempo(param[2]);

						out.println("ACK_OPTS");
						out.flush();
					} else {
						out.println("CURRENT_SESSION/"
								+ serv.getJam().getStyle() + "/"
								+ serv.getJam().getTempo() + "/"
								+ serv.getJam().getNbConnecte() + "/");
					}
				} else {
					out.println("FULL_SESSION");
					out.flush();

					fullSession = true;
				}
			}

			while(true){

				commande = in.readLine();
				System.out.println(commande);
				param = commande.split("/");
				switch (param[0]) {

				case "EXIT":
					if (start) {
						for (int i = 0; i < serv.out.size() - 1; i++) {
							serv.out.get(i)
							.println("EXITED/" + nomClient + "/");
							serv.out.get(i).flush();
						}
						serv.getJam().disconnect();
						client.close();
						Canalaudio.close();
						return;
					}
					break;

				case "SET_OPTIONS":
					if(proprietaire){
						serv.getJam().setStyle(param[1]);
						serv.getJam().setStyle(param[2]);

					}
					break;

				default:
					break;
				}
			}
		} catch (IOException e) {
		}
	}
}
