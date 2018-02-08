package server;

import java.net.ServerSocket;
import java.util.*;
import java.io.*;
import java.net.Socket;

public class Server {
	public int portNum;
	public boolean threaded;

	public Server(String[] args){
		portNum = Integer.parseInt(args[0]);

		//checkign if we are multithreaded mode or not
		if(args[1].equals("-s"))
			threaded = false;
		else if(args[1].equals("-m"))
			threaded = true;
		else{
			System.err.println("USAGE: java -jar Server.jar [port number] [-s (single threaded) | -m (multi-threded)");
			System.exit(1);
		}
	}

	public void run() throws java.io.IOException{
		ServerSocket socket = null;

		try{
			socket = new ServerSocket(portNum);

			//multithreading the server if specified
			if(threaded){
				while(true){
					ServerThread t = new ServerThread(socket.accept());
					t.start();
				}
			}else{
				Socket s = socket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				String input = null;

				//just waiting for input from one thread
				while(true)
					input = in.readLine();
			}
		}catch(java.io.IOException e){
			System.err.println("Error reading from port " + portNum);
			System.exit(1);
		}
	}

	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("USAGE: java -jar Server.jar [port number] [-s (single threaded) | -m (multi-threded)");
			return;
		}

		Server s = new Server(args);
	}

}
