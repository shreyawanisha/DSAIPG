package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicTacToeNodeTest {

    @Test
    public void winsAndPlayouts() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState(Position.parsePosition("X . 0\nX O .\nX . 0", TicTacToe.X));
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.isLeaf());
        assertEquals(2.0, node.wins(), 0.001); // Use delta for double comparison
        assertEquals(1, node.playouts());
    }

    @Test
    public void state() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(state, node.state());
    }

    @Test
    public void white() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.white());
    }

    @Test
    public void children() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(0, node.children().size());
        // no tests yet
    }

    @Test
    public void addChild() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        node.addChild(state);
        assertEquals(1, node.children().size());
        // no tests yet
    }

    @Test
    public void backPropagate() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        node.backPropagate(); // After backpropagation, no children → wins=0.0
        assertEquals(0.0, node.wins(), 0.001); // Use delta
        assertEquals(0, node.playouts());
    }
}