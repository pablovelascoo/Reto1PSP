package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
	
	private final Map<String, ManejadorCliente> conectados = new HashMap<>();
	private final List<String> logMensajes = new ArrayList<>();
	private final Object lock = new Object(); 
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final LocalDateTime tiempoInicio = LocalDateTime.now();
	private String ultimoMensaje = "Ningún mensaje aún";

	public static final int PUERTO = 5000;
	public static final int MAX_CLIENTES = 10; 
	
	private void log(String accion) {
		String timestamp = LocalDateTime.now().format(formatter);
		String logEntry = "[" + timestamp + "] " + accion;
		synchronized(lock) {
			logMensajes.add(logEntry);
			if (accion.contains("Mensaje")) {
				ultimoMensaje = accion;
			}
		}
		System.out.println("LOG: " + logEntry);
	}
	
	private void iniciarMonitorEstado() {
		Thread monitor = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(10000); // Cada 10 segundos
					mostrarEstadoServidor();
				} catch (InterruptedException e) {
					System.err.println("Monitor de estado interrumpido");
					break;
				}
			}
		});
		monitor.setDaemon(true);
		monitor.start();
		System.out.println("Monitor de estado iniciado (cada 10 segundos)");
	}
	
	private void mostrarEstadoServidor() {
		synchronized(lock) {
			Duration tiempoActivo = Duration.between(tiempoInicio, LocalDateTime.now());
			long horas = tiempoActivo.toHours();
			long minutos = tiempoActivo.toMinutesPart();
			long segundos = tiempoActivo.toSecondsPart();
			
			System.out.println("\n" + "=".repeat(60));
			System.out.println(" ESTADO DEL SERVIDOR - " + LocalDateTime.now().format(formatter));
			System.out.println("=".repeat(60));
			System.out.println(" Usuarios conectados: " + conectados.size() + "/" + MAX_CLIENTES);
			System.out.println(" Tiempo activo: " + horas + "h " + minutos + "m " + segundos + "s");
			System.out.println(" Último mensaje: " + ultimoMensaje);
			if (conectados.size() > 0) {
				System.out.print(" Usuarios: ");
				String usuarios = String.join(", ", conectados.keySet());
				System.out.println(usuarios);
			}
			System.out.println("=".repeat(60) + "\n");
		}
	}
	
	public boolean nickEnUso(String nick) {
		synchronized(lock) {
			return conectados.containsKey(nick);
		}
	}
	
	public void agregarCliente(String nick, ManejadorCliente cliente) {
		synchronized(lock) {
			conectados.put(nick, cliente);
			log("Usuario " + nick + " entró al chat. Total conectados: " + conectados.size());
			
			// avisar a todos que hay un nuevo user 
			String notificacion = "SYS | " + nick + " se ha unido al chat";
			for (ManejadorCliente otroCliente : conectados.values()) {
				if (otroCliente != cliente) { // No notificar al recién conectado
					otroCliente.enviarMensaje(notificacion);
				}
			}
		}
	}
	
	public void removerCliente(String nick) {
		if (nick != null) {
			synchronized(lock) {
				conectados.remove(nick);
				log("Usuario " + nick + " salió del chat. Total conectados: " + conectados.size());
				
				// avisar a todos que quien se ha desconectado
				String notificacion = "SYS | " + nick + " ha salido del chat";
				for (ManejadorCliente cliente : conectados.values()) {
					cliente.enviarMensaje(notificacion);
				}
			}
		}
	}
	
	public void difundirMensaje(String mensaje, ManejadorCliente emisor) {
		synchronized(lock) {
			log("Mensaje público de " + emisor.getNick() + ": " + mensaje);
			for (ManejadorCliente cliente : conectados.values()) {
				if (cliente != emisor) { // No enviar al emisor
					cliente.enviarMensaje(mensaje);
				} else {
					// Enviar confirmación al emisor
					cliente.enviarMensaje(mensaje);
				}
			}
		}
	}
	
	public void enviarMensajePrivado(String mensaje, String destinatario, ManejadorCliente emisor) {
		synchronized(lock) {
			ManejadorCliente clienteDestino = conectados.get(destinatario);
			if (clienteDestino != null) {
				log("Mensaje privado de " + emisor.getNick() + " a " + destinatario + ": " + mensaje);
				clienteDestino.enviarMensaje("PRIV | " + emisor.getNick() + " | " + mensaje);
				emisor.enviarMensaje("PRIV | A " + destinatario + " | " + mensaje);
			} else {
				emisor.enviarMensaje("ERROR | Usuario " + destinatario + " no encontrado");
			}
		}
	}
	
	public void listarUsuarios(ManejadorCliente solicitante) {
		synchronized(lock) {
			StringBuilder lista = new StringBuilder("SYS | Usuarios conectados: ");
			for (String nick : conectados.keySet()) {
				lista.append(nick).append(", ");
			}
			if (lista.length() > 2) {
				lista.setLength(lista.length() - 2); // Quitar la ultima coma y espacio
			}
			solicitante.enviarMensaje(lista.toString());
		}
	}
	
	public void iniciar() {
		ServerSocket servidor = null;
		
		try {
			servidor = new ServerSocket(PUERTO);
			System.out.println("Servidor iniciado en puerto " + PUERTO);
			System.out.println("Máximo de clientes permitidos: " + MAX_CLIENTES);
			System.out.println("Esperando conexiones....");
			
			// Iniciar monitor del servidor
			iniciarMonitorEstado();
			
			while (true) {
				Socket cliente = servidor.accept();
				
				// Verificar límite de clientes
				synchronized(lock) {
					if (conectados.size() >= MAX_CLIENTES) {
						try (ObjectOutputStream rechazo = new ObjectOutputStream(cliente.getOutputStream())) {
							rechazo.writeObject("ERROR | Servidor completo. Máximo " + MAX_CLIENTES + " clientes");
							rechazo.flush();
							cliente.close();
							log("Conexión rechazada - Servidor completo (" + conectados.size() + "/" + MAX_CLIENTES + ")");
							continue;
						} catch (IOException e) {
							System.err.println("Error rechazando cliente: " + e.getMessage());
						}
					}
				}
				
				System.out.println("Nuevo cliente conectado desde: " + cliente.getInetAddress());
				
				// Crear un nuevo hilo para manejar este cliente
				ManejadorCliente manejador = new ManejadorCliente(cliente, this);
				Thread hiloCliente = new Thread(manejador);
				hiloCliente.setDaemon(true); // Para que el programa pueda terminar
				hiloCliente.start();
			}
			
		} catch (IOException e) {
			System.err.println("Error en el servidor: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (servidor != null) servidor.close();
			} catch (IOException e) {
				System.err.println("Error cerrando el servidor: " + e.getMessage());
			}
			System.out.println("Fin del servidor");
		}
	}
	
	public static void main(String[] args) {
		new Servidor().iniciar();
	}
}
