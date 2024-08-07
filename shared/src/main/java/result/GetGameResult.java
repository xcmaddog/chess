package result;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.GameData;

import java.io.IOException;

public record GetGameResult(GameData gameData) {
}
