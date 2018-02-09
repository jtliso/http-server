package server;

import java.net.InetSocketAddress;
import java.util.*;
import java.io.*;
import com.sun.net.httpserver.*;

public class WebServer {
	public int portNum;
	public boolean threaded;
	protected HttpServer server;
	protected InetSocketAddress socket;

	public WebServer(String[] args){
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

	//basic handler for root level, prints hello world
	protected static class BasicHandler implements HttpHandler{
		@Override
			public void handle(HttpExchange ex) throws IOException{
				String msg = "<h1>Hello world!</h1>";
				ex.sendResponseHeaders(200, msg.length());
				OutputStream out = ex.getResponseBody();
				out.write(msg.getBytes());
				out.close();
			}

	}


	public void run() throws IOException{
		try{
			socket = new InetSocketAddress(portNum);
			server = HttpServer.create(socket, 0);

			//adding handlers to handle various aspects of the server
			server.createContext("/", new BasicHandler());

			server.start();

		}catch(IOException e){
			System.err.println("Error reading from port " + portNum);
			System.exit(1);
		}
	}

	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("USAGE: java -jar Server.jar [port number] [-s (single threaded) | -m (multi-threded)");
			return;
		}

		
		try{
			WebServer s = new WebServer(args);
			s.run();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
	}

}
