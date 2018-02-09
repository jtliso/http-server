//J.T. Liso and Sean Whalen
//COSC 560, Spring 2018
//February 28, 2018

package server;

import java.util.*;
import java.io.*;
import java.net.*;

//class for parsing HTTP requests on our server
public class RequestParser{
	public String request;
	protected HashMap<String, String> mapped_request;

	public RequestParser(String r){
		request = r;
		mapped_request = new HashMap<String, String>();
	}

	public void parse() throws IOException{
		//splitting the request into various lines and reading line by line
		String[] split = request.split(System.getProperty("line.separator"));

		for(String line : split){
			//checking that the line is not empty
			if(line.length() == 0)
				continue;
			
			String[] split_line = line.split("[=]");
			String key = URLDecoder.decode(split_line[0], System.getProperty("file.encoding"));
			String value = URLDecoder.decode(split_line[1], System.getProperty("file.encoding"));
			mapped_request.put(key, value);
		}
	}

	//creates a response to send to the client based on the request
	public String createResponse(){
		String response = "";

		for(Map.Entry<String, String> entry : mapped_request.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			response += key;
			response += " = ";
			response += value;
			response += "\n";
		}

		return response;
	}

}
