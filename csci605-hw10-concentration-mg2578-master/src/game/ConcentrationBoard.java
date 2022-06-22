package game;

import common.ConcentrationException;
import common.ConcentrationProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the game board for the concentration game.
 *
 * @author RIT CS
 */
public class ConcentrationBoard implements ConcentrationProtocol {
    /** the smallest board is 2x2 */
    private final static int MIN_DIM = 2;
    /** the largest board is 6x6 */
    private final static int MAX_DIM = 6;

    /** the square dimension of the board */
    private int DIM;
    /** the actual board is a 2-D grid of cards */
    private ConcentrationCard board[][];
    /** if the first card is revealed this is set (otherwise null) */
    private ConcentrationCard revealedCard;
    /** the number of card matches that have been made so far */
    private int matches;

    private int flag;


    int flag_for_revealing = 0;
    ConcentrationBoard.CardMatch card;


    String errorString;


    int clientNum;

    /**
     * An internal class used to determine a card match or mismatch.
     */
    public class CardMatch {
        /** the first card */
        private ConcentrationCard card1;
        /** the second card */
        private ConcentrationCard card2;
        /** do the cards match? */
        private boolean match;

        /**
         * Create a new instance from the two revealed cards and whether they matches.
         *
         * @param card1 first card
         * @param card2 second card
         * @param match do the cards match or not
         */
        public CardMatch(ConcentrationCard card1, ConcentrationCard card2, boolean match) {
            this.card1 = card1;
            this.card2 = card2;
            this.match = match;
        }

        /**
         * Get the first card.
         *
         * @return first card
         */
        public ConcentrationCard getCard1() {
            return this.card1;
        }

        /**
         * Get the second card.
         *
         * @return second card
         */
        public ConcentrationCard getCard2() {
            return this.card2;
        }

        /**
         * Is there a card match?
         *
         * @return whether there was a match or not
         */
        public boolean isMatch() {
            return this.match;
        }

        /**
         * Is it ready to check for a match - both cards should be non-null
         *
         * @return is a match ready to check?
         */
        public boolean isReady() {
            return this.card1 != null && this.card2 != null;
        }
    }

    /**
     * Create the board in non-cheat mode.
     *
     * @param DIM square dimension
     * @throws ConcentrationException if the dimension is illegal
     */
    public ConcentrationBoard(int DIM) throws ConcentrationException {
        this(DIM, false);
    }

