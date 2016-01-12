import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;


public class Server
{


  public void genKey()  {
    /**
     * Generate RSA key pair and write to separate files. N.B. This is just for
     * illustration. Private keys should not be stored in an unprotected form
     * like this. Better to use Keystore
     */

    try
    {
      // Generate RSA key pair
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

      // File for writing private key
      FileOutputStream privateKeyFOS = new FileOutputStream("RSAPrivateKeyFile");
      ObjectOutputStream privateKeyOOS = new ObjectOutputStream(privateKeyFOS);

      // File for writing publickey
      FileOutputStream publicKeyFOS = new FileOutputStream("RSAPublicKeyFile");
      ObjectOutputStream publicKeyOOS = new ObjectOutputStream(publicKeyFOS);

      // Write the keys to respective files
      privateKeyOOS.writeObject(keyPair.getPrivate());
      publicKeyOOS.writeObject(keyPair.getPublic());

    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }

    /**
     * Decrypts message provided in file and writes to standard output
     */
  public void decSessionKey()
  {

    try
    {
      // File containing RSA private key
      FileInputStream keyFIS = new FileInputStream("RSAPrivateKeyFile");
      ObjectInputStream keyOIS = new ObjectInputStream(keyFIS);

      // Create RSA cipher instance
      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

      // Initialize the cipher for encryption
      rsaCipher.init(Cipher.DECRYPT_MODE, (PrivateKey) keyOIS.readObject());

      keyOIS.close();
      keyFIS.close();

      // Read ciphertext from file and decrypt it
      FileInputStream encSessionKeyFIS = new FileInputStream("EncryptedSessionKey");
      ObjectInputStream encSessionKeyOIS = new ObjectInputStream(encSessionKeyFIS);
      
      byte[] sessionKey = (byte []) encSessionKeyOIS.readObject(); //Casting key to byte array
      byte[] decSessionKey = rsaCipher.doFinal(sessionKey); //Decryption
      
      SecretKey decryptedSessionKey = new SecretKeySpec(decSessionKey, "AES"); //Create Secret key
      encSessionKeyFIS.close();
      encSessionKeyOIS.close();
      
      
      //Specify destination to save
      FileOutputStream sessionKeyFOS = new FileOutputStream("SessionKeyServer");
      ObjectOutputStream sessionKeyOOS = new ObjectOutputStream(sessionKeyFOS);
     
      sessionKeyOOS.writeObject(decryptedSessionKey); //Write key to file
      sessionKeyOOS.close();
      sessionKeyFOS.close();
      System.out.println("Session key decrypted at Server.");
      
      

    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }

   public void encryptMsg(String message){
    try{
      // File containing secret AES key
      FileInputStream keyFIS = new FileInputStream("SessionKeyServer");
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
     
      // Write ciphertext to file
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
      FileInputStream keyFIS = new FileInputStream("SessionKeyServer");
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
      System.out.println("Eror when decrypting message: " + e);
    }
  }

  












}