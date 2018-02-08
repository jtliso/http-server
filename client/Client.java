package client;

import java.net.*;
import java.io.*;

public class Client{
	public String hostname;
	public int portNum;
	protected Socket socket;
    protected PrintWriter out;
	protected BufferedReader serverin;
	protected BufferedReader stdin;


	public Client(String[] args) throws IOException{
		hostname = args[0];
		portNum = Integer.parseInt(args[1]);

		try{
			socket = new Socket(hostname, portNum);
			out = new PrintWriter(socket.getOutputStream(), true);
			serverin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stdin = new BufferedReader(new InputStreamReader(System.in));
		}catch(UnknownHostException e){
			System.err.println("Error connecting to host");
			System.exit(1);
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}finally{
			socket.close();
			out.close();
			stdin.close();
			serverin.close();
			System.exit(1);
		}
	}

	public void connect() throws IOException{
		String input = null;

		try{
			//threading a listener to listen for server responses
			ClientThread c = new ClientThread(socket, hostname, serverin);
			c.start();

			//waiting for client input and sending it to server
			while((input = stdin.readLine()) != null)
				out.println(input);
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}finally{
			socket.close();
			out.close();
			stdin.close();
			serverin.close();
			return;
		}
	}

	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("USAGE: java -jar Client.jar [hostname] [port number]");
			return;
		}

		try{
			Client client = new Client(args);
			client.connect();
		}catch(IOException e){
			System.err.println("Error connecting. Disconnecting...");
			return;
		}
	}
}
