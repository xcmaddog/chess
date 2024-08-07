package serverfacade;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.PositionPieceMapAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dataaccess.DataAccessException; //I may want to move the data access exception to shared
import model.GameInfo;
import request.*;
import result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade (String url) {
        serverUrl = url;
    }

    //methods that make calls to the server

    public String login(LoginRequest loginRequest) throws DataAccessException {
        String method = "POST";
        String path = "/session";
        LoginResult loginResult = this.makeRequest(method, path, loginRequest, LoginResult.class, null);
        return loginResult.authToken();
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String method = "DELETE";
        String path = "/session";
        this.makeRequest(method, path, logoutRequest, null, logoutRequest.authToken());
    }

    public String register(RegisterRequest registerRequest) throws DataAccessException {
        String method = "POST";
        String path = "/user";
        RegisterResult registerResult = this.makeRequest(method, path, registerRequest, RegisterResult.class, null);
        String authToken = registerResult.authToken();
        return authToken;
    }

    public String createGame (CreateGameRequest createGameRequest, String authToken) throws DataAccessException {
        String method = "POST";
        String path = "/game";
        CreateGameResult createGameResult = this.makeRequest(method, path, createGameRequest, CreateGameResult.class,
                authToken);
        String gameID = String.valueOf(createGameResult.gameID());
        String result = String.format("Successfully created game: %s\n Its game ID is: %s",
                createGameRequest.gameName(), gameID);
        return result;
    }

    public String listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String method = "GET";
        String path = "/game";
        ListGamesResult listGamesResult = this.makeRequest(method, path, null, ListGamesResult.class,
                listGamesRequest.authToken());
        StringBuilder listedGames = new StringBuilder("GameID|WhiteUsername|BlackUsername|GameName\n");
        for(GameInfo game : listGamesResult.getAllGameInfo()){
            listedGames.append(gameInfoToString(game));
            listedGames.append("\n");
        }
        listedGames.deleteCharAt(listedGames.length()-1);
        //listedGames.deleteCharAt(listedGames.length()-1);
        return listedGames.toString();
    }

    public String joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        String method = "PUT";
        String path = "/game";
        JoinGameResult joinGameResult = this.makeRequest(method, path, joinGameRequest,
                JoinGameResult.class, authToken);
        String result = String.format("You successfully joined game %s as the %s player",
                joinGameRequest.gameID(),joinGameRequest.playerColor().toString());
        return result;
    }

    public String observeGame(GetGameRequest getGameRequest, String authToken) throws DataAccessException{
        String method = "GET";
        String path = String.format("/observe?gameId=%s", getGameRequest.gameID());
        GetGameResult getGameResult = this.makeRequest(method, path, null, GetGameResult.class, authToken);
        String result = new Gson().toJson(getGameResult.gameData());
        return result;
    }

    public void clear() throws DataAccessException {
        String method = "DELETE";
        String path = "/db";
        this.makeRequest(method, path, null,null, null);
    }

    //helper methods

    private <T> T makeRequest (String method, String path, Object request, Class<T> responseClass, String authToken)
            throws DataAccessException {
        try{
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null){
                writeHeader(authToken, http);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        }
        catch (Exception e) {
                throw new DataAccessException(e.getMessage());
        }
    }

    private static void writeHeader(String authToken, HttpURLConnection http){
        http.addRequestProperty("authorization", authToken);
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException{
        if(request != null){
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try(OutputStream reqBody = http.getOutputStream()){
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException{
        T response = null;
        if (http.getContentLength() < 0){
            try(InputStream respBody = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null){
                    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HashMap<ChessPosition,
                                    ChessPiece>>(){}.getType(), new PositionPieceMapAdapter()).create();
                    response = gson.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private String gameInfoToString(GameInfo gameInfo){
        String gameID = String.valueOf(gameInfo.getGameID());
        String whiteUsername = gameInfo.getWhiteUsername();
        String blackUsername = gameInfo.getBlackUsername();
        String gameName = gameInfo.getGameName();
        StringBuilder resultBuilder = new StringBuilder();

        if(gameID.length() > 6){
            resultBuilder.append(gameID, 0, 3).append("...");
        }else{
            resultBuilder.append(" ".repeat((6 - gameID.length())));
            resultBuilder.append(gameID);
        }
        resultBuilder.append("|");
        if(whiteUsername==null){
            resultBuilder.append("             ");
        }else if(whiteUsername.length() >= 13){
            resultBuilder.append(gameID, 0, 10).append("...");
        }else{
            resultBuilder.append(" ".repeat((13 - whiteUsername.length())));
            resultBuilder.append(whiteUsername);
        }
        resultBuilder.append("|");
        if(blackUsername == null){
            resultBuilder.append("             ");
        } else if(blackUsername.length() >= 13){
            resultBuilder.append(blackUsername, 0, 10).append("...");
        }else{
            resultBuilder.append(" ".repeat((13 - blackUsername.length())));
            resultBuilder.append(blackUsername);
        }
        resultBuilder.append("|");
        if(gameName.length() > 8){
            resultBuilder.append(gameName, 0, 5).append("...");
        }else{
            resultBuilder.append(" ".repeat((8 - gameName.length())));
            resultBuilder.append(gameName);
        }
        return resultBuilder.toString();
    }

    private boolean isSuccessful(int status){
        return status == 200;
    }
}
