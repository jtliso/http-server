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
import java.nio.file.Paths;

public class WebServer {
	public int portNum;
	public boolean threaded;
	protected HttpServer server;
	protected InetSocketAddress socket;
	
	public Map<String, String> commands;
	

	public WebServer(String[] args){
		portNum = Integer.parseInt(args[0]);

		//checking if we are multithreaded mode or not
		if(args[1].equals("-s"))
			threaded = false;
		else if(args[1].equals("-m"))
			threaded = true;
		else{
			System.err.println("USAGE: java -jar Server.jar [port number] [-s (single threaded) | -m (multi-threaded)");
			System.exit(1);
		}
		
		
	}

	//basic handler for root level, prints hello world
	protected static class BasicHandler implements HttpHandler{
		@Override
			public void handle(HttpExchange ex) throws IOException{
				//String msg = "<h1>Go Vols!</h1>";
				//msg += "<img src=\"http://i0.kym-cdn.com/photos/images/newsfeed/001/207/210/b22.jpg\">";
				
				byte[] encoded = Files.readAllBytes(Paths.get("index.html"));
				String msg = new String(encoded);
				
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
				String query = ex.getRequestURI().getQuery();

				//checking that query was provided
				if(query == null){
					ex.sendResponseHeaders(200, 0);
					out.write("Error, you need to provide a file name in the url\nUSAGE host/file?file=...".getBytes());
					out.close();
				}else{

					String[] split = ex.getRequestURI().getQuery().split("=");
					
					//checking that the correct parameter was passed
					if(!split[0].equals("file")){
						ex.sendResponseHeaders(200, 0);
						out.write("Error, file parameter must be called file\nUSAGE host/file?file=...".getBytes());
						out.close();
					}else{
						String param = split[1];

						//check if file can be opened and read
						File file = new File(param);
						if(file.isFile() && file.canRead()){
							ex.sendResponseHeaders(200, file.length());
							Files.copy(file.toPath(), out);
							out.close();
						}else{
							ex.sendResponseHeaders(200, 0);
							out.write("Error reading file. Please try again and make sure path is correct".getBytes());
							out.close();
						}
					}
				}
			}
		}

	//recursive call to list all files and subfiles in directory
	//printed as unordered list in HTML
	protected static void listFiles(File dir, OutputStream out, String currDir) throws IOException{
		String fileList;

		for(File f : dir.listFiles()){
			if(f.isHidden()) //skipping hidden files
				continue;
			if(f.isDirectory()) //recursively exploring directory if needed
				listFiles(f, out, currDir);
			else if(f.isFile()){
				String fname = f.getAbsolutePath().replace(currDir+"/", "");
				out.write(("<li><a href=\"file?file="
					+fname+"\">"+fname+"</a></li>").getBytes());
			}
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
				listFiles(curDir, out, curDir.getAbsolutePath());
				out.write("</ul></html>".getBytes());
				out.close();
		}
	}

	//handler to run CGI script
	//executed whenever user navigates to /cgi
	protected static class CGIHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange ex) throws IOException{
			String param = "";
			String cgiResp = ""; 			//holds cgi resp. or error msg. 
			String scriptName = "";
			String command = "";
			
			Map<String, String> commands = new HashMap<String, String>();	//support file types
			commands.put("php", "php");
			commands.put("py", "python");
			commands.put("pl", "perl");
			
			//parse GET request
			RequestParser r = new RequestParser(ex.getRequestHeaders());
			r.parse();
			
			//TO-DO HERE: 
			// 1) parse arguments and pass to cgi script in param var.
			String path = ex.getRequestURI().getPath();
			cgiResp = "<p>" + path + "</p>";

			//checking that path was provided
			if (path != null) {
				String[] slash = path.split("/");
				//checking that /script was passed
				if (slash.length == 3) {
					scriptName = slash[2];
					//find extension
					String[] period = scriptName.split("\\.");
					if (period.length > 1) {
						command = commands.get(period[period.length-1]);
					}
				}
			}
				
			//run cgi script, put resp. in cgiResp			
			if (!scriptName.isEmpty()) {
				try {
					String line; 
					StringBuilder output = new StringBuilder();
					Process p = Runtime.getRuntime().exec(command + " cgi-bin/" + scriptName + " " + param); 
					BufferedReader input = 
						new BufferedReader (
							new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						output.append(line);
					}
					input.close();
					cgiResp = output.toString();
					
				}
				catch (Exception err) {
					cgiResp = "<p>" + err.toString() + "</p>";
				}
			}
			else {
				cgiResp = "<p>usage: hostname/cgi-bin/script</p>";
			}

			//send GET response
			String response = r.createResponse();
			OutputStream out = ex.getResponseBody();
			ex.getResponseHeaders().add("Content-type", "text/html");
			ex.sendResponseHeaders(200, 0);
			
			//output either response from script or error message
			out.write(cgiResp.getBytes());
			out.close();
		}
	}

	//handler to handle POST requets
	//asks for favorite band
	protected static class PostHandler implements HttpHandler{
		@Override
		public void handle(HttpExchange ex) throws IOException{
				//parse POST request
				RequestParser r = new RequestParser(ex.getRequestHeaders());
				r.parse();
				String form = "<html><head><title>CS560 Programming assigment 1</title><style>html {margin: 0;padding: 0;height: 100%;width: 100%;}body {width: 900px;margin: auto;	background-image: url(\"http://www.misucell.com/data/out/9/IMG_315890.png\");}.container {max-width: 80%;text-align: right;color: rgb(77,77,77);}</style></head><body><div class=\"container\">";

				InputStreamReader in = new InputStreamReader(ex.getRequestBody(), "utf-8");
				BufferedReader buf = new BufferedReader(in);
				String query = buf.readLine();

				//checking that the query is not null
				if(query != null){
					String[] params = query.split("&");

					String name = params[0].split("=")[1].replace("+", " ");
					String comment = params[1].split("=")[1].replace("+", " ");

					BufferedWriter writer = new BufferedWriter(new FileWriter("post_output.txt", true)); //append to file if already there
					String msg = name + " | " + comment + "\n";
					form += msg;
					writer.write(msg);
					writer.close();
				}

				form += "<form method=\"post\">Name:<br><input type=\"text\" name=\"firstname\"><br> Comment:<br><input type=\"text\" name=\"bandname\"><br><br><input type=\"submit\" value=\"Submit\"></form>";
				form += "</div></body></html>";
				
				ex.getResponseHeaders().add("Content-type", "text/html");
				ex.sendResponseHeaders(200, 0);

				
				//sending header
				OutputStream out = ex.getResponseBody();
				out.write(form.getBytes());
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
			server.createContext("/cgi-bin", new CGIHandler());
			server.createContext("/post", new PostHandler());
			
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
