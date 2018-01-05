package game.serverConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerPrinter {

    private PrintWriter pw;

    public ServerPrinter(Socket socket) throws IOException {
        this.pw = new PrintWriter(socket.getOutputStream(), true);
    }

    public void writeToServer(String message){
        pw.println(message);
    }
}
