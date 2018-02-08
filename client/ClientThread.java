package client;

import java.net.*;
import java.io.*;

public class ClientThread extends Thread{
	protected Socket socket;
	protected String hostName;
	protected BufferedReader out;

	ClientThread(Socket s, String name, BufferedReader b){
		this.socket = s;
		this.hostName = name;
		this.out = b;
	}

	public void run(){
		try{
			String input;
			while((input = out.readLine()) != null)
				System.out.println(input);
		
		}catch(IOException e){
			System.err.println("Error reading from server");
			return;
		}
	}
}
