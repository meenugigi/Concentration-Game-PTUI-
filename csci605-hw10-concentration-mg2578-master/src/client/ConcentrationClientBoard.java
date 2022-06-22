/*
Homework 10 : Concentration
File Name : ConcentrationClientBoard.java
 */
package client;

import static common.ConcentrationProtocol.REVEAL_MSG;

/**
 * This class contains a matrix that represents what the client shows the user,
 * e.g. a grid of strings or characters.
 * It creates the initial grid once the square dimension of the board is sent
 * from the server.
 * It provides a way to access and modify the cells in the grid, based on
 * messages from the server.
 * A way to print out the board to the user.
 *
 * @author Meenu Gigi, mg2578@rit.edu
 * @author Vedika Vishwanath Painjane, vp2312@rit.edu
 */
public class ConcentrationClientBoard {

    /** the square dimension of the board */
    private int DIM;
    /** the client board */
    private char clientBoard[][];


    /**
     * A getter to get the board dimension.
     *
     * @return board dimension
     */
    public int getDIM(){
        return DIM;
    }


    /**
     * Getter to get the client board.
     *
     * @return board.
     */
    public char[][] getClientBoard(){
        return clientBoard;
    }


    /**
     * Creates the initial grid with hidden card values.
     *
     * @param message      board dimension
     * @return board
     *
     */
    private char[][] createGrid(String message){
        String[] messageArray = message.split(" ");
        this.DIM = Integer.parseInt(messageArray[1]);
        clientBoard = new char[DIM][DIM];
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                this.clientBoard[row][col] = '.';
            }
        }
        return clientBoard;
    }


    /**
     * Getter to get the initial board.
     *
     * @param message      board dimension
     * @return board
     */
    public char[][] getCreatedGrid(String message){
        return createGrid(message);
    }


    /**
     * Replaces the board position with face-up value of card.
     *
     * @param message      card to be revealed.
     * @return board
     */
    private char[][] serverRevealsCard(String message){
        String[] messageArray = message.split(" ");
        int row = Integer.parseInt(messageArray[1]);
        int col = Integer.parseInt(messageArray[2]);
        String cardCharacterString = messageArray[3];
        char cardValue = cardCharacterString.charAt(0);
        this.clientBoard[row][col] = cardValue;
        return clientBoard;
    }


    /**
     * Getter to get the board with revealed card.
     *
     * @param message      card to be revealed.
     * @return board
     */
    public char[][] getRevealedCard(String message){
        return serverRevealsCard(message);
    }


    /**
     * For a card match.
     *
     * @param message      cards.
     * @return message array.
     */
    private String[] cardMatch(String message){
        return message.split(" ");
    }


    /**
     * Getter to get the card match.
     *
     * @param message      card to be revealed.
     * @return message array.
     */
    public String[] getCardsMatched(String message){
        return cardMatch(message);
    }


    /**
     * Revert to face-down value of card if match not found.
     *
     * @param message      card to be matched.
     * @return board.
     */
    private char[][] cardDoNotMatch(String message){
        String[] messageArray = message.split(" ");
        int card1_row = Integer.parseInt(messageArray[1]);
        int card1_col = Integer.parseInt(messageArray[2]);
        int card2_row = Integer.parseInt(messageArray[3]);
        int card2_col = Integer.parseInt(messageArray[4]);
//          if match not found, hide card value.
        this.clientBoard[card1_row][card1_col] = '.';
        this.clientBoard[card2_row][card2_col] = '.';
        return clientBoard;
    }

    /**
     * Getter to get the board with face down value of unmatched cards..
     *
     * @param message      cards to be matched.
     * @return board.
     */
    public char[][] getCardsNotMatched(String message){
        return cardDoNotMatch(message);
    }


    /**
     * Returns a string representation of the board, for example a
     * 4x4 game that is just underway.
     *
     *   0123
     * 0|G...
     * 1|G...
     * 2|....
     * 3|....
     *
     * @return the board as a string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // build the top row of indices
        str.append("  ");
        for (int col=0; col<this.DIM; ++col) {
            str.append(col);
        }
        str.append("\n");
        // build each row of the actual board
        for (int row=0; row<this.DIM; ++row) {
            str.append(row).append("|");
            // build the columns of the board
            for (int col=0; col<this.DIM; ++col) {
                str.append(clientBoard[row][col]);
            }
            str.append("\n");
        }
        return str.toString();
    }


    /**
     * Send string message to server to reveal card values.
     *
     * @param fromUser      card to be revealed.
     * @return card to be revealed.
     */
    private String messageToServer(String fromUser){
        int row = 0, col = 0;
        String[] messageArray = fromUser.split(" ");
        row = Integer.parseInt(messageArray[0]);
        col = Integer.parseInt(messageArray[1]);
        return String.format(REVEAL_MSG, row, col);
    }

    /**
     * Getter to send message to server to reveal card values.
     *
     * @param fromUser      card to be revealed.
     * @return card to be revealed.
     */
    public String getMessageToServer(String fromUser){
        return messageToServer(fromUser);
    }
}