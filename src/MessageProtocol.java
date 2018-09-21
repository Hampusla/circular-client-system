import java.util.ArrayList;
import java.util.Arrays;


public class MessageProtocol {


    //States Nodes can be in.
    private static final int NEW_NODE = 0;
    private static final int ELECTION = 1;
    private static final int ELECTION_OVER = 2;
    private static final int MESSAGE = 3;
    private Integer state = 0;
    private String socketID;
    private boolean leader = false;
    private long startTime = 0;
    private int roundCounter = 0;

    public MessageProtocol(String socketID) {
        this.socketID = socketID;
    }



    public String processInput(String input) {

        String output = "";

        String[] messageParts = input.split("\n");

        //Set the array to 3 parts
        if (messageParts.length == 1) {
            String firstPart = messageParts[0];
            messageParts = new String[3];
            messageParts[0] = firstPart;
            messageParts[1] = "";
            int length = messageParts[0].length();
            messageParts[2] = new String(new byte[98 - length]);
        }

        //Validate that the message follows format
        if (!validation(messageParts)) {
            throw new IllegalArgumentException();
        }

        //Check if it's a resend of first message
        if (messageParts[0].equals("RESEND_FIRST")) {
            state = NEW_NODE;
        }


        if (state.equals(MESSAGE) && !messageParts[0].equals("MESSAGE")) {
            return null;
        }

        //Start a new Election
        if (state.equals(NEW_NODE)) {
            if (messageParts[0].equals("ELECTION")) {
                state = ELECTION;
            }else {
                state = ELECTION;
                messageParts[0] = "ELECTION";
            }
        }if (
            state.equals(ELECTION) && messageParts[0].equals("ELECTION_OVER")) {
            state = ELECTION_OVER;
        }if (state.equals(ELECTION)) {

            //If state is ELECTION
            //
            //  *Check so message stat is ELECTION
            //      *else set state ELECTION_OVER
            //  *Check if ID is higher or lower then own
            //  *If higher then don't change
            //  *If lower then change to own ID.
            //  *If same then
            //      *Set state to Election_OVER
            //     * Use own ID


            if(messageParts[1].compareTo(socketID) < 0) {

                messageParts[1] = new String(socketID);
            }else if(messageParts[1].compareTo(socketID) == 0) {

                messageParts[0] = "ELECTION_OVER";
            }

        }if(state.equals(ELECTION_OVER)) {

            // If state is ELECTION_OVER
            //
            //
            //  *If the ID is same as own then
            //      *Set state to MESSAGE
            //      *Set leader to true
            //      *send a chosen message
            //      *start timer
            //  *If ID is different
            //      *set state to MESSAGE
            //      *send the same message

            if(messageParts[1].compareTo(socketID) == 0) {
                state = MESSAGE;
                leader = true;
                messageParts[0] = "MESSAGE";
                messageParts[1] = "This is a message";
                startTime = System.currentTimeMillis();
            }else {
                state = MESSAGE;
            }
        }if(state.equals(MESSAGE) &&
            leader &&
            messageParts[0].equals("MESSAGE")) {

             // If state is MESSAGE
             //
             //  *If leader is true
             //      *count up round counter
             //      *if counter is x % 1000 = 0 then
             //          *Write in terminal the time per round
             //          *Take new starttime
             //      *send message
             //  *If leader false
             //      *send same message

            roundCounter++;
            if((roundCounter % 1000) == 0) {
                long time = System.currentTimeMillis() - startTime;
                System.out.println("Time per round " + time/1000);
                startTime = System.currentTimeMillis();

            }
        }




        // Add together messagePart 1 and 2
        // fill rest with |0 to 100 chars.
        output = String.join(
            "\n", messageParts[0], messageParts[1]);
        output = output.concat("\n");

        char[] filler = new char[100 - output.length()];
        Arrays.fill(filler, '\0');

        output = output.concat(new String(filler));

        return output;
    }

    boolean validation(String[] messageParts) {

        ArrayList<String> validTypes = new ArrayList<>();
        validTypes.add("NEW_NODE");
        validTypes.add("RESEND_FIRST");
        validTypes.add("ELECTION");
        validTypes.add("ELECTION_OVER");
        validTypes.add("MESSAGE");

        int numberOfRows = messageParts.length;
        if (numberOfRows != 3) {
            return false;
        }

        int length = messageParts[0].length() + messageParts[1].length() + messageParts[2].length() + 2;
        if (length != 100) {
            return false;
        }

        return validTypes.contains(messageParts[0]);
    }

}
