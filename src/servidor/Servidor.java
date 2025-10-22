package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

	public static final int PUERTO=5000;
	
	public void iniciar() {
		ServerSocket servidor = null;
		Socket cliente = null;
		ObjectInputStream entrada = null;
		ObjectOutputStream salida = null;
		String nick = null;
		
		
			try {
				servidor = new ServerSocket(PUERTO);
				System.out.println("Esperando conexiones....");
				cliente = servidor.accept();
				System.out.println("Cliente conectado: ");
				
				salida = new ObjectOutputStream(cliente.getOutputStream());
				salida.flush();
				entrada = new ObjectInputStream(cliente.getInputStream());
				
				salida.writeObject("Introduce tu nick");
				salida.flush();
				System.out.println("Esperando nick.....");
				Object obj = entrada.readObject();
				System.out.println("Nick recibido: ");
				
				
				String respuesta;
				
				if (!(obj instanceof String)) {
					respuesta = "ERROR EN EL FORMATO Se esperaba un String ";
				}
				else {			
					nick =((String) obj).trim();
					if (nick.isEmpty()) {
						respuesta = "Nick vacio";
					}
					else {
						respuesta = "SYS | Bienvenido "+nick;
					}
					}
				salida.writeObject(respuesta);
				
				boolean seguirCliente=true;
				while(seguirCliente) {
					Object objMsg = entrada.readObject();
					if (!(objMsg instanceof String)) {
						salida.writeObject("ERROR Se esperaba String");
						salida.flush();
						continue;
					}
					String linea = ((String )objMsg).trim();
					if (linea.equalsIgnoreCase("QUIT")) {
						salida.writeObject("SYS|AGUR ");
						salida.flush();
						seguirCliente=false;
					}
					else if(linea.startsWith("msg")) {
						String texto = linea.substring(4);
						salida.writeObject("PUB | "+nick+" | "+texto);
						salida.flush();
					}
					else {
						salida.writeObject("ERROR comando no reconocido");
					}
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
					try {
						if (entrada!=null) entrada.close();
						if (salida!=null) salida.close();
						if (cliente != null) cliente.close();
						if (servidor !=null) servidor.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Fin del servidor");
			}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		new Servidor().iniciar();
		
	}
}
