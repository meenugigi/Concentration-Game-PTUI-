/*
Homework 10 : Concentration
File Name : ConcentrationClientServerThread.java
 */
package server;

import common.ConcentrationException;
import game.ConcentrationBoard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class contains the run method.
 * Call functions to process the inputs received.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationClientServerThread extends Thread {

    /** socket */
    private Socket socket = null;
    /** the board dimension */
    private int boardDimension = 0;
    /** flag to control the invoking of methods to check for card match. */
    private int flag = 0;
    /** for threading purpose */
    private int clientNum = 0;


    /**
     * Constructor.
     *
     * @param socket         socket
     * @param boardDim       board dimensions
     * @param clientNum      the clinet number
     *
     */
    public ConcentrationClientServerThread(Socket socket, int boardDim,
                                           int clientNum) {
        super("ConcentrationClientServerThread");
        this.socket = socket;
        this.boardDimension = boardDim;
        this.clientNum = clientNum;
    }


    /**
     * The method that runs with the thread is started.
     *
     */
    public void run() {
        try (
//                Connects PrintWriter stream to the output stream.
                PrintWriter out = new PrintWriter(socket.getOutputStream(),
                        true);
//                reads from BufferedReader created from a socket.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ConcentrationBoard board = new ConcentrationBoard(boardDimension);
            board.setClientNum(clientNum);
//            calls method to process board dimension.
            outputLine = board.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = board.processInput(inputLine);
                String[] outputLineArray = outputLine.split(" ");
                if (processData(out, outputLine, board, outputLineArray)) break;
            }
            socket.close();
        } catch (IOException | ConcentrationException e) {
            e.printStackTrace();
        }
    }

    private boolean processData(PrintWriter out, String outputLine,
                        ConcentrationBoard board, String[] outputLineArray)
                        throws ConcentrationException {
        if(outputLineArray[0].equals("CARD")) {
            outputLine = processCard(out, outputLine, board);
        }
        if(outputLineArray[0].equals("ERROR")){
            out.println(outputLine);
        }
//                if game is over.
        if(board.gameOver()){
            outputLine = board.processInput("GAME_OVER");
            out.println(outputLine);
            return true;
        }
        return false;
    }



    private String processCard(PrintWriter out, String outputLine,
                               ConcentrationBoard board) throws ConcentrationException {
        flag++;
        out.println(outputLine);
        if(flag == 2){
            outputLine = board.processInput("DECISION");
            out.println(outputLine);
            flag = 0;
        }
        return outputLine;
    }


}
