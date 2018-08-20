import javax.swing.*;

class ServerTest {
    public static void main(String[] args){
        ServerClass server = new ServerClass();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.setServer();
    }
}
