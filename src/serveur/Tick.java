package serveur;

import java.util.ArrayList;

public class Tick extends Thread {
	private int numero;
	private Server s;

	public Tick(int numero, Server s){
		this.numero = numero;
		this.s = s;
		this.start();
	}

	public void run(){
		while(true){
			ArrayList<float[]> listBuff = s.getTabBuffer().get(numero);
			if(listBuff != null){
				if(listBuff.size() == s.getJam().getNbConnecte()){
					this.notifyAll();
					break;
				}
			}
		}
	}
}
