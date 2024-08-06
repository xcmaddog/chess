package ui;

import dataaccess.DataAccessException;
import request.*;
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
                case "login" : { yield login(params);}
                case "logout" : { yield logout();}
                case "register" : { yield register(params);}
                case "create" : { yield createGame(params);}
                case "list" : { yield listGames();}
                case "join" : { yield joinGame();}
                case "observe": { yield observeGame();}
                case "quit" : {
                    if(signedIn){
                        String s = logout() + "\nquit";
                        yield s;
                    }else{
                        String s = "quit";
                        yield s;
                    }
                }
                default : { yield help();}
            };
        }catch(DataAccessException e){
            String errorMsg = e.getMessage();
            var errorWords = errorMsg.split(" ");
            var errorCode = errorWords[1];
            switch (errorCode){
                case "401": { return "Error: unauthorized"; }
                case "400": {return "Error: bad request";}
                case "403": {return "Error: already taken";}
                case "500": {
                    StringBuilder description = new StringBuilder();
                    for(int i = 2; i < errorWords.length; i++){
                        description.append(errorWords[i]);
                    }
                    return String.format("Error: %s", description.toString());
                }
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

    public String createGame(String... params) throws Exception {
        if (!signedIn){
            throw new Exception("You need to be signed in to create a game");
        }
        if(params.length == 1){
            String gameName = params[0];
            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
            String result = server.createGame(createGameRequest, authToken);
            //return String.format("You created a new game: %s \n" +
            //        "Its game ID is: %s", gameName,gameID);
            return result;
        }
        throw new Exception("Expected one game name (no spaces)");
    }

    public String listGames() throws Exception {
        if (!signedIn){
            throw new Exception("You need to be signed in to list the games");
        }
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        String listedGames = server.listGames(listGamesRequest);
        return listedGames;
    }

    public String joinGame(String... params){
        return null;
    }

    public String observeGame(String... params){
        return null;
    }

    public String help(){
        if (signedIn){
            return String.format("""
                    LOGGED IN AS: %s\s
                    create <GAME_NAME> - create a game\s
                    list - to list all of the chess games\s
                    join <GAME_ID> [WHITE|BLACK] - to join a chess game\s
                    observe <GAME_ID> - to observe a chess game\s
                    logout - to log out\s
                    quit - to log out and quit playing chess\s
                    help - to display the possible commands""", username);
        }else{
            return preloginHelp;
        }
    }

    public boolean isSignedIn(){
        boolean result = signedIn;
        return result;
    }

    private final String preloginHelp = "register <USERNAME> <PASSWORD> <EMAIL> - to create and account \n" +
            "login <USERNAME> <PASSWORD> - to play chess \n" +
            "quit - to quit playing chess \n" +
            "help - to display the possible commands";


}
