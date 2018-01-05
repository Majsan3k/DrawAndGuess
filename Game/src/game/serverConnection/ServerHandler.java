package game.serverConnection;

import game.mainFrame.GameFrame;

public class ServerHandler {

    private GameFrame gameFrame;
    private ServerPrint serverPrint;
    private ServerReader serverReader;

    public ServerHandler(GameFrame gf, ServerPrint sp, ServerReader sr){
        this.gameFrame = gf;
        this.serverPrint = sp;
        this.serverReader = sr;
    }



}
