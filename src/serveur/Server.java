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

public class Server extends Thread{
	private ServerSocket serv;
	private ServerSocket cAudio;
	private Jam j;
	private int port;
	private int portA = 2015;
	private int timeout = 1000;  // en millisecondes. type a changer plus tard si necessaire
	private int MAX = 4;
	private ArrayList<String> connectedUser;

	public static ArrayList<PrintStream> out;
	public static ArrayList<PrintStream> outAudio;
	public HashMap<String, String> db;
	public static HashMap<Integer, ArrayList<float[]>> tabBuffer;

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
//			cAudio.setReuseAddress(true);
//			cAudio.bind(new InetSocketAddress(portA));
			System.out.println("Récupération de la base de donnée");
			BufferedReader br = null;
			try {
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

			while(true){
				Socket client = serv.accept();
				System.out.println("User connected");
				Service s = new Service(client, this);				
			}
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	public void addOut(PrintStream o){
		out.add(o);
	}

	public void disconnectOut(PrintStream o){
		out.remove(o);
	}

	public void addOutAudio(PrintStream o){
		outAudio.add(o);
	}

	public ServerSocket getcAudio(){
		return cAudio;
	}

	public Jam getJam(){
		return j;
	}
	
	public int getTimeout() {
		return timeout;
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

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

	public synchronized boolean register(String user, String pw) throws IOException{
		if(db.containsKey(user)){
			return false;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("CompteUtil",true));
		
		bw.write(user);
		bw.write("/");
		bw.write(pw);
		bw.write("\n");
		bw.close();
		return true;
	}
	
	public synchronized ArrayList<String> getConnectedUser() {
		return connectedUser;
	}

	public synchronized String getConnectedUserString() {
		String s="";
		for(int i = 0;i<connectedUser.size()-1;i++){
			s=s+connectedUser.get(i)+"/";
		}
		return s+connectedUser.get(connectedUser.size()-1);
	}

	public void setConnectedUser(ArrayList<String> connectedUser) {
		this.connectedUser = connectedUser;
	}
	
	public synchronized void addConnectedUser(String n){
		this.connectedUser.add(n);
	}
	
	public synchronized void deleteUser(String n){
		this.connectedUser.remove(n);
	}
	
	public synchronized boolean checkNameClient(String n){
		if(db.containsKey(n) ||connectedUser.contains(n)){ // Si le nom est deja reserve
			return false;
		}
		return true;
	}


	public static ArrayList<PrintStream> getOutAudio() {
		return outAudio;
	}


	public static void setOutAudio(ArrayList<PrintStream> outAudio) {
		Server.outAudio = outAudio;
	}


	public synchronized HashMap<Integer, ArrayList<float[]>> getTabBuffer() {
		return tabBuffer;
	}


	public static void setTabBuffer(HashMap<Integer, ArrayList<float[]>> tabBuffer) {
		Server.tabBuffer = tabBuffer;
	}
	
	/**
	 * Ajoute le buffer dans le tableau et retourne l'indice de la case dans laquelle il est ajoute
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
	
//	public ArrayList<float[]> getBuffer(int tick){
//		if(tabBuffer.get(tick) == null){
//			tabBuffer.put(tick, new ArrayList<float[]>());
//		}
//		return tabBuffer.get(tick);
//	}
}
