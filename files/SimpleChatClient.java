package better_chat;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class SimpleChatClient {
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	
	/* main */
	public static void main(String[] args) {
		SimpleChatClient client = new SimpleChatClient();
		client.go();
	}
	
	/* method that sets up all the gui */
	public void go() {
		JFrame frame = new JFrame("Chat Client");
		JPanel mainPanel = new JPanel();
		Font textFont = new Font("sanserif", Font.BOLD,12);
		incoming = new JTextArea(15,30);
		incoming.setFont(textFont);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		this.setUpNetworking();
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 350);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	/* method that establishes a connection between the client and the server */
	private void setUpNetworking() {
		try {
			sock = new Socket("127.0.0.1",5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			incoming.append("Logged in successfully \n");
			System.out.println("networking established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/* inner class that sends the message to the server when the user clicks the send button*/
	public class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				writer.println(outgoing.getText());
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}
	/* job for a thread that constantly checks for new messages from the server */
	public class IncomingReader implements Runnable{
		public void run() {
			String message;
			try {
				while (true) {
					message = reader.readLine();
					System.out.println("read " + message);
					incoming.append(message +"\n");
					incoming.setCaretPosition(incoming.getDocument().getLength());
				}
			} catch (Exception ex) { ex.printStackTrace();}
		}
	}
}
