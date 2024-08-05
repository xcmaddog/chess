package request;

import chess.ChessGame;

/**
 * @param playerColor private final String authToken;
 */
public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
}
