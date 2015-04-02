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
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Thread{
	private ServerSocket serv;
	private ServerSocket cAudio;
	private Jam j;
	private int port;
	private int portA;
	public static ArrayList<PrintStream> out;
	public static ArrayList<PrintStream> outAudio;
	public String ficCompte;
	public HashMap<String, String> db;

	public Server() throws IOException{
		out = new ArrayList<PrintStream>();
		outAudio = new ArrayList<PrintStream>();
		j = new Jam(10);
		port = 2013;
		portA = 2015;
		ficCompte = "/users/nfs/Etu2/3100192/workspace/Network_Musical_Jammin/CompteUtil";
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

	public String getFicCompte(){
		return ficCompte;
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
}
