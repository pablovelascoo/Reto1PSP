package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManejadorCliente implements Runnable {

	private String nick;
	private final Socket socket;
	private ObjectInputStream in;
	private final Object outLock = new Object(); // sincronizacion
	private ObjectOutputStream out;
	private final Servidor server;
	
	public ManejadorCliente(Socket socket, Servidor server) {
		this.socket = socket;
		this.server = server;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void enviarMensaje(String mensaje) {
		try {
			synchronized(outLock) {
				if (out != null) {
					out.writeObject(mensaje);
					out.flush();
				}
			}
		} catch (IOException e) {
			System.err.println("Error enviando mensaje a " + nick + ": " + e.getMessage());
		}
	}
	
	@Override
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			
			out.writeObject("Introduce tu nick");
			out.flush();
			System.out.println("Esperando nick.....");
			
			Object obj = in.readObject();
			System.out.println("Nick recibido: " + obj);
			
			String respuesta;
			
			if (!(obj instanceof String)) {
				respuesta = "ERROR EN EL FORMATO Se esperaba un String ";
				out.writeObject(respuesta);
				out.flush();
				return;
			} else {			
				nick = ((String) obj).trim();
				if (nick.isEmpty()) {
					respuesta = "Nick vacio";
					out.writeObject(respuesta);
					out.flush();
					return;
				} else {
					if (server.nickEnUso(nick)) {
						respuesta = "ERROR | Nick ya en uso";
						out.writeObject(respuesta);
						out.flush();
						return;
					}
					respuesta = "SYS | Bienvenido " + nick;
					server.agregarCliente(nick, this);
				}
			}
			
			out.writeObject(respuesta);
			out.flush();
			
			boolean seguirCliente = true;
			while (seguirCliente) {
				Object objMsg = in.readObject();
				if (!(objMsg instanceof String)) {
					out.writeObject("ERROR Se esperaba String");
					out.flush();
					continue;
				}
				
				String linea = ((String) objMsg).trim();
				if (linea.equalsIgnoreCase("QUIT")) {
					out.writeObject("SYS|AGUR ");
					out.flush();
					seguirCliente = false;
				} else if (linea.startsWith("msg ")) {
					String texto = linea.substring(4);
					String mensajeCompleto = "PUB | " + nick + " | " + texto;
					
					// Enviar a todos clientes 
					server.difundirMensaje(mensajeCompleto, this);
					
				} else if (linea.startsWith("priv ")) {
					// Formato: priv destinatario mensaje
					String[] partes = linea.substring(5).split(" ", 2);
					if (partes.length >= 2) {
						String destinatario = partes[0];
						String mensaje = partes[1];
						server.enviarMensajePrivado(mensaje, destinatario, this);
					} else {
						out.writeObject("ERROR | Formato: priv <usuario> <mensaje>");
						out.flush();
					}
				} else if (linea.equalsIgnoreCase("list")) {
					server.listarUsuarios(this);
				} else {
					out.writeObject("ERROR | Comandos: msg <texto>, priv <usuario> <mensaje>, list, QUIT");
					out.flush();
				}
			}
			
		} catch (IOException e) {
			System.err.println("Error de I/O con cliente " + nick + ": " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Error de clase con cliente " + nick + ": " + e.getMessage());
		} finally {
			server.removerCliente(nick);
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				if (socket != null) socket.close();
			} catch (IOException e) {
				System.err.println("Error cerrando recursos: " + e.getMessage());
			}
			System.out.println("Cliente " + nick + " desconectado");
		}
	}
}
