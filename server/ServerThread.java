package server;

import java.util.*;
import java.net.Socket;
import java.io.*;

public class ServerThread extends Thread{
	protected Socket socket;
	protected PrintWriter out;
	protected BufferedReader in;

	public ServerThread(Socket s){
		socket = s;

		try{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public void run(){
		try{
			String input = null;

			while(true){
				input = in.readLine();
			}
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
