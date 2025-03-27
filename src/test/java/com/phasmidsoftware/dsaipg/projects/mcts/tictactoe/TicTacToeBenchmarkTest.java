package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import static org.junit.Assert.*;


import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TicTacToeBenchmarkTest {

    /**
     * This test invokes the main method and checks that the CSV file is created,
     * is not empty, and that its first line matches the expected CSV header.
     */
    @Test(timeout = 10000)
    public void testMainCreatesCSVFile() throws Exception {
        String filePath = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/CSVResult/benchmark_doubling.csv";
        File file = new File(filePath);
        // Delete the file if it exists from a previous run.
        if (file.exists()) {
            assertTrue("Unable to delete existing CSV file", file.delete());
        }

        // Redirect System.out to avoid cluttering test output.
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Run the benchmark.
        TicTacToeBenchmark.main(new String[]{});

        // Restore System.out.
        System.setOut(originalOut);

        // Check that the CSV file now exists.
        assertTrue("CSV file should be created", file.exists());

        // Read and verify the contents of the CSV file.
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        assertFalse("CSV file should not be empty", lines.isEmpty());
        assertEquals("CSV header should be present",
                "Iterations,AvgTotalTime(ms),WinsX,AvgTimeX(ms),WinsO,AvgTimeO(ms),Draws,AvgTimeDraw(ms)",
                lines.get(0));
    }

    /**
     * This test captures the standard output while running the main method and
     * checks that summary lines (which start with "Iterations:") are printed.
     * Since the loop doubles the iterations from 100 to 1600, we expect 5 summary lines.
     */
    @Test(timeout = 10000)
    public void testMainPrintsSummary() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Run the benchmark.
        TicTacToeBenchmark.main(new String[]{});

        // Restore the original System.out.
        System.setOut(originalOut);

        String output = outContent.toString();
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new StringReader(output))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Iterations:")) {
                    count++;
                }
            }
        }
        // Expect 5 iterations printed (for iterations: 100, 200, 400, 800, 1600).
        assertEquals("There should be 5 summary lines printed", 5, count);
    }
}