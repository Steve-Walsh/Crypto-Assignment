import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Scanner;


public class Communication
{

	Server serverClass;
	Client clientClass;
	Scanner input;
	Scanner input2;

	public Communication(){
		serverClass = new Server();
		clientClass = new Client();
		input = new Scanner(System.in);
		input2 = new Scanner(System.in);
	}

	public static void main(String[] args) {

		Communication instance = new Communication();
		instance.start();
	}

  public void start()
  {
    //menu
  	System.out.println("Going to Generate Keys");
    serverClass.genKey(); // makes RSA Key
    clientClass.genSessionKey(); // makes Session key
    clientClass.encSessionKey(); // Encrypts Session key from Client
    serverClass.decSessionKey(); // Server Decrypts session key
   	menu();
    
  }
	public void menu(){

	int option = mainMenu();
		while (option != 0) {

			switch (option) {
			case 1:
				serverMessage();
				
				break;

			case 2:
				clientMessage();
				break;

			case 3:

				break;

			}

			option = mainMenu();
		}

		System.out.println("Exiting... bye");
	}

	/**
	 * mainMenu() - This method displays the menu for the application, reads the
	 * menu option that the user entered and returns it.
	 * 
	 * @return the users menu choice
	 */
	private int mainMenu() {
		System.out.println("=================");
		System.out.println("   Select Option  ");
		System.out.println("=================");
		System.out.println("1) Server sends Message to Client");
		System.out.println("2) Client sends message to Server");
		System.out.println("0) Exit");
		System.out.print("==>>");
		int option = input.nextInt();
		return option;
	}

	public void serverMessage(){

		System.out.println("Server Message > ");
		System.out.print("> ");
		String message = input2.nextLine();
		System.out.println("Server > ");
		serverClass.encryptMsg(message);
		System.out.println("Client > ");
		clientClass.decryptMsg();
		


	}

	public void clientMessage(){
		System.out.println("Client Message > ");
		System.out.print("> ");
		String message = input2.nextLine();
		System.out.println("Client > ");
		clientClass.encryptMsg(message);
		System.out.println("Server > ");
		serverClass.decryptMsg();

	}

	





}