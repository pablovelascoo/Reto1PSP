package cliente;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import view.ClienteView;

public class Cliente {
	private Socket socket;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private Thread hiloEscucha;
	private boolean conectado;
	private ClienteView view;
	private String nick;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Cliente cliente = new Cliente();
				ClienteView frame = new ClienteView(cliente);
				cliente.setView(frame);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Cliente() {
		this.conectado = false;
	}

	public void conectar(String ip, int puerto, String nick) throws IOException, ClassNotFoundException {
		// Crear conexión
		socket = new Socket(ip, puerto);
		salida = new ObjectOutputStream(socket.getOutputStream());
		salida.flush();
		entrada = new ObjectInputStream(socket.getInputStream());
		this.nick = nick;

		// Leer mensaje de bienvenida
		String mensaje = (String) entrada.readObject();
		notificarMensaje(mensaje);

		// Enviar nick
		salida.writeObject(nick);
		salida.flush();

		// Recibir respuesta
		String resp = (String) entrada.readObject();
		notificarMensaje(resp);

		conectado = true;
		iniciarHiloEscucha();
	}

	public void desconectar() {
		if (conectado) {
			try {
				if (salida != null) {
					salida.writeObject("QUIT");
					salida.flush();
				}
			} catch (IOException e) {
				// Ignorar errores al desconectar
			}
			cerrarConexion();
		}
	}

	private void cerrarConexion() {
		try {
			if (entrada != null)
				entrada.close();
			if (salida != null)
				salida.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// Ignorar errores al cerrar
		}

		conectado = false;
		socket = null;
		entrada = null;
		salida = null;
	}

	public void enviarMensaje(String destinatario, String mensaje) throws IOException {
		if (!conectado)
			return;

		String mensajeCompleto;
		if (mensaje.equalsIgnoreCase("list") || mensaje.equalsIgnoreCase("ls")) {
			mensajeCompleto = "list";
		} else if (!destinatario.isEmpty()) {
			mensajeCompleto = "priv " + destinatario + " " + mensaje;
		} else {
			mensajeCompleto = "msg " + mensaje;
		}

		salida.writeObject(mensajeCompleto);
		salida.flush();
	}

	private void iniciarHiloEscucha() {
		hiloEscucha = new Thread(() -> {
			try {
				while (conectado) {
					Object r = entrada.readObject();
					if (r instanceof String) {
						String mensajeServidor = (String) r;
						notificarMensaje(mensajeServidor);
						if (mensajeServidor.startsWith("SYS|AGUR")) {
							break;
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				if (conectado) {
					notificarMensaje("Conexión con servidor perdida");
				}
			} finally {
				cerrarConexion();
				if (view != null) {
					view.actualizarEstadoDesconectado();
				}
			}
		});
		hiloEscucha.setDaemon(true);
		hiloEscucha.start();
	}

	private void notificarMensaje(String mensaje) {
		if (view != null) {
			view.mostrarMensaje(mensaje);
		}
	}

	public void setView(ClienteView view) {
		this.view = view;
	}

	public boolean estaConectado() {
		return conectado;
	}

}