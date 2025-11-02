package cliente;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import util.Util;
import view.ClienteView;

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
		System.out.println(resp);
		
		final ObjectInputStream entradaFinal = entrada;
		final ObjectOutputStream salidaFinal = salida;
		
		System.out.println("Comandos disponibles:");
		System.out.println("  msg <texto>             - Enviar mensaje público");
		System.out.println("  priv <usuario> <texto>  - Enviar mensaje privado");
		System.out.println("  list                    - Ver usuarios conectados");
		System.out.println("  QUIT                    - Salir del chat");
		System.out.println();		// Crear hilo para escuchar mensajes del servidor
		Thread hiloEscucha = new Thread(() -> {
		    try {
		        while (true) {
		            Object r = entradaFinal.readObject();
		            if (r instanceof String) {
		                String mensajeServidor = (String) r;
		                System.out.println( mensajeServidor);
		                if (mensajeServidor.startsWith("SYS|AGUR")) {
		                    break; // El servidor cerró la conexión
		                }
		            }
		        }
		    } catch (IOException | ClassNotFoundException e) {
		        System.out.println("Conexión con servidor perdida");
		    }
		});
		hiloEscucha.setDaemon(true);
		hiloEscucha.start();

		// bucle de envío
		boolean seguir = true;
		while (seguir) {
		    System.out.println("Introduce un mensaje: ('msg')");
		    String linea = Util.introducirCadena();  // lee teclado

		    // enviar
		    salidaFinal.writeObject(linea);
		    salidaFinal.flush();

		    // verificar si el usuario quiere salir
		    if (linea.trim().equalsIgnoreCase("QUIT")) {
		        seguir = false;
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
		EventQueue.invokeLater(() -> {
			try {
				ClienteView frame = new ClienteView();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}