package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private final int PORT;
	private static final int THREAD_POOL = 4;
	ArrayList<RequestHandler> clients;
	
	public Server(int port){
		PORT = port;
	}

	public void start() throws IOException {
		clients = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL);
		try(ServerSocket serverSocket = new ServerSocket(PORT)){
			while (true) {
				RequestHandler client = new RequestHandler(serverSocket.accept(), this);
				executor.execute(client);
				clients.add(client);
			}
		}
	}
	
	public void removeClient(RequestHandler client) {
		clients.remove(client);
	}
	
	public void sendMessageToAllClients(byte[] message) {
		clients.stream().forEach(client -> client.sendMessage(message));
	}
	
	public static void main(String[] args) throws IOException {
		new Server(7777).start();
	}
}
