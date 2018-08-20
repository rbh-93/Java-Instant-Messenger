import javax.swing.*;

class ClientMain {
    public static void main(String[] args) {
        //client connects to localhost for testing
        ClientClass client = new ClientClass("127.0.0.1");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setServerConnection();

    }
}
