package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.GamesHandler;
import handler.UserHandler;
import spark.*;

import java.util.Objects;

//import static jdk.internal.net.http.common.Log.headers;

public class Server {

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GamesHandler gameHandler;

    public Server() {
        UserDAO userDAO = new SQLUserDAO();
        GameDAO gameDAO = new SQLGameDAO();
        //the server stores the location of all the data access objects
        AuthDAO authDAO = new SQLAuthDAO();

        this.clearHandler = new ClearHandler(userDAO, gameDAO, authDAO);
        this.userHandler = new UserHandler(userDAO, gameDAO, authDAO);
        this.gameHandler = new GamesHandler(userDAO, gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db",this::deleteEverything);
        Spark.post("/user", this::registerUser);
        Spark.post("/session",this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String deleteEverything(Request req, Response res) {

        //System.out.println("You made it to the deleteEverything function");
        try{
            String result = clearHandler.handleClearAll();
            res.status(200);
            return result;
        }
        catch (Exception e){
            res.status(500);
            return String.format("\"message\": \"Error: %s",e.getMessage());
        }
    }

    private String registerUser(Request req, Response res) {

        try{
            String result = userHandler.handleRegistration(req.body()); //might need to add a throw in UserHandler for invalid inputs
            res.status(200);
            return result;
        }
        catch(DataAccessException dataAccessException){
            if(Objects.equals(dataAccessException.getMessage(), "A user with that username already exists")){
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else if(Objects.equals(dataAccessException.getMessage(), "username not provided")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else if(Objects.equals(dataAccessException.getMessage(), "password not provided")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else if (Objects.equals(dataAccessException.getMessage(), "email not provided")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else{
                res.status(500);
                String description = dataAccessException.getMessage();
                return "{ \"message\": \"Error: " + description + "\" }";
            }
        }
    }

    private String loginUser (Request req, Response res) {
        try{
           String result = userHandler.handleLogin(req.body());
           res.status(200);
           return result;
        }catch (DataAccessException dataAccessException) {
            if(Objects.equals(dataAccessException.getMessage(), "The password provided was incorrect")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else if (Objects.equals(dataAccessException.getMessage(), "The username provided is invalid")){
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
        }catch(DataAccessException dataAccessException){
            if (Objects.equals(dataAccessException.getMessage(), "The authToken was not recognized")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else{
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }

    private String createGame (Request req, Response res) {
        try{
            String theAuthToken = req.headers("authorization");
            String result = gameHandler.handleCreateGame(theAuthToken,req.body());
            res.status(200);
            return result;
        }
        catch (DataAccessException dataAccessException){
            if (Objects.equals(dataAccessException.getMessage(), "Invalid AuthToken")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else if (Objects.equals(dataAccessException.getMessage(), "Invalid request")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }else {
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }

    private String joinGame(Request req, Response res) {
        try{
            String theAuthToken = req.headers("authorization");
            String result = gameHandler.handleJoinGame(theAuthToken,req.body());
            res.status(200);
            return result;
        }
        catch (DataAccessException dataAccessException){
            if(Objects.equals(dataAccessException.getMessage(), "Invalid request")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            } else if (Objects.equals(dataAccessException.getMessage(), "There is already someone playing white in this game") ||
                        Objects.equals(dataAccessException.getMessage(), "There is already someone playing black in this game")) {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else if (Objects.equals(dataAccessException.getMessage(), "Invalid AuthToken")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }else if (Objects.equals(dataAccessException.getMessage(), "Game not found")){
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            } else{
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }

    private String listGames (Request req, Response res){
        try{
            String theAuthToken = req.headers("authorization");
            String result = gameHandler.handleListGames(theAuthToken);
            res.status(200);
            return result;
        }
        catch(DataAccessException dataAccessException){
            if (Objects.equals(dataAccessException.getMessage(), "Invalid AuthToken")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            } else{
                res.status(500);
                return "{ \"message\": \"Error: " + dataAccessException.getMessage() + "\" }";
            }
        }
    }


}
