/*
Homework 10 : Concentration
File Name : ConcentrationClient.java
 */
package client;

import common.ConcentrationException;
import java.io.*;
import java.net.*;
import java.util.regex.Pattern;
import static common.ConcentrationProtocol.*;

/**
 * This class connects with the server.
 * Handles errors on the client side.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationClient {

    /** instance of ConcentrationClientBoard */
    private ConcentrationClientBoard board = new ConcentrationClientBoard();
    /** to prompt user for input */
    private static final String PROMPT = "> ";


    /**
     * An empty constructor.
     *
     */
    public ConcentrationClient() {
    }


    /**
     * The main method.
     * Stores hostname and portnumber.
     * Verifies command line arguments.
     *
     * @param args      command line arguments
     *
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        new ConcentrationClient().begin(hostName, portNumber);
    }


    /**
     * Creates socket object.
     * Creates PrintWriter stream and BufferedReader to read from socket and
     * user input.
     *
     * @param hostName      the hostname
     * @param portNumber    the portnumber
     *
     */
    private void begin(String hostName, int portNumber) {
        try (
//                creates a new Socket object.
                Socket kkSocket = new Socket(hostName, portNumber);
//                Connects PrintWriter stream to the output stream.
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
//                reads from BufferedReader created from a socket.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()))
        ) {
//            reads from BufferedReader from user input.
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
//            calls method to run simulation.
            simulation(stdIn, out, in);
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ConcentrationException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Reads data sent from server.
     * Depending upon message sent from server, a call is made to the
     * respective methods to perform their respective tasks.
     * Ends game if message received from server indicates to end game.
     *
     * @param stdIn      BufferedReader to read from user input.
     * @param out        PrintWriter stream.
     * @param in         reads from BufferedReader created from a socket.
     * @throws  IOException to handle possible exceptions when reading data.
     * @throws ConcentrationException to handle invalid coordinates or
     * attempting to reveal an already revealed card.
     */
    private void simulation(BufferedReader stdIn, PrintWriter out,
                            BufferedReader in) throws IOException, ConcentrationException {
        int matchFlag = 0;
        int flag = 0;
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
//            end game if 'GAME_OVER' message is received from server.
            if (fromServer.equals(GAME_OVER)){
                System.out.println("You Won!!");
                break;
            } else{
//                split message received from server.
                String[] messageArray = fromServer.split(" ");
                String message = messageArray[0];
                switch (message) {
//                    creates initial board on receiving board dimensions
//                    from server.
//                    display initial board.
                    case BOARD_DIM -> {
                        board.getCreatedGrid(fromServer);
                        displayBoard(stdIn, out);
                        break;
                    }
//                    displays board with revealed card value.
                    case CARD -> {
                        flag++;
                        board.getRevealedCard(fromServer);
                        System.out.println(board.toString());
                        if (flag < 2) {
                            messageFromClient(stdIn, out);
                        } else {
                            flag = 0;
                        }
                        break;
                    }
//                    to handle errors.
                    case ERROR -> {
                        System.out.println(fromServer);
                        displayBoard(stdIn, out);
                        break;
                    }
//                    displays board with face-up value of cards
//                    when a match is found.
                    case MATCH -> {
                        matchFlag++;
                        board.getCardsMatched(fromServer);
                        System.out.println(board.toString());
                        if (matchFlag != board.getDIM()) {
                            messageFromClient(stdIn, out);
                        }
                        break;
                    }
//                    displays board with hidden value of cards
//                    when a match is not found.
                    case MISMATCH -> {
                        board.getCardsNotMatched(fromServer);
                        displayBoard(stdIn, out);
                        break;
                    }
                    default -> {
                        break;
                    }
                }
            }
        }
    }


    /**
     * Prints the board.
     * Calls method to process next input fom client.
     *
     * @param stdIn      BufferedReader to read from user input.
     * @param out        PrintWriter stream.
     * @throws IOException to handle possible exceptions when reading data.
     * @throws ConcentrationException to handle invalid coordinates or
     * attempting to reveal an already revealed card.
     *
     */
    private void displayBoard(BufferedReader stdIn, PrintWriter out)
            throws IOException, ConcentrationException {
        System.out.println(board.toString());
        messageFromClient(stdIn, out);
    }


    /**
     * Matches user input against provided pattern.
     * Checks if user input is in valid format.
     * if yes, sends user input to server.
     * else, throw error.
     * check if card attempted to reveal is an already revealed card.
     *
     * @param stdIn      BufferedReader to read from user input.
     * @param out        PrintWriter stream.
     * @throws IOException to handle possible exceptions when reading data.
     * @throws ConcentrationException to handle invalid coordinates or
     * attempting to reveal an already revealed card.
     *
     */
    private void messageFromClient(BufferedReader stdIn, PrintWriter out)
            throws IOException, ConcentrationException {
//        pattern in required format of user input.
        Pattern inputPatter = Pattern.compile("\\d\\s\\d$");
        System.out.println();
        System.out.print(PROMPT);
        String fromUser = stdIn.readLine();
//        if user input is in required format.
        if (fromUser != null && inputPatter.matcher(fromUser).matches()) {
            String[] userArray = fromUser.split(" ");
            int row = Integer.parseInt(userArray[0]);
            int col = Integer.parseInt(userArray[1]);
//            checks if row and col coordinates are within bounds.
            checkBounds(row, col, out, stdIn, fromUser);
        }
//        throw error if user input is in incorrect format
        else{
            try{
                throw new ConcentrationException("Invalid coordinates.");
            } catch (ConcentrationException e) {
                System.err.println(e.getMessage());
            }
            messageFromClient(stdIn, out);
        }
    }


    /**
     * Check if user entered coordinates are within board dimensions.
     * @param row       Row coordinate.
     * @param col       Column coordinate.
     * @param out        PrintWriter stream.
     * @param stdIn      BufferedReader to read from user input.
     * @param fromUser   user input
     * @throws ConcentrationException to handle invalid coordinates or
     * attempting to reveal an already revealed card.
     * @throws IOException to handle possible exceptions when reading data.
     *
     */
    private void checkBounds(int row, int col, PrintWriter out,
                             BufferedReader stdIn, String fromUser)
            throws ConcentrationException, IOException {

        if(row< board.getDIM() && col < board.getDIM()){
            String sendToServer = board.getMessageToServer(fromUser);
//                check if card is already revealed.
            checkForRevealedCard(row, col, sendToServer, out, stdIn);
        }
//            throw exception if row or col values are outside desired bounds.
        else{
            try{
                throw new ConcentrationException("Coordinate out of range.");
            } catch (ConcentrationException e) {
                System.err.println(e.getMessage());
            }
            messageFromClient(stdIn, out);
        }
    }


    /**
     * Check if card attempted to be revealed is already revealed.
     * @param row               Row coordinate.
     * @param col               Column coordinate.
     * @param sendToServer      message sent to server.
     * @param out               PrintWriter stream.
     * @param stdIn             BufferedReader to read from user input.
     * @throws ConcentrationException to handle invalid coordinates or
     * attempting to reveal an already revealed card.
     * @throws IOException to handle possible exceptions when reading data.
     *
     */
    private void checkForRevealedCard(int row, int col, String sendToServer,
                                     PrintWriter out, BufferedReader stdIn)
            throws ConcentrationException, IOException {
        if(board.getClientBoard()[row][col] != '.'){
            try{
                throw new ConcentrationException("Card already revealed.");
            }catch (ConcentrationException e){
                System.err.println(e.getMessage());
            }
            messageFromClient(stdIn, out);
        }else{
            out.println(sendToServer);
        }
    }
}