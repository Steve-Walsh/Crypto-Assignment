import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.MessageDigest;

public class Client
{



	public void genSessionKey()
	 {
        try
        {
            // File for writing output
            FileOutputStream keyFOS = new FileOutputStream("SessionKeyClient");
            ObjectOutputStream keyOOS = new ObjectOutputStream(keyFOS);
            
            // Generate random AES key
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecretKey aesKey = keygen.generateKey();
            keyOOS.writeObject(aesKey);
            
            System.out.println("AES key generated and written to file: SessionKeyClient");
             
            keyOOS.close();
            keyFOS.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Encrypts message provided on command line and writes to file
     * @param args
     */
	public void encSessionKey(){
    try
    {


      // File containing RSA public key
      FileInputStream keyFIS = new FileInputStream("RSAPublicKeyFile");
      ObjectInputStream keyOIS = new ObjectInputStream(keyFIS);

      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");;
      // Initialize the cipher for encryption
      rsaCipher.init(Cipher.ENCRYPT_MODE, (PublicKey) keyOIS.readObject());
      keyOIS.close();
      keyFIS.close();

      // File containing secret AES key
      FileInputStream aesFIS = new FileInputStream("SessionKeyClient");
      ObjectInputStream aesOIS = new ObjectInputStream(aesFIS);

      // Read in the AES key
      SecretKey aesKey = (SecretKey) aesOIS.readObject();
      byte[] sessionKey = aesKey.getEncoded();
      aesFIS.close();
      aesOIS.close();

      // Encrypt the key
      byte[] encSessionKey = rsaCipher.doFinal(sessionKey);

      // File for writing output
      FileOutputStream fos = new FileOutputStream("EncryptedSessionKey");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(encSessionKey); // Write encryptedKey to file
      oos.close();
      fos.close();
      System.out.println("Session key encrypted by Client.");

    }
    catch (Exception e)
    {
      System.out.println("Session Key error" + e);
    }
  }

  public void encryptMsg(String message){
    try{
      // File containing secret AES key
      FileInputStream keyFIS = new FileInputStream("SessionKeyClient");
      ObjectInputStream keyOIS = new ObjectInputStream(keyFIS);
     
      // Read in the AES key
      SecretKey aesKey = (SecretKey) keyOIS.readObject();
      keyOIS.close();
      keyFIS.close();

      // set IV (required for CBC)
      byte[] iv ={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
      IvParameterSpec ips = new IvParameterSpec(iv);
     
      // Create AES cipher instance
      Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
     
      // Initialize the cipher for encryption
      aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ips);

      // File for writing output
      FileOutputStream encryptMsgFOS = new FileOutputStream("message.txt");

      byte plaintext[] = message.getBytes();
      byte[] ciphertext = aesCipher.doFinal(plaintext);
      
      encryptMsgFOS.write(ciphertext);
      encryptMsgFOS.close();
      // Display ciphertext in Hex format
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < ciphertext.length; i++)
      {
          hexString.append(Integer.toHexString(0xF & ciphertext[i]>>4));
          hexString.append(Integer.toHexString(0xF & ciphertext[i]));
          hexString.append(" ");
      }
      System.out.println("Ciphertext: " + hexString.toString());

    }
    catch(Exception e){
      System.out.println("Error when encrypting message: " + e);
    }

  }

  public void decryptMsg(){
    try{
      // File containing secret AES key
      FileInputStream keyFIS = new FileInputStream("SessionKeyClient");
      ObjectInputStream keyOIS = new ObjectInputStream(keyFIS);
      // Read in the AES key
      SecretKey aesKey = (SecretKey) keyOIS.readObject();
      keyOIS.close();
      keyFIS.close();

      // set IV (required for CBC)
      byte[] iv = new byte[] {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
      IvParameterSpec ips = new IvParameterSpec(iv);
      
      // Create AES cipher instance
      Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
     
      // Initialize the cipher for decryption
      aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ips);

      // Read ciphertext from file and decrypt it
      FileInputStream fis = new FileInputStream("message.txt");
      BufferedInputStream bis = new BufferedInputStream(fis);
      CipherInputStream cis = new CipherInputStream(bis, aesCipher);
      StringBuffer plaintext = new StringBuffer();
      int c;
      while ((c = cis.read()) != -1){
        plaintext.append((char) c);
      }
      cis.close();
      bis.close();
      fis.close();
      System.out.println("Plaintext: " + plaintext.toString());


    }
    catch (Exception e){
      System.out.println("Error when decrypting message: " + e);
    }

  }

	



}