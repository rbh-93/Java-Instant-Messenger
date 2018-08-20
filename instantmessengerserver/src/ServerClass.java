import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class ServerClass extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    //Streams help in communication
    //Two types of streams: OutputStream and InputStream
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    //constructor
    public ServerClass() {
        setTitle("Instant Messenger Server");
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
        //server setup
        public void setServer(){
            try{
                server = new ServerSocket(8080, 100);
                while (true){
                    try{
                        waitForConnection();
                        setupIOStreams();
                        chat();

                    }catch (EOFException eof){
                        showMessage("\nServer connection ended.");
                    }finally {
                        closeConnection();
                    }
                }
            }catch (IOException ioException ){
                ioException.printStackTrace();
            }
        }
        //wait for connection method
        private void waitForConnection() throws IOException{
            showMessage("\nWaiting for someone to connect...");
            //connection is made when someone connects to the server
            //server.accept() listens for a connection to be made to this socket and accepts it
            //a new Socket (called connection) is created
            connection = server.accept();
            //getInetAddress() returns address where socket is connected. Returns client address
            //getHostName() returns IP address of client as a String
            showMessage("\nConnected to " + connection.getInetAddress().getHostAddress());
        }
        //IO Streams to send and receive data
        private void setupIOStreams() throws IOException{
            //this creates the path that allows us to connect to the client
            //the client is whatever the Socket connection created
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            showMessage("\nStream setup complete.\n");
        }
        //chat() method which allows chat between client and server
        private void chat() throws IOException{
            String message = "\nYou are connected.";
            sendMessage(message);
            enableTyping(true);
            do {
                try{
                    message = (String) input.readObject();
                    showMessage("\n" + message);
                }catch (ClassNotFoundException cnfException){
                    showMessage("\nInvalid Input Type.");
                }
            }while(!message.equals("CLIENT - END"));
        }
        //closing streams and sockets
        public void closeConnection(){
            showMessage("\nClosing connection...");
            enableTyping(false);
            try{
                //close output Stream
                output.close();
                //close input Stream
                input.close();
                //close overall connection between client and server
                connection.close();
            }catch (IOException ioException){
                ioException.printStackTrace();
            }
        }
        //send message to client
        private void sendMessage(String message){
            try{
                //the output Stream gets the String object which we want to send from the client
                output.writeObject("SERVER - " + message);
                output.flush();
                //displays the message the server has sent
                showMessage("\nSERVER - " + message);
            }catch (IOException ioException){
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
        private void enableTyping(final boolean tof){
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






