package client;

// provides helper methods to print byte[]
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.security.MessageDigest;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.io.FileNotFoundException;

import javax.crypto.KeyGenerator;

/** 
 * 	Generate a digest using the MD5 algorithm.
 */
public class Encryption{

    public byte[] hash(byte[] plainBytes) throws Exception{

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.update(plainBytes);
        byte[] digest = messageDigest.digest();

        return digest;
    }

    public byte[] cipher(byte[] plain, byte[] key) throws Exception{ 
        
        SecretKey decipher_key = new SecretKeySpec(key, 0, key.length, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decipher_key.getEncoded());
        cipher.init(Cipher.ENCRYPT_MODE, decipher_key, ivParameterSpec);
        return cipher.doFinal(plain);
    }

    public byte[] decipher(byte[] cipherBytes, byte[] key) throws Exception{

        SecretKey decipher_key = new SecretKeySpec(key, 0, key.length, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decipher_key.getEncoded());
        cipher.init(Cipher.DECRYPT_MODE, decipher_key, ivParameterSpec);
        return cipher.doFinal(cipherBytes);
    }

    public byte[] generateKey() throws Exception{

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.init(128, random);
        SecretKey key = keyGen.generateKey();
        byte[] encoded = key.getEncoded();

        return encoded;
    }

    public static void write(byte[] key, String keyPath) throws Exception {
        
        //System.out.println("Writing key to '" + keyPath + "' ..." );

        FileOutputStream fos = new FileOutputStream(keyPath);
        fos.write(key);
        fos.close();
    }

    public static byte[] read(String keyPath) throws FileNotFoundException, Exception {
        //System.out.println("Reading key from file " + keyPath + " ...");
        FileInputStream fis = new FileInputStream(keyPath);
        byte[] encoded = new byte[fis.available()];
        fis.read(encoded);
        fis.close();
        return encoded;
    }

    public byte[] makeMac(byte[] bytes, byte[] key) throws Exception{

        Mac cipher = Mac.getInstance("HmacMD5");
        SecretKey hash_key = new SecretKeySpec(key, 0, key.length, "AES");
        cipher.init(hash_key);
        byte[] mac = cipher.doFinal(bytes);

        return mac;
    }

    public boolean verifyMac(byte[] ciphered, byte[] bytes, byte[] key) throws Exception {

        Mac cipher = Mac.getInstance("HmacMD5");
        SecretKey hash_key = new SecretKeySpec(key, 0, key.length, "AES");
        cipher.init(hash_key);
        byte[] new_cipheredBytes = cipher.doFinal(bytes);
        return Arrays.equals(ciphered, new_cipheredBytes);
    }
}
