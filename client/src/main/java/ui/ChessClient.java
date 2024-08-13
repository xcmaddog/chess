package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mydataaccess.DataAccessException;
import model.GameData;
import request.*;
import serverfacade.ServerFacade;
import websocketfacade.WebSocketFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final BoardDisplay boardDisplay;
    private WebSocketFacade ws;
    private boolean signedIn = false;
    private boolean inGameplay = false;
    private boolean isObserving = false;
    private boolean shouldDispWhite = true;
    private int gameID = 0;
    private Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HashMap<ChessPosition,
            ChessPiece>>(){}.getType(), new PositionPieceMapAdapter()).create();

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
                case "join" : { yield joinGame(params);}
                case "observe": { yield observeGame(params);}
                case "leave" : {yield leaveGame();}
                case "redraw" : {yield redrawBoard();}
                case "move" :{yield makeMove(params);}
                case "highlight" : {yield highlight(params);}
                case "quit" : {
                    if(inGameplay || isObserving){
                        leaveGame();
                    }
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

    private String highlight(String... params) throws Exception {
        if (!signedIn){
            throw new Exception("You need to be signed in to interact with a game");
        }
        if(!(inGameplay || isObserving)){
            throw new Exception("You must be playing or observing a game to highlight the valid moves");
        }
        if (params.length == 1){
            ChessPosition startingPosition = stringToPosition(params[0]);
            GameData gameData = getGameData(gameID);
            ChessGame chessGame = gameData.getGame();
            Collection<ChessMove> validMoves = chessGame.validMoves(startingPosition);
            HashSet<ChessPosition> validSquares = new HashSet<>();
            for(ChessMove m : validMoves){
                validSquares.add(m.getEndPosition());
            }
            if (shouldDispWhite){
                boardDisplay.displayWhiteBoard(chessGame, validSquares);
            }else{
                boardDisplay.displayBlackBoard(chessGame, validSquares);
            }
            String result = "Legal moves are highlighted light gray";
            return result;
        }
        throw new Exception("Expected highlight information as <startingSquare>");
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

    public String logout() throws Exception {
        if (inGameplay || isObserving){
            leaveGame();
        }
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

    public String joinGame(String... params) throws Exception {
        /*
        This first big block of code deals with the server to add the user to the
        database and display some stuff to the terminal
        */
        if (!signedIn){
            throw new Exception("You need to be signed in to list the games");
        }
        if (isObserving || inGameplay){
            throw new Exception("You cannot join a game if you are already observing or playing another game");
        }
        if(params.length == 2){
            String theID = params[0];
            int gameID = Integer.parseInt(theID);
            this.gameID = gameID;
            ChessGame.TeamColor teamColor;
            if (params[1].equalsIgnoreCase("BLACK")){
                teamColor = ChessGame.TeamColor.BLACK;
                shouldDispWhite = false;
            } else if (params[1].equalsIgnoreCase("WHITE")){
                teamColor = ChessGame.TeamColor.WHITE;
                shouldDispWhite = true;
            } else{
                throw new Exception("Expected to join either \"WHITE\" or \"BLACK\" team");
            }
            JoinGameRequest joinGameRequest = new JoinGameRequest(teamColor, gameID);

            /*
            This chunk of code is to deal with the websocket
             */
            ws = new WebSocketFacade(serverUrl, boardDisplay);
            ws.joinGame(username, authToken, gameID);
            inGameplay = true;
            String result;
            try{
                String json = server.joinGame(joinGameRequest, authToken);
                GameData gameData = gson.fromJson(json, GameData.class);
                result = String.format("You joined game %s (%s) as the %s player",
                        gameData.getGameName(),gameData.getGameID(),teamColor);
            } catch (DataAccessException e){
                ws.leaveGame(username, authToken, gameID);
                this.gameID = 0;
                isObserving = false;
                inGameplay = false;
                result = String.format("You failed to join game %s as the %s player",
                        gameID,teamColor);
            }
            return result;
        }
        throw new Exception("Expected join info as <gameID> [BLACK|WHITE]");
    }

    public String observeGame(String... params) throws Exception {
        if (!signedIn){
            throw new Exception("You need to be signed in to list the games");
        }
        if (isObserving || inGameplay){
            throw new Exception("You cannot join a game if you are already observing or playing a game");
        }

        if (params.length == 1){
            String theID = params[0];
            int gameID = Integer.parseInt(theID);
            GameData gameData = getGameData(gameID);
            if((Objects.equals(username, gameData.getBlackUsername())) ||
                    (Objects.equals(username, gameData.getWhiteUsername()))){
                throw new Exception("You cannot observe a game if you are one of the players");
            }
            ws = new WebSocketFacade(serverUrl, boardDisplay);
            ws.joinGame(username, authToken, gameID);
            shouldDispWhite = true;
            this.gameID = gameID;
            isObserving =true;
            inGameplay = false;

            return String.format("You are now observing %s.\n" +
                    "%s is playing as white and\n" +
                    "%s is playing as black",
                    gameData.getGameName(), gameData.getWhiteUsername(), gameData.getBlackUsername());
        }
        throw new Exception("Expected observe info as <gameID>");
    }

    public String redrawBoard() throws Exception{
        if (!signedIn){
            throw new Exception("You need to be signed in to redraw the board");
        }
        if (!(isObserving || inGameplay)){
            throw new Exception("You need to be observing or playing a game to redraw the board");
        }
        GameData gameData = getGameData(gameID);
        if(shouldDispWhite){
            boardDisplay.displayWhiteBoard(gameData.getGame(), null);
        }else{
            boardDisplay.displayBlackBoard(gameData.getGame(), null);
        }

        return String.format("Redrew game %s", gameID);
    }

    public String leaveGame() throws Exception {
        if (!(isObserving || inGameplay)){
            throw new Exception("You need to be observing or playing a game to leave it");
        }
        try{
            ws.leaveGame(username, authToken, gameID);
            String result = String.format("You successfully left game %s", gameID);
            gameID = 0;
            isObserving = false;
            inGameplay = false;
            return result;
        } catch(Exception e){
            throw e;
        }
    }

    public String makeMove(String... params) throws Exception {
        if(!inGameplay){
            throw new Exception("You need to be playing a game to make a move");
        }
        ChessMove chessMove = stringToMove(params);
        ws.makeMove(username, authToken, gameID, chessMove);
        String result;
        /*
        if (params.length == 2){
            result = String.format("You successfully moved from %s to %s",params[0], params[1]);
        }else{
            result = String.format("You successfully moved from %s to %s and the piece was promoted to a %s",
                    params[0], params[1], params[2]);
        }
         */
        return "";
    }

    public String help(){
        if (!signedIn){
            return preloginHelp;
        } else if(inGameplay){
            return gameplayHelp;
        } else if (isObserving){
            return observingHelp;
        } else { return String.format("""
                    LOGGED IN AS: %s\s
                    create <GAME_NAME> - create a game\s
                    list - to list all of the chess games\s
                    join <GAME_ID> [WHITE|BLACK] - to join a chess game\s
                    observe <GAME_ID> - to observe a chess game\s
                    logout - to log out\s
                    quit - to log out, type quit a second time to quit playing\s
                    help - to display the possible commands""", username);
        }
    }

    public boolean isSignedIn(){
        boolean result = signedIn;
        return result;
    }

    private ChessMove stringToMove(String... params) throws Exception {
        if (params.length == 2 || params.length == 3){
            ChessPosition start = stringToPosition(params[0]);
            ChessPosition end = stringToPosition(params[1]);

            GameData gameData = getGameData(gameID);

            ChessPiece potentialPiece = gameData.getGame().getBoard().getPiece(start);
            if(potentialPiece == null){
                String message = String.format("There is not a piece at %s", params[0]);
                throw new Exception(message);
            }
            ChessMove chessMove;
            if (params.length == 3){
                ChessPiece.PieceType pieceType = stringToType(params[2]);
                chessMove = new ChessMove(start, end, pieceType);
            } else{
                chessMove = new ChessMove(start, end);
            }
            return chessMove;
        }
        throw new Exception("Expected input in format: move <startingSquare> <finishingSquare> \n" +
                "or move <startingSquare> <finishingSquare> <promoteTo>");
    }

    private ChessPosition stringToPosition(String writtenPosition) throws Exception {
        char[] validLetters = {'a','b','c','d','e','f','g','h'};
        if(writtenPosition.length() == 2){
            char first = Character.toLowerCase(writtenPosition.charAt(0));
            char second = writtenPosition.charAt(1);
            int col = 0;
            int counter = 0;
            for (char c : validLetters){
                counter++;
                if (c == first){
                    col = counter;
                }
            }
            if (col == 0){
                throw new Exception("Expected the first character to be a letter between 'a' and 'h'");
            }
            int row = Character.getNumericValue(second);
            if (row < 1 || row > 8 ){
                throw new Exception("Expected the second character to be a digit between 1 and 8");
            }
            return new ChessPosition(row, col);
        }
        throw new Exception("Position expected to be two characters long. ie: e4");
    }

    private ChessPiece.PieceType stringToType (String writtenType) throws Exception {
        writtenType = writtenType.toLowerCase();
        return switch (writtenType){
            case "queen","q" : {yield ChessPiece.PieceType.QUEEN;}
            case "king" : {yield ChessPiece.PieceType.KING;}
            case "rook", "castle", "r","c" : {yield ChessPiece.PieceType.ROOK;}
            case "bishop","b" : {yield ChessPiece.PieceType.BISHOP;}
            case "knight", "horse","k", "h" : {yield ChessPiece.PieceType.KNIGHT;}
            case "pawn", "p" : {yield ChessPiece.PieceType.PAWN;}
            default: {throw new Exception("Expected final input to be the name of a piece type");}
        };
    }

    private GameData getGameData(int gameID) throws DataAccessException {
        GetGameRequest getGameRequest = new GetGameRequest(gameID);
        String jsonGameData = server.observeGame(getGameRequest, authToken);
        GameData gameData = gson.fromJson(jsonGameData, GameData.class);
        return gameData;
    }

    private final String preloginHelp = SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL> - to create and account \n" +
            "login <USERNAME> <PASSWORD> - to play chess \n" +
            "quit - to quit playing chess \n" +
            "help - to display the possible commands";

    private final String gameplayHelp = "redraw - to redraw the chess board \n" +
            "leave - to stop playing this game \n" +
            "resign - to forfeit the game \n" +
            "move <startingSquare> <finishingSquare> - to move a piece  \n" +
            "highlight <startingSquare> - to highlight the legal moves \n" +
            "help - to display the possible commands";

    private final String observingHelp = "redraw - to redraw the chess board \n" +
            "leave - to stop observing this game \n" +
            "help - to display the possible commands";

}
