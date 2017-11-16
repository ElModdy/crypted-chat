package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.NoSuchPaddingException;
import resources.Crypt;
import resources.Utils;

public class Client {
	private final Crypt crypt;
	private final String algorithm;
	private final String username;
	Socket server;
	
	public Client(String address, int port, String algorithm, String username)
			throws NoSuchAlgorithmException, NoSuchPaddingException, UnknownHostException, IOException {
		this.username = username;
		this.algorithm = algorithm;
		crypt = new Crypt(algorithm);
		server = new Socket(address, port);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try(InputStream in = server.getInputStream()){
					byte[] message = new byte[255];
					while (in.read(message) != -1) {
						System.out.println(new String(crypt.decrypt(Utils.getContent(message))).trim());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private byte[] encrypt(String message) {
		byte[] messageBytes = message.getBytes();
		int len = messageBytes.length;
		int block = crypt.getBlockSize();
		byte[] wrappedMessage = new byte[(int)(Math.ceil((float)len/block) * block)];
		System.arraycopy(messageBytes, 0, wrappedMessage, 0, len);
		return wrappedMessage;
	}
	
	public void connect() {
		try (OutputStream out = server.getOutputStream();
				Scanner SysIn = new Scanner(System.in);) {
			byte[] message = Utils.componeMessage(username.getBytes(),
					new byte[] {0},
					algorithm.getBytes(),
					new byte[] {0},
					crypt.getKeyBytes());
			message = Utils.componeMessage(new byte[] {(byte)message.length}, message); 
			out.write(message);
			out.flush();
			while ((message = encrypt(SysIn.nextLine())) != null) {
				message = crypt.encrypt(message);
				message = Utils.componeMessage(new byte[] {(byte)message.length}, message);
				out.write(message);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		new Client("127.0.0.1", 7777, "DESede", "ElModdy").connect();
	}
}
