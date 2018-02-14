//J.T. Liso and Sean Whalen
//COSC 560, Spring 2018
//February 28, 2018

package server;

import java.net.InetSocketAddress;
import java.util.*;
import java.io.*;
import com.sun.net.httpserver.*;
import java.net.*;
import java.nio.file.Files;

public class WebServer {
	public int portNum;
	public boolean threaded;
	protected HttpServer server;
	protected InetSocketAddress socket;

	public WebServer(String[] args){
		portNum = Integer.parseInt(args[0]);

		//checking if we are multithreaded mode or not
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
				String msg = "<h1>Go Vols!</h1>";
				msg += "<img src=\"http://i0.kym-cdn.com/photos/images/newsfeed/001/207/210/b22.jpg\">";
				ex.sendResponseHeaders(200, msg.length());
				OutputStream out = ex.getResponseBody();
				out.write(msg.getBytes());
				out.close();
			}

	}

	//handler to handle GET requests
	protected static class GetHandler implements HttpHandler{
		@Override
			public void handle(HttpExchange ex) throws IOException{
				//parse GET request
				RequestParser r = new RequestParser(ex.getRequestHeaders());
				r.parse();

				//send GET response
				String response = r.createResponse();
				ex.getResponseHeaders().add("Content-type", "text/html");
				ex.sendResponseHeaders(200, 0);
				OutputStream out = ex.getResponseBody();
				response = "<html><h1>You did a GET Request! Good Job!</h1></html>";
				out.write(response.getBytes());
				out.close();
			}
	}

	//handler to handle File transfer
	protected static class FileTransfer implements HttpHandler{
		@Override
			public void handle(HttpExchange ex) throws IOException{
				//parse GET request
				RequestParser r = new RequestParser(ex.getRequestHeaders());
				r.parse();

				//send GET response
				String response = r.createResponse();
				ex.getResponseHeaders().add("Content-type", "text/plain");

				//read some file and print it
				OutputStream out = ex.getResponseBody();

				File file = new File("got.txt");
				ex.sendResponseHeaders(200, file.length());
				Files.copy(file.toPath(), out);
				out.close();
			}
	}

	//recursive call to list all files and subfiles in directory
	//printed as unordered list in HTML
	protected static void listFiles(File dir, OutputStream out) throws IOException{
		String fileList;

		for(File f : dir.listFiles()){
			if(f.isHidden()) //skipping hidden files
				continue;
			if(f.isDirectory()) //recursively exploring directory if needed
				listFiles(f, out);
			else if(f.isFile())
				out.write(("<li>"+f.getAbsolutePath()+"</li>").getBytes());
		}
	}

	//handler for directory listing
	//lists files in current directory
	protected static class DirectoryHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange ex) throws IOException{
				//parse GET request
				RequestParser r = new RequestParser(ex.getRequestHeaders());
				r.parse();
			
				//send GET response
				String response = r.createResponse();
				ex.getResponseHeaders().add("Content-type", "text/html");
				ex.sendResponseHeaders(200, 0);
				
				//sending header
				OutputStream out = ex.getResponseBody();
				response = "<html><h1>Directory listing!</h1><ul>";
				out.write(response.getBytes());

				File curDir = new File(".");
				listFiles(curDir, out);
				out.write("</ul></html>".getBytes());
				out.close();
		}
	}


	public void run() throws IOException{
		try{
			socket = new InetSocketAddress(portNum);
			server = HttpServer.create(socket, 0);

			//adding handlers to handle various aspects of the server
			server.createContext("/", new BasicHandler());
			server.createContext("/get", new GetHandler());
			server.createContext("/file", new FileTransfer());
			server.createContext("/dir", new DirectoryHandler());
			
			//threading the server if specified
			if(!threaded)
				server.setExecutor(null);
			else
				server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

			server.start();

		}catch(IOException e){
			System.err.println("Error reading from port " + portNum);
			System.exit(1);
		}
	}

	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("USAGE: java -jar Server.jar [port number] [-s (single threaded) | -m (multi-threaded)]");
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
