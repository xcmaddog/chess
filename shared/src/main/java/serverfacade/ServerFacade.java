package serverfacade;

import com.google.gson.Gson;
import dataaccess.DataAccessException; //I may want to move the data access exception to shared
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

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
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status){
        return status == 200;
    }
}
