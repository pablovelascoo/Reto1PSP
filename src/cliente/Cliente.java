package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import util.Util;

public class Cliente {

	private static final String IP = "127.0.0.1";
    private static final int PUERTO = 5000;
    
    public void iniciar() {
    	Socket cliente = null;
    	ObjectInputStream entrada = null;
    	ObjectOutputStream salida = null;
    	
    	try {
			cliente = new Socket (IP,PUERTO);
			System.out.println("Conexion realizada con servidor: ");
			salida = new ObjectOutputStream(cliente.getOutputStream());
			salida.flush();
			entrada = new ObjectInputStream(cliente.getInputStream());
			
			String mensaje = (String) entrada.readObject();
			System.out.println(mensaje);
			
			String nick = Util.introducirCadena();
			salida.writeObject(nick);
			salida.flush();
			
			String resp = (String) entrada.readObject();
			System.out.println("Servidor: " + resp);
			
			// ... ya leíste "Introduce tu nick", enviaste el nick y leíste "Bienvenido"
			System.out.println("Puedes escribir: MSG|texto  o  QUIT");

			// 🔽 NUEVO: bucle de envío/recepción
			boolean seguir = true;
			while (seguir) {
				System.out.println("Introduce un mensaje: ('msg')");
			    String linea = Util.introducirCadena();  // lee teclado

			    // enviar
			    salida.writeObject(linea);
			    salida.flush();

			    // leer respuesta del servidor
			    Object r = entrada.readObject();
			    if (r instanceof String) {
			        resp = (String) r;
			        System.out.println("Servidor: " + resp);
			        if (resp.startsWith("SYS|Adiós")) {
			            seguir = false; // el servidor cierra esta sesión
			        }
			    } else {
			        System.out.println("Servidor envió objeto inesperado");
			    }
			}

			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (entrada !=null ) entrada.close();
				if (salida != null ) salida.close();
				if (cliente != null ) cliente.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Fin cliente");
		}
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Cliente().iniciar();
	}

}
