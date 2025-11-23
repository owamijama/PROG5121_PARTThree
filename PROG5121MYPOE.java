/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.JOptionPane;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A simple Swing-based chat-like application that lets a user:
 *  - Log in (hard-coded credentials)
 *  - Send a message (with validation)
 *  - Store messages locally in a JSON file
 *  - View stored messages
 *
 * @author RC_Student_Lab
 */
public class PROG5121MYPOE {

    private static boolean exit = false;
    private static int totalMessages = 0;          // messages actually sent
    private static int messageCounter = 0;         // sequential counter for hash
    private static final JSONArray messageStorage = new JSONArray();
    private static List<JSONObject> sentMessages = new ArrayList<>();
    private static int maxMessages;
            

    public static void main(String[] args) {
        // Simple hard-coded login – replace with real auth if needed
        if (!login()) {
            return; // exit if login fails
        }

        while (!exit) {
            String[] options = {
                "1. Send message", 
                "2. Show recently sent messages",
                "3. Display the sender and receipent of all sent messages",
                "4. Display the longest sent message",
                "5. Search Message ID",
                "6. Search for all messages sent to a particular receipent",
                "7. Delete a message using Hash",
                "8. Display report that lists full details of all messages",
                "9. Quit",
                
                
          };  
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an option",
                    "Chat Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
            int Choice = 0;

    switch (Choice) {
    case 0:     {
                    int maxMessages = 0;
                    //Send Message
                    if (messageCounter < maxMessages) {
                        sendMessage();
                    } else {
                        JOptionPane.showMessageDialog(null, "Message limit reached.");
                    }       }
        break;


    case 1: showRecentlySentMessages();
        break;

    case 2:
        displaySenderAndRecipients();
        break;

    case 3:
        DisplayLongestMessage();
        break;

    case 4:
        search_MessageID();
        break;

    case 5:
        Search_Recipient();
        break;

    case 6:
        DeleteUsingHash();
        break;

    case 7:
        ReportwithFullDetails();
        break;

    case 8: //Quit
        exit = true;
        break;
}
             
        }
    }

    /** Hard-coded login – returns true only for admin/5678 */
    private static boolean login() {
        String username = JOptionPane.showInputDialog("Input Username");
        String password = JOptionPane.showInputDialog("Input Password");

        if ("administrator".equals(username) && "5678".equals(password)) {
            JOptionPane.showMessageDialog(null, "Login successful!");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Login unsuccessful");
            return false;
        }
    }

    /** Handles the whole "send message" flow */
    private static void sendMessage() {
        // ---- 1. Generate a unique ID ----
        long messageId = 100000000000L + new Random().nextLong(900000000);

        // ---- 2. Increment counter (used for hash) ----
        messageCounter++;

        // ---- 3. Get and validate recipient ----
        String recipient = JOptionPane.showInputDialog(
                "Enter recipient number (+ccxxxxxxxxxx):");
        recipient = checkRecipient(recipient);
        if (recipient == null) {
            return; // user cancelled or invalid format
        }

        // ---- 4. Get and validate message text ----
        String message = JOptionPane.showInputDialog(
                "Enter your message (max 250 characters):");
        if (message == null || message.isBlank()) {
            JOptionPane.showMessageDialog(null, "Message cannot be empty.");
            return;
        }
        if (message.length() > 250) {
            JOptionPane.showMessageDialog(null,
                    "Message exceeds 250 characters. Please shorten it.");
            return;
        }

        // ---- 5. Build hash ----
        String[] words = message.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : "";
        String hash = String.format("%02d:%d:%s%s",
                Long.parseLong(Long.toString(messageId).substring(0, 2)),
                messageCounter,
                firstWord,
                lastWord);

        // ---- 6. Choose action: Send / Disregard / Store ----
        String[] actions = {"Send", "Disregard", "Store"};
        int action = JOptionPane.showOptionDialog(
                null,
                "Select an action for this message:",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                actions,
                actions[0]);

        if (action == 1) { // Disregard
            JOptionPane.showMessageDialog(null, "Message disregarded.");
            return;
        }

        // ---- 7. Prepare JSON object (used 8. Store if requested) ----
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("MessageID", messageId);
        jsonMessage.put("MessageHash", hash);
        jsonMessage.put("Recipient", recipient);
        jsonMessage.put("Message", message);

        if (action == 2) { // Store
            messageStorage.add(jsonMessage);
            JOptionPane.showMessageDialog(null, "Message stored locally.");
            return;
        }

        // ---- 9. Send (count as transmitted) ----
        totalMessages++;
        JOptionPane.showMessageDialog(null,
                String.format("""
                    Message Sent!
                    Message ID: %d
                    Message Hash: %s
                    Recipient: %s
                    Message: %s
                    """, messageId, hash, recipient, message));
    }
    /** Writes the in-memory JSONArray to a JSON file */

