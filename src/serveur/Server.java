package serveur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe server. C'est ici qu'il y a toutes les donnees stockees (liste des utilisateurs connectes, les ports, etc.)
 * et les ServerSocket
 * @author Eric
 *
 */
public class Server extends Thread{
	private ServerSocket serv;  // ServerSocket pour le canal principal
	private ServerSocket cAudio; // ServerSocket pour l'audio
	private Jam j;
	private int port; // numero du port pour le canal principal
	private int portA = 2015; // portAudio (par defaut 2015)
	private int timeout = 1000;  // en millisecondes. type a changer plus tard si necessaire
	private int MAX = 4; // Nombre maximum de connecte a la jam (par defaut 4)
	private ArrayList<String> connectedUser; // Liste des utilisateurs connectes

	public static ArrayList<PrintStream> out; // ArrayList des outputstreams (pour envoyer des messages a tous les clients)
	public static ArrayList<PrintStream> outAudio; // ArrayList des outputstreams audio
	public HashMap<String, String> db; // Hashmap contenant les noms et mot de passe des personnes enregistres
	public static HashMap<Integer, ArrayList<float[]>> tabBuffer; // Hashmap contient les buffers audios, Integer correspond au tick

	/**
	 * Constructeur. Le port du canal principal est fixe par defaut a 2013
	 * @param max nombre maximum de personnes connectes
	 * @param timeout le timeout avant de deconnecter un client
	 * @param portAudio le numero du port audio
	 * @throws IOException
	 */
	public Server(int max, int timeout, int portAudio) throws IOException{
		MAX = max;
		this.timeout = timeout;
		portA = portAudio;
		out = new ArrayList<PrintStream>();
		outAudio = new ArrayList<PrintStream>();
		connectedUser = new ArrayList<String>();
		tabBuffer = new HashMap<Integer, ArrayList<float[]>>();
		j = new Jam(MAX, this);
		
		port = 2013;
		
		db = new HashMap<String,String>();
	}


