package serveur;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;

public class ServiceAudioEmission extends Thread {
	protected String nomClient;
	protected PrintStream outAudio;
	protected Server s;
	protected int tick;
	protected int numero;

	public ServiceAudioEmission(PrintStream outAudio, String nomClient, Server s, int tick, int numero){

		this.outAudio = outAudio;
		this.nomClient = nomClient;
		this.s = s;
		this.tick = tick;
		this.numero = numero;
		this.start();
	}

	public void run(){
		try {
			s.getJam().getListTick().get(tick).wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// On recupere le buffer le plus grand
			int MaxTailleBuff = 0;

			for(int i = 0; i < s.getTabBuffer().get(tick).size(); i++){
				if(s.getTabBuffer().get(tick).get(i).length > MaxTailleBuff);
					MaxTailleBuff = s.getTabBuffer().get(tick).get(i).length;
			}

			float[] BufferMixe = new float[MaxTailleBuff];
			for(int i = 0; i < MaxTailleBuff; i++){
				BufferMixe[i] = 0;
			}
			
			// On mix
			for(int i = 0; i < s.getTabBuffer().get(tick).size(); i++){
				if(i == numero) // On ne mixe pas lui meme
					continue;
				
				boolean depassement= false;
				float max = -100000;
				float[] f = s.getTabBuffer().get(tick).get(i);
				for(int j = 0; j < f.length; j++){
					BufferMixe[j] = BufferMixe[j] + f[j];
					if(BufferMixe[j] > 1 ||BufferMixe[j] < 1)
						depassement = true;
					
					if(BufferMixe[j] > max)
						max = BufferMixe[j];
				}
				if(depassement){
					for(int j = 0; j < f.length; j++){
						BufferMixe[j] = BufferMixe[j] / max;
					}	
				}
			}
			
			outAudio.println("AUDIO_MIX/" + Arrays.toString(BufferMixe));
		}
	}
}
