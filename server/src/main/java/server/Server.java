package server;

import dataAccess.*;
import exception.ResponseException;
import handler.ClearHandler;
import handler.UserHandler;
import spark.*;

import java.util.Objects;

//import static jdk.internal.net.http.common.Log.headers;

public class Server {

    //the server stores the location of all the data access objects
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final ClearHandler clearHandler;
    private final UserHandler userHandler;

    public Server(){
        this.userDAO = new MemoryUserDAO();
        this.gameDAO = new MemoryGameDAO();
        this.authDAO = new MemoryAuthDAO();
        this.clearHandler = new ClearHandler(userDAO, gameDAO, authDAO);
        this.userHandler = new UserHandler(userDAO,gameDAO,authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db",this::deleteEverything);
        Spark.post("/user", this::registerUser);
        Spark.post("/session",this::loginUser);
        Spark.delete("/session", this::logoutUser);
        //Spark.get(" ")


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String deleteEverything(Request req, Response res) throws ResponseException {

        //System.out.println("You made it to the deleteEverything function");

        String result = clearHandler.handleClearAll();
        res.status(200);
        return result;
    }

    private String registerUser(Request req, Response res) throws ResponseException {

        //System.out.println("You made it to the registerUser function and the request body is: ");
        //System.out.println(req.body());

        try{
            String result = userHandler.handleRegistration(req.body()); //might need to add a throw in UserHandler for invalid inputs
            res.status(200);
            return result;
        }
        catch(dataaccess.DataAccessException dataAccessException){
            if(dataAccessException.getMessage() == "A user with that username already exists"){
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else if(dataAccessException.getMessage() == "username not provided"){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else if(dataAccessException.getMessage() == "password not provided"){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else if (dataAccessException.getMessage() == "email not provided"){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else{
                res.status(500);
                String description = dataAccessException.getMessage();
                return "{ \"message\": \"Error: " + description + "\" }";
            }
        }
    }

    private String loginUser (Request req, Response res) throws ResponseException {
        try{
           String result = userHandler.handleLogin(req.body());
           res.status(200);
           return result;
        }catch (dataaccess.DataAccessException dataAccessException) {
            if(dataAccessException.getMessage() == "The password provided was incorrect"){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else if (dataAccessException.getMessage() == "The username provided is invalid"){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else{
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }

    private String logoutUser (Request req, Response res){
        try{
            String theAuthToken = req.headers("authorization");
            String result = userHandler.handleLogout(theAuthToken);
            res.status(200);
            return result;
        }catch(dataaccess.DataAccessException dataAccessException){
            if (Objects.equals(dataAccessException.getMessage(), "The authToken was not recognized")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else{
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }

}
