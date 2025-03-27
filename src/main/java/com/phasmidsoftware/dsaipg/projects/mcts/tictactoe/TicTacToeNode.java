/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class TicTacToeNode implements Node<TicTacToe> {
    private final Random random = new Random();
    private Node<TicTacToe> parent;
    private double wins;
    private int playouts;

    /**
     * @return true if this node is a leaf node (in which case no further exploration is possible).
     */
    public boolean isLeaf() {
        return state().isTerminal();
    }

    /**
     * @return the State of the Game G that this Node represents.
     */
    public State<TicTacToe> state() {
        return state;
    }

    /**
     * Method to determine if the player who plays to this node is the opening player (by analogy with chess).
     * For this method, we assume that X goes first so is "white."
     * NOTE: this assumes a two-player game.
     *
     * @return true if this node represents a "white" move; false for "black."
     */
    public boolean white() {
        return state.player() == state.game().opener();
    }

    /**
     * @return the children of this Node.
     */
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    /**
     * Method to add a child to this Node.
     *
     * @param state the State for the new chile.
     */
    public void addChild(State<TicTacToe> state) {
        if(state == null) {
            throw new IllegalArgumentException("empty state added");
        }
        TicTacToeNode child = new TicTacToeNode(state);
        child.setParent(this);  // Set the parent of the child
        children.add(child);
    }

    /**
     * This method sets the number of wins and playouts according to the children states.
     */
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<TicTacToe> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }

    /**
     * @return the score for this Node and its descendents a win is worth 2 points, a draw is worth 1 point.
     */
    public double wins() {
        return wins;
    }

    public void setWins(double wins) {
        this.wins = wins;
    }

    /**
     * @return the number of playouts evaluated (including this node). A leaf node will have a playouts value of 1.
     */
    public int playouts() {
        return playouts;
    }

    public void setPlayouts(int playout) {
        this.playouts = playout;
    }

    // Implement parent methods
    public Node<TicTacToe> getParent() {
        return parent;
    }

    public void setParent(Node<TicTacToe> parent) {
        this.parent = parent;
    }

    public TicTacToeNode(State<TicTacToe> state) {
        this.state = state;
        this.children = new ArrayList<>();
        this.parent = null;
        initializeNodeData();
    }

private void initializeNodeData() {
    if (isLeaf()) {
        // For a terminal node:
        this.playouts = 1;
        // If there is a winner, assign 2 points; if it's a draw, assign 1 point.
        Optional<Integer> winner = state.winner();
        if (winner.isPresent()) {
            this.wins = 2.0;
        } else {
            this.wins = 1.0;
        }
    } else {
        this.playouts = 0;
        this.wins = 0.0;
    }
}

    private final State<TicTacToe> state;
    private final ArrayList<Node<TicTacToe>> children;
}