    /** Validates the recipient phone format */
    private static String checkRecipient(String recipient) {
        if (recipient == null || !recipient.matches("\\+\\d{9,12}")) {
            JOptionPane.showMessageDialog(null,
                    "Invalid number: must start with '+' followed by 9-12 digits.");
            return null;
        }
        return recipient;
    }

    /** Shows all messages that were stored (not sent) */
    private static void showRecentlySentMessages() {
        if (messageStorage.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored messages yet.");
            return;
        }

        StringBuilder output = new StringBuilder("Stored Messages:\n\n");
        for (Object obj : messageStorage) {
            JSONObject msg = (JSONObject) obj;
            output.append("ID: ").append(msg.get("MessageID")).append("\n")
                  .append("Recipient: ").append(msg.get("Recipient")).append("\n")
                  .append("Message: ").append(msg.get("Message")).append("\n")
                  .append("Hash: ").append(msg.get("MessageHash")).append("\n")
                  .append("---\n");
        }
        JOptionPane.showMessageDialog(null, output.toString());
    }
   static String createHash(int messageId, int counter, String msg) {
    String[] words = msg.trim().split("\\s+");
    String first = words.length > 0 ? words[0].toUpperCase() : "";
    String last = words.length > 1 ? words[words.length - 1].toUpperCase() : "";
    return String.format("%03d:%d:%s %s", messageId % 100, counter, first, last);
}

static void displaySenderAndRecipients() {
    StringBuilder sb = new StringBuilder("Senders and Recipients:\n");
        Iterable<JSONObject> sentMessages = null;
    for (JSONObject msg : sentMessages) {
        sb.append("Sender: ").append(msg.get("Sender"))
          .append(" Recipient: ").append(msg.get("Recipient")).append("\n");
    }
    JOptionPane.showMessageDialog(null, sb.toString());
}

// Method that is used to display the longest message
static void DisplayLongestMessage() {
    JSONObject longest = null;
        Iterable<JSONObject> sentMessages = null;
    for (JSONObject msg : sentMessages) {
        if (longest == null || msg.get("Message").toString().length() > longest.get("Message").toString().length()) {
            longest = msg;
        }
    }
    
    if (longest != null) {
        JOptionPane.showMessageDialog(null, "Longest Message:\n" +
                                          "Sender: " + longest.get("Sender") + 
                                          "\nRecipient: " + longest.get("Recipient") +
                                          "\nMessage: " + longest.get("Message"));
    } else {
        JOptionPane.showMessageDialog(null, "No messages found.");
    }
}
    // Method for searching recipient.
  static void Search_Recipient() {
    String input = JOptionPane.showInputDialog("Enter recipient number to search:");
    StringBuilder sb = new StringBuilder("Messages for " + input + ":\n");
    boolean found = false;
        Iterable<JSONObject> sentMessages = null;
    for (JSONObject msg : sentMessages) {
        if (msg.get("Recipient").toString().equals(input)) {
            found = true;
            sb.append("ID: ").append(msg.get("MessageID"))
              .append(", Message: ").append(msg.get("Message")).append("\n");
        }
    }
    JOptionPane.showMessageDialog(null, found ? sb.toString() : "No Messages found for recipient.");
}

// Method used to delete the message hash.
static void DeleteUsingHash() {
    String input = JOptionPane.showInputDialog("Enter message hash to delete:");
    Iterator<JSONObject> iterator = sentMessages.iterator();
    while (iterator.hasNext()) {
        JSONObject msg = iterator.next();
        if (msg.get("MessageHash").equals(input)) {
            iterator.remove();
            int Total_messages = 0;
            Total_messages--;
            JOptionPane.showMessageDialog(null, "Message deleted.");
            return;
        }
    
        
        // Method for displaying a report with full details.
   
    }
    
}
  static void ReportwithFullDetails() {
    StringBuilder sb = new StringBuilder("Full message Report:\n");
        Iterable<JSONObject> sentMessages = null;
    for (JSONObject msg : sentMessages) {
        sb.append("ID: ").append(msg.get("MessageID"))
          .append(", Hash: ").append(msg.get("MessageHash"))
          .append(", Sender: ").append(msg.get("Sender"))
          .append(", Recipient: ").append(msg.get("Recipient"))
          .append(", Message: ").append(msg.get("Message"))
          .append("\n");
    }
    JOptionPane.showMessageDialog(null, sb.toString());
}

// Method for storing messages.
static void saveMessagesToJSON() {
    try (FileWriter file = new FileWriter("storedMessages.json")) {
        file.write(messageStorage.toJSONString());
        file.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private static void search_MessageID() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    }





 
