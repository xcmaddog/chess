package chess;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PositionPieceMapAdapter extends TypeAdapter<HashMap<ChessPosition, ChessPiece>> {

    @Override
    public void write(JsonWriter out, HashMap<ChessPosition, ChessPiece> map) throws IOException {

    }

    @Override
    public HashMap<ChessPosition, ChessPiece> read(JsonReader in) throws IOException {
        HashMap<ChessPosition, ChessPiece> map = new HashMap<>();
        in.beginObject();

        while (in.hasNext()) {
            String key = in.nextName();  // The key is a string like "[2,1]"
            ChessPosition position = parsePosition(key);
            ChessPiece piece = new Gson().fromJson(in, ChessPiece.class);
            map.put(position, piece);
        }

        in.endObject();
        return map;
    }

    private ChessPosition parsePosition(String key) {
        key = key.replace("[", "").replace("]", "");
        String[] parts = key.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        return new ChessPosition(row, col);
    }
}

