package serveur;

import java.io.IOException;

public class TestSocket {

	public static void main(String[] args) {
		try {
			Server s = new Server(4, 1000, 2015);
			s.start();
		} catch (IOException e) {
			System.err.println("Probleme dans le serveur");
		}

	}

}
