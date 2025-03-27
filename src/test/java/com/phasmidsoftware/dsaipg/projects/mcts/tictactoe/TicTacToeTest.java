package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.*;

public class TicTacToeTest {

    /**
     * Verifies that runGame() returns a terminal state with a winner.
     */
    @Test
    public void runGame() {
        long seed = 0L;
        TicTacToe game = new TicTacToe(seed); // deterministic game
        State<TicTacToe> state = game.runGame();
        Optional<Integer> winner = state.winner();
        if (winner.isPresent()) {
            // Depending on the implementation and seed, the winner might be X.
            assertEquals("Winner should be player X", Integer.valueOf(TicTacToe.X), winner.get());
        } else {
            fail("Expected a winner but found none");
        }
    }

    /**
     * Tests the starting position rendering.
     */
    @Test
    public void testStartingPosition() {
        Position starting = TicTacToe.startingPosition();
        String expected = ". . .\n. . .\n. . .";
        assertEquals("Starting board should match expected layout", expected, starting.render());
    }

    /**
     * Verifies that the opening player is X.
     */
    @Test
    public void testOpener() {
        TicTacToe game = new TicTacToe(0L);
        assertEquals("Opener should be player X", TicTacToe.X, game.opener());
    }

    /**
     * Verifies that a new game state is not terminal and that the board matches the starting position.
     */
    @Test
    public void testStart() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        assertNotNull("Starting state should not be null", state);
        assertFalse("New game state should not be terminal", state.isTerminal());
        Position expected = TicTacToe.startingPosition();
        Position actual = ((TicTacToe.TicTacToeState) state).position();
        assertEquals("The board of the starting state should match the starting position", expected, actual);
    }

    /**
     * Tests that in the starting state, the player to move is X.
     */
    @Test
    public void testStatePlayerStarting() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        assertEquals("Starting state player should be X", TicTacToe.X, state.player());
    }

    /**
     * Tests that there are exactly 9 moves available in the starting state and that each move has valid coordinates.
     */
    @Test
    public void testMovesStartingState() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        Collection<Move<TicTacToe>> moves = state.moves(TicTacToe.X);
        assertEquals("There should be 9 moves in the starting state", 9, moves.size());
        for (Move<TicTacToe> move : moves) {
            int[] coords = ((TicTacToe.TicTacToeMove) move).move();
            assertEquals("Each move should have 2 coordinates", 2, coords.length);
            assertTrue("Row coordinate should be between 0 and 2", coords[0] >= 0 && coords[0] < 3);
            assertTrue("Column coordinate should be between 0 and 2", coords[1] >= 0 && coords[1] < 3);
        }
    }

    /**
     * Verifies that calling moves() with the same player as the last move throws an exception.
     */
    @Test(expected = RuntimeException.class)
    public void testMovesConsecutiveException() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        // Using TicTacToe.blank (i.e. -1) to simulate a consecutive move scenario.
        state.moves(TicTacToe.blank);
    }

    /**
     * Tests that applying a move correctly produces a new state with the move applied.
     */
    @Test
    public void testNextMove() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        Move<TicTacToe> move = state.moves(TicTacToe.X).iterator().next();
        State<TicTacToe> newState = state.next(move);
        int[] coords = ((TicTacToe.TicTacToeMove) move).move();
        Position expected = TicTacToe.startingPosition().move(TicTacToe.X, coords[0], coords[1]);
        Position actual = ((TicTacToe.TicTacToeState) newState).position();
        assertEquals("New state position should reflect the move", expected, actual);
    }

    /**
     * Verifies that the starting state is not terminal.
     */
    @Test
    public void testIsTerminalStarting() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        assertFalse("Starting state should not be terminal", state.isTerminal());
    }

    /**
     * Tests that a full board is considered terminal.
     */
    @Test
    public void testIsTerminalFullBoard() {
        // Create a full board position that is terminal.
        String fullGrid = "X X 0\nX O 0\nX X 0";
        Position fullPos = Position.parsePosition(fullGrid, TicTacToe.X);
        TicTacToe game = new TicTacToe(0L);
        TicTacToe.TicTacToeState fullState = game.new TicTacToeState(fullPos);
        assertTrue("Full board should be terminal", fullState.isTerminal());
    }

    /**
     * Tests that the winner is correctly identified in a winning board.
     */
    @Test
    public void testWinnerState() {
        // Create a winning board for X.
        String winGrid = "X . 0\nX O .\nX . 0";
        Position winPos = Position.parsePosition(winGrid, TicTacToe.X);
        TicTacToe game = new TicTacToe(0L);
        TicTacToe.TicTacToeState winState = game.new TicTacToeState(winPos);
        Optional<Integer> winner = winState.winner();
        assertTrue("There should be a winner", winner.isPresent());
        assertEquals("Winner should be X", Integer.valueOf(TicTacToe.X), winner.get());
    }

    /**
     * Verifies that TicTacToeMove correctly stores the player and move coordinates.
     */
    @Test
    public void testTicTacToeMove() {
        TicTacToe.TicTacToeMove move = new TicTacToe.TicTacToeMove(TicTacToe.X, 1, 2);
        assertEquals("Move's player should be X", TicTacToe.X, move.player());
        assertArrayEquals("Move coordinates should be [1, 2]", new int[]{1, 2}, move.move());
    }

    /**
     * Verifies that the game() method in TicTacToeState returns the enclosing TicTacToe instance.
     */
    @Test
    public void testGameMethodInState() {
        TicTacToe game = new TicTacToe(0L);
        TicTacToe.TicTacToeState state = game.new TicTacToeState();
        assertEquals("State's game should be the TicTacToe instance", game, state.game());
    }

    /**
     * Tests that the state's toString() returns the expected string representation.
     */
    @Test
    public void testStateToString() {
        TicTacToe game = new TicTacToe(0L);
        State<TicTacToe> state = game.start();
        String expected = "TicTacToe\n. . .\n. . .\n. . .\n";
        assertEquals("State toString() should match expected output", expected, state.toString());
    }
}