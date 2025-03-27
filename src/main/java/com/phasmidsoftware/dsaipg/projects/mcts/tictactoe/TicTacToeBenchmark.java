package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class TicTacToeBenchmark {

    public static void main(String[] args) {
        // Number of games to run for each iteration setting
        int gamesPerSetting = 50;
        // MCTS iterations range: starting from 100 up to 1600 (doubling each time)
        int startIterations = 100;
        int maxIterations = 1600;
        String filePath = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/CSVResult/benchmark_doubling.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // CSV header for per-setting results
            writer.write("Iterations,AvgTotalTime(ms),WinsX,AvgTimeX(ms),WinsO,AvgTimeO(ms),Draws,AvgTimeDraw(ms)");
            writer.newLine();

            for (int iterations = startIterations; iterations <= maxIterations; iterations *= 2) {
                long totalTime = 0;
                int winsX = 0, winsO = 0, draws = 0;
                long totalTimeX = 0, totalTimeO = 0, totalTimeDraw = 0;
                int gamesRun = 0;

                for (int game = 0; game < gamesPerSetting; game++) {
                    long startTime = System.currentTimeMillis();
                    // Create a new game instance with a fixed seed for reproducibility.
                    TicTacToe gameInstance = new TicTacToe(0L);
                    State<TicTacToe> finalState = gameInstance.runGame(iterations);
                    long elapsed = System.currentTimeMillis() - startTime;
                    totalTime += elapsed;
                    gamesRun++;

                    // Outcome determination:
                    Optional<Integer> maybeWinner = finalState.winner();
                    if (maybeWinner.isPresent()) {
                        int winner = maybeWinner.get();
                        if (winner == TicTacToe.X) {
                            winsX++;
                            totalTimeX += elapsed;
                        } else if (winner == TicTacToe.O) {
                            winsO++;
                            totalTimeO += elapsed;
                        } else {
                            draws++;
                            totalTimeDraw += elapsed;
                        }
                    } else {
                        // No winner means a draw.
                        draws++;
                        totalTimeDraw += elapsed;
                    }
                }

                long avgTotalTime = totalTime / gamesRun;
                double avgTimeX = winsX > 0 ? (double) totalTimeX / winsX : 0;
                double avgTimeO = winsO > 0 ? (double) totalTimeO / winsO : 0;
                double avgTimeDraw = draws > 0 ? (double) totalTimeDraw / draws : 0;

                // CSV output row
                String output = iterations + "," + avgTotalTime + "," +
                        winsX + "," + avgTimeX + "," +
                        winsO + "," + avgTimeO + "," +
                        draws + "," + avgTimeDraw;
                writer.write(output);
                writer.newLine();

                // Also print a summary line to the console
                System.out.println("Iterations: " + iterations +
                        " | Avg Total Time: " + avgTotalTime + " ms | WinsX: " + winsX + " (Avg: " + avgTimeX + " ms)" +
                        " | WinsO: " + winsO + " (Avg: " + avgTimeO + " ms)" +
                        " | Draws: " + draws + " (Avg: " + avgTimeDraw + " ms)");
            }
        } catch (IOException e) {
            System.err.println("Error writing benchmark file: " + e.getMessage());
        }
    }
}