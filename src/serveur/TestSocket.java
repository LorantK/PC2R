package serveur;

import java.io.IOException;

public class TestSocket {

	public static void main(String[] args) {
		try {
			Server s = new Server();
			s.start();
		} catch (IOException e) {
			System.err.println("Problème dans le serveur");
		}

	}

}
