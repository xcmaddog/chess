package ui;

import dataaccess.DataAccessException;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.ResourceBundle;

public class ChessClient {

    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final BoardDisplay boardDisplay;
    //private WebSocketFacade ws;
    private boolean signedIn = false;

    public ChessClient(String serverUrl, BoardDisplay boardDisplay){
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.boardDisplay = boardDisplay;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "logout" -> logout();
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        }catch(DataAccessException e){
            String errorMsg = e.getMessage();
            switch (e.getMessage()){
                case "failure: 401": { return "Error: unauthorized"; }
                case "failure: 400": {return "Error: bad request";}
                case "failure: 403": {return "Error: already taken";}
                case "failure: 500": {return "Error: (description of error)";}
                default: return "Madsen needs to work on his code";
            }
        }
        catch (Exception ex) {
            String errorMsg =  ex.getMessage();
            return errorMsg;
        }
    }

    public String login(String... params) throws Exception {
        if (signedIn){
            throw new Exception("You are already logged in.");
        }
        try{
            if (params.length == 2){
                String username = params[0];
                String password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                String authToken = server.login(loginRequest);
                this.username = username;
                this.authToken = authToken;
                signedIn = true;
                return String.format("You signed in as %s", this.username);
            }
        }
        catch (DataAccessException e){
            String errorMsg = e.getMessage();
            return errorMsg;
        }
        throw new Exception("Expected login info as <username> <password>");
    }

    public String logout() throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        server.logout(logoutRequest);
        String username = this.username;
        this.username = null;
        this.authToken = null;
        this.signedIn = false;
        return String.format("You logged out %s", username);
    }

    public String register(String... params) throws Exception {
        if (signedIn){
            throw new Exception("You are already logged in.");
        }
        if (params.length == 3){
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            String authToken = server.register(registerRequest);
            this.username = username;
            this.authToken = authToken;
            signedIn = true;
            return String.format("You created a new user: %s, and are now signed in as %s",
                    this.username, this.username);
        }
        throw new Exception("Expected register info as <username> <password> <email>");
    }

    public String help(){
        if (signedIn){
            return String.format("LOGGED IN AS: %s n/" +
                    "create <GAME_NAME> - create a game \n" +
                    "list - to list all of the chess games \n" +
                    "join <GAME_ID> [WHITE|BLACK] - to join a chess game \n" +
                    "observe <GAME_ID> - to observe a chess game \n" +
                    "logout - to log out \n" +
                    "quit - to log out and quit playing chess \n" +
                    "help - to display the possible commands", username);
        }else{
            return preloginHelp;
        }
    }

    private final String preloginHelp = "register <USERNAME> <PASSWORD> <EMAIL> - to create and account \n" +
            "login <USERNAME> <PASSWORD> - to play chess \n" +
            "quit - to quit playing chess \n" +
            "help - to display the possible commands";


}
