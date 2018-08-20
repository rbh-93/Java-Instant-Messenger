import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientClass extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private String message = "";
    private String serverIP;

    //constructor
    public ClientClass(String hostIp) {
        setTitle("Instant Messenger Client");
        serverIP = hostIp;
        userText = new JTextField();
        //setEditable(false) means before being connected to anyone you are not allowed to type in message box
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(500, 500);
        setVisible(true);
    }

    //server connection
    public void setServerConnection() {
        try {
            serverConnection();
            setupIOStreams();
            chat();

        } catch (EOFException eof) {
            showMessage("\nClient connection ended.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    //server connection
    private void serverConnection() throws IOException {
        showMessage("\nTrying to connect to server...");
        connection = new Socket(InetAddress.getByName(serverIP), 8080);
        showMessage("\nConnected to " + connection.getInetAddress().getHostAddress());
    }

    //IO Streams to send and receive data
    private void setupIOStreams() throws IOException {
        //this creates the path that allows us to connect to the server
        //the client is whatever the Socket connection created
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStream setup complete.");
    }

    //chat() method which allows chat between client and server
    private void chat() throws IOException {
        String message = "\nYou are connected.";
        sendMessage(message);
        enableTyping(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException cnfException) {
                showMessage("\nInvalid Input Type.");
            }
        } while (!message.equals("SERVER - END"));
    }

    //closing streams and sockets
    public void closeConnection() {
        showMessage("\nClosing connection...");
        enableTyping(false);
        try {
            //close output Stream
            output.close();
            //close input Stream
            input.close();
            //close overall connection between client and server
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //send message to server
    private void sendMessage(String message) {
        try {
            //the output Stream gets the String object which we want to send from the client
            output.writeObject("CLIENT - " + message);
            output.flush();
            //displays the message the server has sent
            showMessage("\nCLIENT - " + message);
        } catch (IOException ioException) {
            chatWindow.append("\nMessage cannot be sent.");

        }
    }

    //chatWindow
    private void showMessage(final String text) {
        //invokeLater() method sets aside a thread and updates the GUI
        // the only part we want to update is the chat window every time a text is sent
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }
        );
    }

    //lets user type in their textbox
    private void enableTyping(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );

    }
}




