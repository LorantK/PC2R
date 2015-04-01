package serveur;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
	private ServerSocket serv;
	private ServerSocket cAudio;
	private Jam j;
	private int port;
	private int portA;
	public static ArrayList<PrintStream> out;
	public static ArrayList<PrintStream> outAudio;
	public String ficCompte;

	public Server() throws IOException{
		out = new ArrayList<PrintStream>();
		outAudio = new ArrayList<PrintStream>();
		j = new Jam(10);
		port = 2013;
		portA = 2015;
		ficCompte = "/users/nfs/Etu2/3100192/workspace/Network_Musical_Jammin/CompteUtil";
		
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
}
