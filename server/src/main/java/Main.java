import chess.*;
import dataaccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        Server myServer = new Server();
        myServer.run(8080);


    }
}