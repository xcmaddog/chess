package server;

import dataAccess.*;
import exception.ResponseException;
import handler.ClearHandler;
import spark.*;

public class Server {

    //the server stores the location of all the data access objects
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final ClearHandler clearHandler;

    public Server(){
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.clearHandler = new ClearHandler(userDAO, gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db",this::deleteEverything);
        //Spark.get(" ")

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String deleteEverything(Request req, Response res) throws ResponseException {
        System.out.println("You made it to the deleteEverything funciton");
        String result = clearHandler.handleClearAll();
        res.status(200);
        return result;
    }
}
