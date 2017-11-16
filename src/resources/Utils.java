package resources;

import java.util.Arrays;
import java.util.stream.Stream;

public class Utils {
	public static byte[] getContent(byte[] message) {
		return Arrays.copyOfRange(message, 1, (message[0] & 0xFF) + 1);
	}
	
	public static byte[] componeMessage(byte[]... pieces) {
		byte[] message = new byte[Stream.of(pieces).mapToInt(e -> e.length).sum()];
		int index = 0;
		for (byte[] piece : pieces) {
			System.arraycopy(piece, 0, message, index, piece.length);
			index += piece.length;
		}
		return message;
	}
}
