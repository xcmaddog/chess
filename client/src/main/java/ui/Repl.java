package ui;

import chess.ChessGame;

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
        printPrompt();
    }

    @Override
    public void displayWhiteBoard(ChessGame chessGame) {
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
