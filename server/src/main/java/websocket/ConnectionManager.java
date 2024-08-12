package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, HashMap<String, Connection>> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        if (connections.containsKey(gameID)){
            HashMap<String, Connection> existingConnections= connections.get(gameID);
            existingConnections.put(username, connection);
        } else {
            HashMap<String, Connection> connectionMap = new HashMap<>();
            connectionMap.put(username, connection);
            connections.put(gameID, connectionMap);
        }

    }

    public void remove(int gameID, String username) {
        HashMap<String, Connection> existingConnections= connections.get(gameID);
        existingConnections.remove(username);
        if (existingConnections.isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(int gameID, String excludePlayerName, ServerMessage message) throws IOException {
        HashMap<String, Connection> relevantConnections = connections.get(gameID);
        var removeList = new ArrayList<Connection>();
        for (var c : relevantConnections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerName.equals(excludePlayerName)) {
                    String json = gson.toJson(message);
                    //c.send(notification.toString()); ----this is the old code. I'm keeping it here in case it proves useful for the different classes
                    c.send(json);
                }
            } else {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            relevantConnections.remove(c.playerName);
        }
    }

    public void sendBoard(int gameID, String username, LoadGameMessage gameMessage) throws IOException {
        HashMap<String, Connection> relevantConnections = connections.get(gameID);
        var removeList = new ArrayList<Connection>();
        for (var c : relevantConnections.values()) {
            if (c.session.isOpen()) {
                if (c.playerName.equals(username)) {
                    String json = gson.toJson(gameMessage);
                    //c.send(notification.toString()); ----this is the old code. I'm keeping it here in case it proves useful for the different classes
                    c.send(json);
                }
            } else {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            relevantConnections.remove(c.playerName);
        }
    }
}
