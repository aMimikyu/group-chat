package better_chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class VerySimpleChatServer {
	ArrayList<PrintWriter> clientOutputStreams;
	int i;			
	
	
	/* inner class that represents the job for a thread that handles a single client */
	
	public class ClientHandler implements Runnable {			
		BufferedReader reader;									
		Socket sock;
		
		/* Constructor that gets the reserved socket for the new client and initializes a bufferedReader that reads from that socket 
		 * @param Socket clientSocket 
		 * */
		public ClientHandler(Socket clientSocket) {				
			try {
				sock = clientSocket;
				InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);										
			} catch (Exception ex) { ex.printStackTrace();}
		}
		
		/*	method that checks for new input from a single client 
		 *  @inheritDoc */
		
		public void run() {
			String message;
			try { 
				while ((message = reader.readLine())!= null) {		
					//message = reader.readLine();
					System.out.println("read " + message);
					tellEveryone("user" + Thread.currentThread().getName() + ": " + message);			
				}
			} catch (Exception ex ) { ex.printStackTrace();}
		}
	}
	
	
	
	
	/* main */
	public static void main (String[] args) {
		new VerySimpleChatServer().go();
	}
	
	/* method that waits for a client to connect and handles that connection */
	
	public void go() {
		clientOutputStreams = new ArrayList();
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			
			while (true) {
				Socket clientSocket = serverSock.accept(); 									
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());	   
				clientOutputStreams.add(writer);											
				
				Thread t = new Thread(new ClientHandler(clientSocket)); 
				t.setName("" + ++i);
				t.start();
				System.out.println("got a connection");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/* method that sends a message to all clients that have connected to the server 
	   @param String message
	   */
	public void tellEveryone (String message) {
		Iterator it = clientOutputStreams.iterator();
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
