package view;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import javax.swing.ScrollPaneConstants;

import cliente.Cliente;

public class ClienteView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldIp;
	private JTextField textFieldPuerto;
	private JTextField textFieldUser;
	private JTextField textFieldDestinatario;
	private JTextField textField;
	private JList<String> list;
	private DefaultListModel<String> modeloLista;
	private JLabel lblConexion;
	private JButton btnConectar;
	private JButton btnEnviar;
	
	private Cliente cliente;

	/**
	 * Create the frame.
	 */
	public ClienteView(Cliente cliente) {
		this.cliente = cliente;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		
		JLabel lblIp = new JLabel("IP:");
		lblIp.setFont(new Font("Verdana", Font.BOLD, 15));
		lblIp.setBounds(10, 17, 40, 14);
		contentPane.add(lblIp);
		
		textFieldIp = new JTextField();
		textFieldIp.setFont(new Font("Verdana", Font.PLAIN, 14));
		textFieldIp.setText("127.0.0.1");
		textFieldIp.setBounds(43, 16, 96, 20);
		contentPane.add(textFieldIp);
		textFieldIp.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setFont(new Font("Verdana", Font.BOLD, 15));
		lblPuerto.setBounds(159, 17, 71, 14);
		contentPane.add(lblPuerto);
		
		textFieldPuerto = new JTextField();
		textFieldPuerto.setFont(new Font("Verdana", Font.PLAIN, 14));
		textFieldPuerto.setText("5000");
		textFieldPuerto.setColumns(10);
		textFieldPuerto.setBounds(238, 16, 47, 20);
		contentPane.add(textFieldPuerto);
				
		JLabel lblUser = new JLabel("User:");
		lblUser.setFont(new Font("Verdana", Font.BOLD, 15));
		lblUser.setBounds(295, 17, 59, 14);
		contentPane.add(lblUser);
		
		textFieldUser = new JTextField();
		textFieldUser.setFont(new Font("Verdana", Font.PLAIN, 14));
		textFieldUser.setColumns(10);
		textFieldUser.setBounds(349, 16, 120, 20);
		contentPane.add(textFieldUser);

		btnConectar = new JButton("Conectar");
		btnConectar.setFont(new Font("Verdana", Font.PLAIN, 14));
		btnConectar.setBounds(672, 13, 102, 23);
		btnConectar.addActionListener(this);
		contentPane.add(btnConectar);		
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 50, 770, 2);
		contentPane.add(separator);
		
		modeloLista = new DefaultListModel<>();
		list = new JList<>(modeloLista);
		list.setForeground(Color.BLACK);
		list.setFont(new Font("Verdana", Font.PLAIN, 15));
		list.setFixedCellHeight(-1); // altura variable de celdas
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JTextArea textArea = new JTextArea(value.toString());
				textArea.setFont(new Font("Verdana", Font.PLAIN, 14));
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textArea.setOpaque(true);
				textArea.setEditable(false);
				textArea.setFocusable(false);
				
				int width = list.getWidth();
				if (width > 0) {
					textArea.setSize(width, Short.MAX_VALUE);
				}
				
				if (isSelected) {
					textArea.setBackground(list.getSelectionBackground());
					textArea.setForeground(list.getSelectionForeground());
				} else {
					textArea.setBackground(list.getBackground());
					textArea.setForeground(list.getForeground());
				}
				
				return textArea;
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 69, 764, 389);
		contentPane.add(scrollPane);
		
		JLabel lblDestinatario = new JLabel("Para:");
		lblDestinatario.setFont(new Font("Verdana", Font.BOLD, 15));
		lblDestinatario.setBounds(20, 481, 59, 14);
		contentPane.add(lblDestinatario);
		
		textFieldDestinatario = new JTextField();
		textFieldDestinatario.setFont(new Font("Verdana", Font.PLAIN, 14));
		textFieldDestinatario.setColumns(10);
		textFieldDestinatario.setBounds(72, 478, 88, 20);
		contentPane.add(textFieldDestinatario);
		
		JLabel lblMensaje = new JLabel("Mensaje:");
		lblMensaje.setFont(new Font("Verdana", Font.BOLD, 15));
		lblMensaje.setBounds(170, 478, 147, 20);
		contentPane.add(lblMensaje);
		
		textField = new JTextField();
		textField.setFont(new Font("Verdana", Font.PLAIN, 14));
		textField.setColumns(10);
		textField.setBounds(251, 478, 425, 20);
		textField.addActionListener(this);
		contentPane.add(textField);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setFont(new Font("Verdana", Font.PLAIN, 14));
		btnEnviar.setBounds(686, 477, 88, 23);
		btnEnviar.setEnabled(false);
		btnEnviar.addActionListener(this);
		contentPane.add(btnEnviar);
		
		lblConexion = new JLabel(" ● Desconectado");
		lblConexion.setForeground(Color.RED);
		lblConexion.setFont(new Font("Verdana", Font.BOLD, 15));
		lblConexion.setBounds(501, 17, 161, 14);
		contentPane.add(lblConexion);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == btnConectar) {
			if (!cliente.estaConectado()) {
				conectar();
			} else {
				cliente.desconectar();
			}
		} else if (source == btnEnviar || source == textField) {
			enviarMensaje();
		}
	}
	
	private void conectar() {
		String ip = textFieldIp.getText().trim();
		String puertoStr = textFieldPuerto.getText().trim();
		String nick = textFieldUser.getText().trim();
		
		if (nick.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Debes introducir un nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			int puerto = Integer.parseInt(puertoStr);
			cliente.conectar(ip, puerto, nick);
			
			// Actualizar UI
			lblConexion.setText(" ● Conectado");
			lblConexion.setForeground(Color.GREEN);
			btnConectar.setText("Desconectar");
			btnEnviar.setEnabled(true);
			textFieldIp.setEnabled(false);
			textFieldPuerto.setEnabled(false);
			textFieldUser.setEnabled(false);
			
			// Mostrar información de ayuda
			mostrarMensaje("Conectado correctamente");
			mostrarMensaje("Escribe 'list' para ver usuarios conectados");
			mostrarMensaje("Usa el campo 'Para:' para enviar mensajes privados");
			
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Puerto inválido", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Error al recibir datos", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void enviarMensaje() {
		if (!cliente.estaConectado()) {
			return;
		}
		
		String destinatario = textFieldDestinatario.getText().trim();
		String mensaje = textField.getText().trim();
		
		if (mensaje.isEmpty()) {
			return;
		}
		
		try {
			cliente.enviarMensaje(destinatario, mensaje);
			textField.setText("");
		} catch (IOException e) {
			mostrarMensaje("Error al enviar mensaje: " + e.getMessage());
		}
	}
	
	public void mostrarMensaje(String mensaje) {
		EventQueue.invokeLater(() -> {
			modeloLista.addElement(mensaje);
			// Auto-scroll al final
			int lastIndex = modeloLista.getSize() - 1;
			if (lastIndex >= 0) {
				list.ensureIndexIsVisible(lastIndex);
			}
		});
	}
	
	public void actualizarEstadoDesconectado() {
		EventQueue.invokeLater(() -> {
			lblConexion.setText(" ● Desconectado");
			lblConexion.setForeground(Color.RED);
			btnConectar.setText("Conectar");
			btnEnviar.setEnabled(false);
			textFieldIp.setEnabled(true);
			textFieldPuerto.setEnabled(true);
			textFieldUser.setEnabled(true);
			
			mostrarMensaje("Desconectado del servidor");
			mostrarMensaje("");
		});
	}
}