	/**	
	 * En cas de bug : 
	 * lsof -i:2013
	 * kill pid
	 */
	public void run() {
		try {
			System.out.println("Creation du serveur");
			serv = new ServerSocket();
			serv.setReuseAddress(true); 
			serv.bind(new InetSocketAddress(port));
			cAudio = new ServerSocket(portA);
			ServiceChatEcriture c = new ServiceChatEcriture(this); // Creation du service broadcast serveur
			
			System.out.println("Recuperation de la base de donnee"); 
			BufferedReader br = null;
			try { // On recupere la liste des identifiants/mot de passe deja enregistre 
				br = new BufferedReader(new FileReader("CompteUtil"));
				String line=null;
				String[] param;
				while((line = br.readLine()) != null){
					param = line.split("/");
					db.put(param[0], param[1]);
				}
				br.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}

			while(true){ // On se met en attente de la connexion d'un nouveau client
				Socket client = serv.accept();
				System.out.println("User connected");
				Service s = new Service(client, this);				
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	/**
	 * Ajoute le PrintStream a l'arrayList out
	 * @param o 
	 */
	public void addOut(PrintStream o){
		out.add(o);
	}

	/**
	 * On enleve le Printstream de l'arraylist out (en cas de deconnexion d'un client)
	 * @param o
	 */
	public void disconnectOut(PrintStream o){
		out.remove(o);
	}

	/**
	 * Ajoute le printStream a l'arraylist outAudio
	 * @param o
	 */
	public void addOutAudio(PrintStream o){
		outAudio.add(o);
	}

	/**
	 * Getter pour le serverSocket audio
	 * @return
	 */
	public ServerSocket getcAudio(){
		return cAudio;
	}

	/**
	 * Getter pour la jam
	 * @return l'objet JAM
	 */
	public Jam getJam(){
		return j;
	}
	
	/**
	 * Setter pour la jam
	 * @param j
	 */
	public void setJam(Jam j){
		this.j = j;
	}
	
	/**
	 * Getter pour le timeout
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Setter pour le timeout
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Fonction de login. On verifie ici si le nom n'existe pas deja dans la base
	 * @param user identifiant
	 * @param pw mot de passe
	 * @return true si user n'existe pas, false sinon
	 */
	public synchronized boolean login(String user, String pw){
		System.out.println(pw);
		System.out.println(db.get(user));
		if(!db.containsKey(user)){
			return false;
		}
		if(pw.equals(db.get(user))){
			return true;
		}
		return false;
	}

	/**
	 * Fonction d'enregistrement d'un id et mot de passe
	 * @param user identifiant
	 * @param pw mot de passe
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean register(String user, String pw) throws IOException{
		if(db.containsKey(user)){
			return false;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("CompteUtil",true)); 
		// On ecrit dans le fichier les donnees
		bw.write(user);
		bw.write("/");
		bw.write(pw);
		bw.write("\n");
		bw.close();
		return true;
	}
	
	/**
	 * Getter pour la liste des personnes connectes
	 * @return
	 */
	public synchronized ArrayList<String> getConnectedUser() {
		return connectedUser;
	}

	/**
	 * Recuperer les personnes connectes (sous le format Nom1/Nom2/Nom3 ...)
	 * @return String des personnes connectes
	 */
	public synchronized String getConnectedUserString() {
		String s="";
		for(int i = 0;i<connectedUser.size()-1;i++){
			s=s+connectedUser.get(i)+"/";
		}
		return s+connectedUser.get(connectedUser.size()-1);
	}

	/**
	 * Setter Liste des personnes connectes
	 * @param connectedUser
	 */
	public void setConnectedUser(ArrayList<String> connectedUser) {
		this.connectedUser = connectedUser;
	}
	
	/**
	 * Ajoute le String a la liste des personnes connectes
	 * @param n
	 */
	public synchronized void addConnectedUser(String n){
		this.connectedUser.add(n);
	}
	
	/**
	 * Enleve le String passe en parametre de la liste des personnes connectes
	 * @param n
	 */
	public synchronized void deleteUser(String n){
		this.connectedUser.remove(n);
	}
	
	/**
	 * Verifie si le nom passe en parametre existe deja
	 * @param n
	 * @return
	 */
	public synchronized boolean checkNameClient(String n){
		if(db.containsKey(n) ||connectedUser.contains(n)){ // Si le nom est deja reserve
			return false;
		}
		return true;
	}


	/**
	 * Getter des printStream audio
	 * @return
	 */
	public static ArrayList<PrintStream> getOutAudio() {
		return outAudio;
	}


	public static void setOutAudio(ArrayList<PrintStream> outAudio) {
		Server.outAudio = outAudio;
	}


	/**
	 * Recupere la hashmap des buffer audio
	 * @return
	 */
	public synchronized HashMap<Integer, ArrayList<float[]>> getTabBuffer() {
		return tabBuffer;
	}

	/**
	 * Setter hashmap buffer audio
	 * @param tabBuffer
	 */
	public static void setTabBuffer(HashMap<Integer, ArrayList<float[]>> tabBuffer) {
		Server.tabBuffer = tabBuffer;
	}
	
	/**
	 * Ajoute le buffer dans le tableau et retourne l'indice de la case dans laquelle il est ajoute (pour le mixage)
	 * @param tick
	 * @param buffer
	 * @return
	 */
	public synchronized int addBuffer(int tick, float[] buffer){ // 
		if(tabBuffer.get(tick) == null){
			tabBuffer.put(tick, new ArrayList<float[]>());
		}
		tabBuffer.get(tick).add(buffer);
		return tabBuffer.size() - 1;
	}
	
	/**
	 * Getter MAX
	 * @return
	 */
	public int getMax(){
		return MAX;
	}
	
}
