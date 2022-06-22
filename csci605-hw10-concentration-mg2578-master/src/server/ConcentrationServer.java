/*
Homework 10 : Concentration
File Name : ConcentrationServer.java
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Creates a socket connection.
 * Can handle multiple clients.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationServer {

    /** client number */
    private static int clientNum = 0;


    /**
     * The main method.
     * Stores hostname and board dimension.
     * Verifies command line arguments.
     *
     * @param args      command line arguments
     *
     */
    public static void main(String[] args){
        if (args.length != 2) {
            System.err.println("Usage: java ConcentrationServer <port " +
                    "number> <board dimension>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        int boardDimension = Integer.parseInt(args[1]);
        boolean listening = true;
//       listens to incoming connections and accepts them.
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new ConcentrationClientServerThread(serverSocket.accept(),
                 boardDimension, ++clientNum).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
