import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Lovi on 2017. 03. 09. @ 0:22.
 */
public class Client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP = "127.0.0.1";
	private Socket connection;
	private JTextField userNameField;
	private String actualUserName = "Client";
	private JLabel userNameLabel;
	private JButton userNameSetter;
	private JTextField ipTextField;
	private JButton ipOkButton;
	private JLabel ipLabel;
	private JPanel panel1;
	private JPanel panel2;
	private Container contentPane;
	private JScrollPane scrollPane;

	public Client() {
		super("Lovi's instant messenger client");
		ipTextField = new JTextField("127.0.0.1");
		userText = new JTextField();
		userNameField = new JTextField("Enter username");
		userNameLabel = new JLabel("Current username: Client");
		userNameField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserName(e.getActionCommand());
				userNameField.setText("Username set!");
			}
		});
		userNameSetter = new JButton("Set");
		userNameSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUserName(userNameField.getText());
			}
		});
		chatWindow = new JTextArea();
		ipLabel = new JLabel("IP to connect to: ");
		ipOkButton = new JButton("OK");
		ipOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setServerIpByField(ipTextField.getText());
				startRunning();
				ipOkButton.setVisible(false);
			}
		});
		scrollPane = new JScrollPane(chatWindow);
		scrollPane.setPreferredSize(new Dimension(250,160));
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1,BoxLayout.PAGE_AXIS));
		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.LINE_AXIS));
		userNameField.setFocusable(true);
		userNameField.setRequestFocusEnabled(true);
		userNameField.requestFocus();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		panel1.add(Box.createRigidArea(new Dimension(0,5)));
		panel1.add(userNameLabel);
		panel1.add(Box.createRigidArea(new Dimension(0,5)));
		panel1.add(userText);
		panel1.add(scrollPane);
		panel2.add(userNameField);
		panel2.add(userNameSetter);
		panel2.add(ipLabel);
		panel2.add(ipTextField);
		panel2.add(ipOkButton);
		panel1.add(userText);
		contentPane = getContentPane();
		contentPane.add(panel2,BorderLayout.NORTH);
		contentPane.add(panel1,BorderLayout.CENTER);
		setSize(600, 300);
		setVisible(true);
	}


	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch(EOFException eofe) {
			showMessage("\n Client terminated the connection!");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			closeCrap();
		}
	}

	private void whileChatting() throws IOException {
		ableToType(true);
		userNameField.setEditable(false);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch(ClassNotFoundException clsnfe) {
				showMessage("\n wut is object tipe");
			}
		} while(!message.equals("SERVER - END"));
	}

	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				userText.setEditable(b);
			}
		});
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n You are now connected! \n");
		setButtonVisible(false);
	}

	private void showMessage(final String s) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chatWindow.append(s);
			}
		});
	}

	private void closeCrap() {
		try {
			showMessage("\n closing connection...");
			ableToType(false);
			output.close();
			input.close();
			connection.close();
			userNameField.setEditable(true);
			setButtonVisible(true);
			ipOkButton.setVisible(true);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void connectToServer() throws IOException {
		showMessage("\nAttempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}

	private void sendMessage(String message) {
		try {
			output.writeObject(actualUserName+" - " + message);
			output.flush();
			showMessage("\n"+actualUserName+" - " + message);
		} catch(IOException ioe) {
			chatWindow.append("\n Couldnt send message!!");
		}
	}
	private void setButtonVisible(boolean visible) {
		userNameSetter.setVisible(visible);
	}
	private void setServerIpByField(final String ipAdress) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				serverIP = ipAdress;
			}
		});
	}
	private void setUserName(final String actionCommand) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				actualUserName = actionCommand;
				userNameLabel.setText("Current username: "+actualUserName);

			}
		});
	}

}
