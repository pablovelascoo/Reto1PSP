package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManejadorCliente {

	final String nick;
	final Socket socket;
	final ObjectInputStream in;
	final ObjectOutputStream out;
	final Servidor server;
	
	public ManejadorCliente(String nick, Socket socket, ObjectInputStream in, ObjectOutputStream out,Servidor server) {
		this.nick=nick;
		this.socket = socket;
		this.in=in;
		this.out=out;
		this.server=server;
	}
}