    /**
     * Create the board.
     *
     * @param DIM square dimension
     * @param cheat whether to display the fully revealed board or not
     * @throws ConcentrationException if the dimensions are invalid
     */
    public ConcentrationBoard(int DIM, boolean cheat) throws ConcentrationException {
        // check for bad dimensions
        if (DIM < MIN_DIM || DIM > MAX_DIM) {
            throw new ConcentrationException("Board size out of range: " + DIM);
        } else if (DIM % 2 != 0) {
            throw new ConcentrationException("Board size not even: " + DIM);
        }

        /** create the pair of cards and shuffle them */
        List<Character> chars = new ArrayList<>(DIM*DIM);
        for (char i=0; i<(DIM*DIM)/2; ++i) {
            chars.add((char)(i+'A'));
            chars.add((char)(i+'A'));
        }
        Collections.shuffle(chars);

        /**
         * Create the grid of cards and populate from the shuffled list.
         */
        this.DIM = DIM;
        this.board = new ConcentrationCard[DIM][DIM];
        for (int row=0; row<DIM; ++row) {
            for (int col=0; col<DIM; ++col) {
                this.board[row][col] = new ConcentrationCard(row, col, chars.remove(0));
            }
        }

        // if cheat mode is enabled display the fully revealed board
        if (cheat) {
            System.out.println("SOLUTION:");
            System.out.println(this.toString());
        }

        // hide all the cards in the board
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                this.board[row][col].hide();
            }
        }

        // initialize rest of state
        this.revealedCard = null;
        this.matches = 0;
    }

    /**
     * Get the square dimension of the board
     * @return square dimension
     */
    public int getDIM() {
        return this.DIM;
    }

    /**
     * Get a card from the board at a coordinate.
     *
     * @param row the row
     * @param col the column
     * @return the card
     * @throws ConcentrationException if the coordinate is invalid
     */
    public ConcentrationCard getCard(int row, int col) throws ConcentrationException {
        //TODO YOUR CODE HERE

        if(row >= getDIM() || col >= getDIM()){
            errorString = "Coordinate out of range.";
            throw new ConcentrationException("Coordinate out of range.");
        }
        return board[row][col];
    }




    /**
     * Reveal a hidden card.
     *
     * @param row the row
     * @param col the column
     * @return resulting information about a potential match or mismatch
     * @throws ConcentrationException if the game is over, the coordinate is invalid, or the
     *     card has already been revealed.
     */
    public CardMatch reveal(int row, int col) throws ConcentrationException {
        //TODO YOUR CODE HERE
        flag++;
        if(flag == 1){
            return revealFirstCard(row, col);
        }
        else if(flag == 2) {
            return revealSecondCard(row, col);
        }
        return null;
    }


    private CardMatch revealFirstCard(int row, int col) throws ConcentrationException {
        boolean decision = false;
        CardMatch card;
        if(getCard(row, col).isHidden() == false){
            flag--;
            throw new ConcentrationException("Card Revealed.");
        }
        else {
            revealedCard = getCard(row, col);
            revealedCard.reveal();
            System.out.println(revealedCard);
            card = new CardMatch(revealedCard, null, decision);
        }
        return card;
    }


    private CardMatch revealSecondCard(int row, int col) throws ConcentrationException {
        boolean decision = false;
        CardMatch card;
        if (getCard(row, col).isHidden() == false) {
            flag--;
            throw new ConcentrationException("Card Revealed.");
        } else {
            card = new CardMatch(revealedCard, getCard(row, col), decision);
            if (card.isReady()) {
                if (card.getCard1().equals(card.getCard2())) {
                    decision = true;
                    card.getCard2().reveal();
                } else {
                    card.getCard1().hide();
                    card.getCard2().hide();
                }
            }
            card = new CardMatch(revealedCard, getCard(row, col), decision);
            flag = 0;
        }
        return card;
    }





    /**
     * The game is over when all the matches have been made.
     *
     * @return whether the game is over or not
     */
    public boolean gameOver() {
        //TODO YOUR CODE HERE

        if(matches == (getDIM()*getDIM()) / 2){
            return true;
        }
        return false;
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
                ConcentrationCard card = this.board[row][col];
                // based on whether the card is hidden or not display
                // build with the correct letter
                if (card.isHidden()) {
                    str.append(ConcentrationCard.HIDDEN);
                } else {
                    str.append(this.board[row][col].getLetter());
                }
            }
            str.append("\n");
        }
        return str.toString();
    }


    public String processInput(String theInput) throws ConcentrationException {
        String theOutput = null;

        int row = 0;
        int col = 0;

        if (theInput == null){
            theOutput = String.format(BOARD_DIM_MSG,DIM);
        }
        else if (theInput.startsWith(REVEAL)) {
            System.out.println(theInput);
            String[] messageArray = theInput.split(" ");
            row = Integer.parseInt(messageArray[1]);
            col = Integer.parseInt(messageArray[2]);
            char letter = 0;
            try {
                letter = getCard(row, col).getLetter();
            }
            catch (ConcentrationException e){
                System.err.println(e.getMessage());
                errorString = "Coordinate out of range.";
                return String.format(ERROR_MSG, errorString);
            }

            theOutput = String.format(CARD_MSG, row, col, letter);
            flag_for_revealing++;
            if (canRevealCard(row, col))
                return String.format(ERROR_MSG, errorString);
        }
        else if(theInput.equals("DECISION")){
            flag_for_revealing = 0;
            theOutput = decideCardMatch(card);

        }
        else if(theInput.equals("GAME_OVER")){
            theOutput = GAME_OVER_MSG;
        }
        return theOutput;
    }


    private boolean canRevealCard(int row, int col) {
        if(flag_for_revealing == 1){
            try{
                card = reveal(row, col);
            }
            catch (ConcentrationException e){
                System.err.println(e.getMessage());
                errorString = "Card already revealed. ";
                flag_for_revealing--;
                return true;
            }
        }
        else if (flag_for_revealing == 2){
            try{
                card = reveal(row, col);
            }
            catch (ConcentrationException e){
                System.err.println(e.getMessage());
                errorString = "Card already revealed. ";
                flag_for_revealing--;
                return true;
            }
        }
        return false;
    }


    private String decideCardMatch(CardMatch card) {
        if (card.isMatch()) {
            matches++;
            return String.format(MATCH_MSG,
                    card.getCard1().getRow(), card.getCard1().getCol(),
                    card.getCard2().getRow(), card.getCard2().getCol());
        } else {
            return String.format(MISMATCH_MSG,
                    card.getCard1().getRow(), card.getCard1().getCol(),
                    card.getCard2().getRow(), card.getCard2().getCol());
        }
    }




