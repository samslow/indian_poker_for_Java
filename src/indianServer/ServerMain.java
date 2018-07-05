package indianServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
	final static int PORT = 9977;
	final static int cPort = 9978;
	static ServerSocket chattingSocket;
	
	public static void main(String args[]
			) throws IOException {
		
			ServerSocket socket = new ServerSocket(PORT);
			chattingSocket = new ServerSocket(cPort);
			while(true) {
				Socket csock = socket.accept();
				Accept exe = new Accept(csock);	
			
				new Thread(exe).start();
			} 
	}
}