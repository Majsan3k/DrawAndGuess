package game.mainFrame;

import game.mainFrame.GameFrame;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Game {

    public static void main(String args[]) {

        String host = "127.0.0.1";
        int port = 2000;

        if(args.length > 2){
            System.out.println("Too many arguments");
            System.exit(1);
        }
//        if(args.length > 0){
//            host = args[0];
//        }
//        if(args.length > 1){
//            port = Integer.parseInt(args[1].toString());
//        }

        try {
            Socket socket = new Socket(host, port);
            new GameFrame(socket);

        } catch (SocketException s){
            System.out.println(s.getMessage());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
