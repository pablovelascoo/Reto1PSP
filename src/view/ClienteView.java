package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToolBar;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.DropMode;

public class ClienteView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldIp;
	private JTextField textFieldPuerto;
	private JTextField textFieldUser;
	private JTextField textFieldDestinatario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteView frame = new ClienteView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClienteView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		
		JLabel lblIp = new JLabel("IP:");
		lblIp.setBounds(10, 15, 22, 14);
		contentPane.add(lblIp);
		
		textFieldIp = new JTextField();
		textFieldIp.setText("127.0.0.1");
		textFieldIp.setBounds(31, 12, 96, 20);
		contentPane.add(textFieldIp);
		textFieldIp.setColumns(10);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(136, 15, 47, 14);
		contentPane.add(lblPuerto);
		
		textFieldPuerto = new JTextField();
		textFieldPuerto.setText("5000");
		textFieldPuerto.setColumns(10);
		textFieldPuerto.setBounds(183, 12, 47, 20);
		contentPane.add(textFieldPuerto);
				
		JLabel lblUser = new JLabel("User:");
		lblUser.setBounds(238, 15, 33, 14);
		contentPane.add(lblUser);
		
		textFieldUser = new JTextField();
		textFieldUser.setColumns(10);
		textFieldUser.setBounds(275, 12, 71, 20);
		contentPane.add(textFieldUser);

		JButton btnNewButton = new JButton("Conectar");
		btnNewButton.setBounds(372, 11, 102, 23);
		contentPane.add(btnNewButton);		
		
		JSeparator separator = new JSeparator();
		separator.setBounds(7, 41, 470, 2);
		contentPane.add(separator);
		JList list = new JList();
		list.setBounds(10, 54, 463, 170);
		contentPane.add(list);
		
		JLabel lblDestinatario = new JLabel("Para:");
		lblDestinatario.setBounds(10, 238, 33, 14);
		contentPane.add(lblDestinatario);
		
		textFieldDestinatario = new JTextField();
		textFieldDestinatario.setColumns(10);
		textFieldDestinatario.setBounds(42, 235, 71, 20);
		contentPane.add(textFieldDestinatario);
		
		JLabel lblDestinatario_1 = new JLabel("Para:");
		lblDestinatario_1.setBounds(120, 238, 33, 14);
		contentPane.add(lblDestinatario_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(151, 233, 323, 29);
		contentPane.add(textArea);
		
	}
}
