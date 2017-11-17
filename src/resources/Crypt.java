package resources;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
	
    private SecretKey key;
    private Cipher cipher;    
    
    public Crypt(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
    	key = KeyGenerator.getInstance(algorithm).generateKey();
    	cipher = Cipher.getInstance(algorithm);
    }
    
    public Crypt(String algorithm, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException  {
    	key =  new SecretKeySpec(keyBytes, algorithm);
    	cipher = Cipher.getInstance(algorithm);
    }

    public byte[] encrypt(byte[] inputBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(inputBytes);
    }

    public byte[] decrypt(byte[] cryptedBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {   	
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cryptedBytes);
    }
    
    public byte[] getKeyBytes(){
    	return key.getEncoded();
    }
    
    public int getBlockSize(){
    	return cipher.getBlockSize();
    }
}
