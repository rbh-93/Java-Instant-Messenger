import javax.swing.*;

class ServerMain {
    public static void main(String[] args){
        ServerClass server = new ServerClass();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.setServer();
    }
}
