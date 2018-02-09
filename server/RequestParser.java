//J.T. Liso and Sean Whalen
//COSC 560, Spring 2018
//February 28, 2018

package server;

import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.Headers;


//class for parsing HTTP requests on our server
public class RequestParser{
	public Headers request;
	protected HashMap<String, List<String>> mapped_request;

	public RequestParser(Headers h){
		request = h;
		mapped_request = new HashMap<String, List<String>>();
	}

	//parses the header into a map
	public void parse() throws IOException{
		for(String key : request.keySet()){
			List<String> value = request.get(key);
			mapped_request.put(key, value);

		}
	}

	//creates a response to send to the client based on the request that was parsed into a map
	public String createResponse(){
		String response = "";

		for(Map.Entry<String, List<String>> entry : mapped_request.entrySet()){
			String key = entry.getKey();
			List<String> value = entry.getValue();
			response += key;
			response += " = ";
			for(String val : value)
				response += value + " ";
			response += "\n";
		}

		return response;
	}

}
