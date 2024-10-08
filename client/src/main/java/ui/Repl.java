package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements BoardDisplay{

    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("\uD83D\uDC36 Welcome to Madsen's Chess Project. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        client.eval("quit");
        System.out.println();
    }

    @Override
    public void displayMessage(String message){
        System.out.println(message);
    }

    @Override
    public void displayBlackBoard(ChessGame chessGame, HashSet<ChessPosition> validSquares) {
        System.out.println("\n" + SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"h"+smol+"g" +smol+ "f" +
                smol+ "e" +smol+ "d" +smol+ "c" +smol+ "b" +smol+ "a");//print the top row
        for (int i = 1; i<9; i++){ //i is for row
            StringBuilder row = new StringBuilder();
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            for(int j = 8; j>0; j--){ //j is for column
                row.append(squareToString(new ChessPosition(i,j), chessGame.getBoard(), validSquares));
            }
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            System.out.println(row);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"h"+smol+"g" +smol+ "f" +
                smol+ "e" +smol+ "d" +smol+ "c" +smol+ "b" +smol+ "a");
        System.out.println(RESET);
    }

    @Override
    public void displayWhiteBoard(ChessGame chessGame, HashSet<ChessPosition> validSquares) {
        System.out.println("\n"+SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"a"+smol+"b" +smol+ "c"
                +smol+ "d" +smol+ "e" +smol+ "f" +smol+ "g" +smol+ "h");//print the top row
        for (int i = 8; i>0; i--){ //i is for row
            StringBuilder row = new StringBuilder();
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            String rowCap = String.format(someSmalls + "%s" + someSmalls, i);
            row.append(rowCap);
            for(int j = 1; j<9; j++){ //j is for column
                row.append(squareToString(new ChessPosition(i,j), chessGame.getBoard(), validSquares));
            }
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(rowCap);
            System.out.println(row);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"a"+smol+"b" +smol+ "c"
                +smol+ "d" +smol+ "e" +smol+ "f" +smol+ "g" +smol+ "h");
        System.out.println(RESET);
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_PURPLE);
    }

    String squareToString (ChessPosition chessPosition, ChessBoard chessBoard, HashSet<ChessPosition> validSquares){
        StringBuilder result = new StringBuilder();
        if (validSquares == null){
            setNormalSquareColor(chessPosition, result);
        } else if (validSquares.contains(chessPosition)){
            result.append(SET_BG_COLOR_LIGHT_GREY);
        }else{
            setNormalSquareColor(chessPosition, result);
        }
        ChessPiece potentialPiece = chessBoard.getPiece(chessPosition);
        setSquareContents(potentialPiece, result);
        return result.toString();
    }

    private String pieceToString(ChessPiece chessPiece){
        ChessPiece.PieceType pieceType = chessPiece.getPieceType();
        return switch (pieceType){
            case PAWN : { yield BLACK_PAWN;}
            case KING : { yield BLACK_KING;}
            case QUEEN : { yield BLACK_QUEEN;}
            case ROOK : { yield BLACK_ROOK;}
            case BISHOP : { yield BLACK_BISHOP;}
            case KNIGHT: { yield BLACK_KNIGHT;}
        };
    }

    private void setNormalSquareColor(ChessPosition chessPosition, StringBuilder result){
        if (chessPosition.getRow() % 2 == 1){ // odd row
            if(chessPosition.getColumn() % 2 == 1){ // odd column
                result.append(SET_BG_COLOR_BLACK);//dark background
            } else{ //even column
                result.append(SET_BG_COLOR_DARK_GREY);//light background
            }
        } else{ //even row
            if(chessPosition.getColumn() % 2 == 1){ // odd column
                result.append(SET_BG_COLOR_DARK_GREY);//light background
            } else{//even column
                result.append(SET_BG_COLOR_BLACK);//light background
            }
        }
    }

    private void setSquareContents(ChessPiece potentialPiece, StringBuilder result){
        if (potentialPiece == null){
            result.append(EMPTY);
        } else if(potentialPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            result.append(SET_TEXT_COLOR_PURPLE);
            String pieceString = pieceToString(potentialPiece);
            result.append(pieceString);
        }else{
            result.append(SET_TEXT_COLOR_BLUE);
            String pieceString = pieceToString(potentialPiece);
            result.append(pieceString);
        }
    }

    private final String manySmalls = SMALL + SMALL+ SMALL + SMALL +SMALL + SMALL + SMALL + SMALL + SMALL + SMALL+
            SMALL + SMALL +SMALL + SMALL + SMALL + SMALL+ SMALL +SMALL + SMALL+ SMALL +SMALL + SMALL;
    private final String someSmalls = SMALL+SMALL+SMALL+SMALL+SMALL;
    private final String smol = SMALL + SMALL+ SMALL + SMALL + SMALL+ SMALL + SMALL +SMALL+SMALL + SMALL + SMALL+SMALL
            + SMALL;
    private final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
}
