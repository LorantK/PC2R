package serveur;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * Recupere les buffers et les ajoute dans la hashmap
 * @author Eric
 *
 */
public class ServiceAudioReception extends Thread{
	protected Socket canalAudio;
	protected String nomClient;
	protected DataInputStream inAudio;
	protected PrintStream outAudio;
	protected Server s;

	public ServiceAudioReception(Socket Canalaudio, String nomClient, Server s){

		this.canalAudio = Canalaudio;
		this.nomClient = nomClient;
		this.s = s;
		try {
			inAudio = new DataInputStream(canalAudio.getInputStream());
			outAudio = new PrintStream(canalAudio.getOutputStream(), true);
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
		try {
			String commande;
			while(true){
				byte[] buffer = new byte[44100];
				float[] f;
				commande = inAudio.readLine(); // On recupere le message contenant le buffer
				System.out.println(commande);
				String [] tab = commande.split("/");
				if(tab[0].equals("AUDIO_CHUNK")){ // Si c'est le message contenant le buffer
					if(tab.length != 3){
						outAudio.println("AUDIO_OK"); 
						int tick = Integer.parseInt(tab[1]);
						f = ByteBuffer.wrap(tab[2].getBytes()).asFloatBuffer().array(); //String-> byte[] -> float[]
						int numero = s.addBuffer(tick, f); // Ajout du buffer dans la hashmap
						// le numero servira pour le mixage 
						ServiceAudioEmission s1 = new ServiceAudioEmission(outAudio, nomClient, s, tick, numero);
					}
					else{
						outAudio.println("AUDIO_KO"); 
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
