# http-server
UTK CS560 Programming Assignment 1


## Overview
The goal is to implement a basic HTTP  server that supports directory listing, staticfiles, and CGI scripts.The server needs to run on a Linux server environment, such as those supported in our lab machines. Additionally, you will also have to write a sample CGI shell script that can be executed by your web server. Specifically, your web server needs to support the following features:

1. Single  connection mode (serial) and multiple connectionmode (parallel with multiple threads)

2. HTTP GET requests with query and header parsing

3.Automatic directory listing

4.Static file transport

5.Basic CGI support by running a sample CGI script on the server side 

## Design
Generally, the implementation of a HTTP server is straightforward. First, the code needs to allocate a serversocket, bind it to a port, and then listen for incoming connections. Next, the server accepts an incoming client connection and parse the input datastream into a HTTP request. Based on the request's parameters, it then forms a response and sends it back to the client. In a loop, the server continues to perform steps 2 and 3 for as long as the server is running. If we are in multi-thread mode, then we simply fork a thread after we accept a connection and let the child thread handle parsing and responding to the request. In practice, you may choose to use either a process (as in Linux processes) or a thread to handle incoming calls.
