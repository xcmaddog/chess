package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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
    public void displayBlackBoard(ChessGame chessGame) {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"1"+smol+"2" +smol+ "3" +smol+ "4"
                +smol+ "5" +smol+ "6" +smol+ "7" +smol+ "8");//print the top row
        for (int i = 1; i<9; i++){ //i is for row
            StringBuilder row = new StringBuilder();
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            for(int j = 8; j>0; j--){ //j is for column
                row.append(squareToString(new ChessPosition(i,j), chessGame.getBoard()));
            }
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            System.out.println(row);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls + "1"+smol+"2" +smol+ "3"
                +smol+ "4" +smol+ "5" +smol+ "6" +smol+ "7" +smol+ "8");
        //print each middle row in a loop
    }

    @Override
    public void displayWhiteBoard(ChessGame chessGame) {
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls +"1"+smol+"2" +smol+ "3"
                +smol+ "4" +smol+ "5" +smol+ "6" +smol+ "7" +smol+ "8");//print the top row
        for (int i = 8; i>0; i--){ //i is for row
            StringBuilder row = new StringBuilder();
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            for(int j = 1; j<9; j++){ //j is for column
                row.append(squareToString(new ChessPosition(i,j), chessGame.getBoard()));
            }
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE);
            row.append(String.format(someSmalls+ "%s"+someSmalls,i));
            System.out.println(row);
        }
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_PURPLE + manySmalls + "1"+smol+"2" +smol+ "3" +smol+ "4" +smol+ "5" +smol+ "6" +smol+ "7" +smol+ "8");
        //print each middle row in a loop
        //print the bottom row (identical to top)
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    String squareToString (ChessPosition chessPosition, ChessBoard chessBoard){
        StringBuilder result = new StringBuilder();
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
        ChessPiece potentialPiece = chessBoard.getPiece(chessPosition);
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

    private final String manySmalls = SMALL + SMALL+ SMALL + SMALL +SMALL + SMALL + SMALL + SMALL + SMALL + SMALL+
            SMALL + SMALL +SMALL + SMALL + SMALL + SMALL+ SMALL +SMALL + SMALL;
    private final String someSmalls = SMALL+SMALL+SMALL+SMALL+SMALL;
    private final String smol = SMALL + SMALL+ SMALL + SMALL +SMALL+SMALL + SMALL + SMALL+SMALL + SMALL + SMALL ;
}
