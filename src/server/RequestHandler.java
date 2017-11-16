package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.NoSuchPaddingException;
import resources.Crypt;
import resources.Utils;

public class RequestHandler implements Runnable {
	private Socket client;
	private Crypt crypt;
	private Server server;
	private byte[] username;
	private OutputStream writer;
	
	public RequestHandler(Socket client, Server server) throws IOException {
		writer = client.getOutputStream();
		this.client = client;
		this.server = server;
	}
	
	private void inizializeClient(byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException {
		int[] delPosition = new int[2];
		int n, i;
		n = i = 0;
		while(n != 2)
			if(message[i++] == 0)
				delPosition[n++] = i - 1;
		
		username = Arrays.copyOfRange(message, 0, delPosition[0]++);
		String alg = new String(Arrays.copyOfRange(message, delPosition[0], delPosition[1]++));
		byte[] key = Arrays.copyOfRange(message, delPosition[1], message.length);
		crypt = new Crypt(alg,
				key);
	}
	
	public void sendMessage(byte[] message){			
		try {
			message = crypt.encrypt(message);
			writer.write(Utils.componeMessage(new byte[] {(byte)message.length}, message));
			writer.flush();
		} catch (IOException e) {
			removeClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeClient() {
		server.removeClient(this);		
	}

	private void sendMessageToAllClients(byte[] message) {
		server.sendMessageToAllClients(message);
	}
	
	@Override
	public void run() {
		try (InputStream in = client.getInputStream()) {
			byte[] message = new byte[256];
			in.read(message);
			inizializeClient(Utils.getContent(message));
			while (in.read(message) != -1)
				sendMessageToAllClients(Utils.componeMessage(username, 
						" : ".getBytes(),
						crypt.decrypt(Utils.getContent(message))));
		} catch (Exception e) {
			System.out.println(e);
			removeClient();
		}
	}
}