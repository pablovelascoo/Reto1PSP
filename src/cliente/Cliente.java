package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import util.Util;

public class Cliente {

	private static final String IP = "127.0.0.1";
    private static final int PUERTO = 5000;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		boolean valido=false;
		while(!valido) {
			System.out.println("Introduce tu nick: ");
			String nick=Util.introducirCadena().trim();
		
			if (!nick.isEmpty()) {
				valido=true;
			}
			try(Socket socket = new Socket(IP,PUERTO)){
				
				
				
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error al conectar con el servidor " + e.getMessage());
			} 
		}
		
	}

}
