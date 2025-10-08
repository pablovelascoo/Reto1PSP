package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	public static final int puerto=5000;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		boolean seguir=true;
		
		try (ServerSocket servidor = new ServerSocket(puerto)){
			
			System.out.println("Servidor iniciado... (Escuchando en el puerto: "+puerto+")");
			
			while(seguir) {
				Socket cliente =servidor.accept();
				System.out.println("Cliente conectado "+cliente.getRemoteSocketAddress());
				cliente.close();
			}
			
			servidor.close();
			
			System.out.println("Servidor finalizado");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error en el servidor: "+ e.getMessage());
		}
	}

}
