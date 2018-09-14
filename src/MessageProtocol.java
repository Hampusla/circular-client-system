import java.net.InetAddress;

public class MessageProtocol {

    /**
     * States Nodes can be in.
     */
    private static final int NEW_NODE = 0;
    private static final int ELECTION = 1;
    private static final int ELECTION_OVER = 2;
    private static final int MESSAGE = 3;
    private Integer state = 0;
    private String socketID;
    private boolean leader = false;
    private int starttime = 0;

    public MessageProtocol(String socketID) {
        this.socketID = socketID;
    }



    public String processInput(String input) {

        String output = "Bullshite";

        String[] messageParts = input.split("\n");
        System.out.println(messageParts[0]);
        System.out.println(messageParts[1]);
        System.out.println(messageParts[2]);

        if (state.equals(NEW_NODE)) {

        }else if (
            state.equals(ELECTION) && messageParts[0].equals("ELECTION_OVER")) {
            state = ELECTION_OVER;
        }else if (state.equals(ELECTION)) {
            /**If state is ELECTION
             *
             *  *Check so message stat is ELECTION
             *      *else set state ELECTION_OVER
             *  *Check if ID is higher or lower then own
             *  *If higher then don't change
             *  *If lower then change to own ID.
             *  *If same then
             *      *Set state to Election_OVER
             *      * Use own ID
             */
            if(messageParts[1].compareTo(socketID) > 0) {
                messageParts[1] = new String(socketID);
            }else if(messageParts[1].compareTo(socketID) == 0) {
                state = ELECTION_OVER;
                messageParts[0] = "ELECTION_OVER";
                messageParts[1] = new String(socketID);
            }
        }else if(state.equals(ELECTION_OVER)) {
            /**
             * If state is ELECTION_OVER
             *
             *
             *  *If the ID is same as own then
             *      *Set state to MESSAGE
             *      *Set leader to true
             *      *send a chosen message
             *  *If ID is different
             *      *set state to MESSAGE
             *      *send the same message
             *      *start timer
             */
            if(messageParts[1].compareTo(socketID) == 0) {
                state = MESSAGE;
                leader = true;
                messageParts[0] = "MESSAGE";
            }
        }



         /**
         * If state is MESSAGE
         *
         *  *If leader is true
         *      *count up round counter
         *      *if counter is x % 1000 = 0 then
         *          *Write in terminal the time per round
         *          *Take new starttime
         *      *send message
         *  *If leader false
         *      *send same message
         *
         */

        /**
         * Add together messagePart 1 and 2
         * fill rest with |0 to 100 chars.
         */
        return output;
    }

}