//        if (state == WAITING) {
//            System.out.println("Client #" + clientNum + " started...");
//            theOutput = String.format(BOARD_DIM_MSG,
//                    DIM);
//            state = REVEAL_TO_CLIENT;
//        } else if (state == REVEAL_TO_CLIENT) {
//            if(!theInput.equals("DECISION")){
//                flag_for_revealing++;
//            }
//
//            String[] messageArray = theInput.split(" ");
//            int row = Integer.parseInt(messageArray[1]);
//            int col = Integer.parseInt(messageArray[2]);
//            char letter = getCard(row, col).getLetter();
//            if(row >= DIM && col >= DIM ){
//                state = ERROR;
//                errorString = "Coordinate out of " +
//                        "range.";
//                theOutput = String.format(ERROR_MSG, errorString);
////                    throw new ConcentrationException("Coordinate out of " +
////                            "range.");
//            }else{
//                theOutput = String.format(CARD_MSG, row, col, letter);
//            }
//            if (flag_for_revealing == 1) {
//                card = reveal(row, col);
//            } else if (flag_for_revealing == 2) {
//                //THIS WAS CREATING THE ISSUE, SO REMOVED THIS IF
//                // CONDITION
//                //if (revealedCard != null) {
//                if(!getCard(row, col).isHidden()){
//                    state = ERROR;
//                    errorString = "Already revealed card" +
//                            " cannot be entered again.";
//                    theOutput = String.format(ERROR_MSG, errorString);
////                            throw new ConcentrationException("Already revealed card" +
////                                    " cannot be entered again.");
//                }
////                        if (revealedCard.equals(getCard(row, col))) {
////                            throw new ConcentrationException("Already revealed card" +
////                                    " cannot be entered again.");
////                        }
//                else{
//                    card = reveal(row, col);
//                    state = DECIDE;
//                    flag_for_revealing = 0;
//                }
//            }
//        } else if (state == DECIDE) {
//            if (card.isMatch()) {
//                theOutput = String.format(MATCH_MSG,
//                        card.getCard1().getRow(), card.getCard1().getCol(),
//                        card.getCard2().getRow(), card.getCard2().getCol());
//            } else {
//                theOutput = String.format(MISMATCH_MSG,
//                        card.getCard1().getRow(), card.getCard1().getCol(),
//                        card.getCard2().getRow(), card.getCard2().getCol());
//            }
//
//            if (gameOver()) {
//                status = true;
//                state = GAME_OVER;
//            } else {
//                state = REVEAL_TO_CLIENT;
//            }
//        } else if (state == GAME_OVER) {
//            theOutput = GAME_OVER_MSG;
//        }else if(state == ERROR){
//            state = REVEAL_TO_CLIENT;
//            if (flag_for_revealing == 1){
//                state = REVEAL_TO_CLIENT;
//            }else if(flag_for_revealing == 2){
//                state = DECIDE;
//            }
//            //theOutput = String.format(ERROR_MSG, errorString);
//        }
//        synchronized (this){
//            System.out.println("Sending to Client #" + clientNum + ": " + theOutput);
//        }
//
//        if(state == ERROR){
//
//        }
//        return theOutput;
////        catch (ConcentrationException e) {
////            state = ERROR;
////            System.err.println(e.getMessage());
////        }catch (ArrayIndexOutOfBoundsException e){
////            state = ERROR;
////            errorString = "Coordinate entered incorrectly.";
////            System.err.println("Coordinate entered incorrectly.");
////        }
////        return theOutput;
//    }


//    public boolean gameOverStatus() {
//        return status;
//    }

    public void setClientNum(int num){
        this.clientNum = num;
    }
}
