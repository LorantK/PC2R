package serveur;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServiceAudio extends Thread{
	protected Socket canalAudio;
	protected String nomClient;
	protected DataInputStream inAudio;
	protected PrintStream outAudio;
	protected Server s;

	public ServiceAudio(Socket Canalaudio, String nomClient, Server s){

		this.canalAudio = Canalaudio;
		this.nomClient = nomClient;
		this.s = s;
		try {
			inAudio = new DataInputStream(Canalaudio.getInputStream());
			outAudio = new PrintStream(canalAudio.getOutputStream());

			synchronized(this){
				s.addOutAudio(outAudio);
			}
		} catch (IOException e) {
			try{
				canalAudio.close();
			}catch (IOException e1) {}
			System.err.println(e.getMessage());
			return;
		}

		this.start();
	}

	public void run()  {
		
		while(true){}

	}
}
