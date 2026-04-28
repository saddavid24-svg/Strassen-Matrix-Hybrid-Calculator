package com.example.strassenmatrix;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText sizeInput;
    GridLayout gridA, gridB;
    TextView output;

    EditText[][] ACells, BCells;
    int n;

    private static final int THRESHOLD = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sizeInput = findViewById(R.id.sizeInput);
        gridA = findViewById(R.id.gridA);
        gridB = findViewById(R.id.gridB);
        output = findViewById(R.id.output);

        findViewById(R.id.btnGenerate).setOnClickListener(v -> generate());
        findViewById(R.id.btnCompare).setOnClickListener(v -> runBenchmark());
    }

    // Grids

    private void generate() {
        try {
            n = Integer.parseInt(sizeInput.getText().toString());

            if (n != 2 && n != 4 && n != 8) {
                output.setText("Only 2, 4, 8 allowed");
                return;
            }

            ACells = createGrid(gridA, n);
            BCells = createGrid(gridB, n);

            output.setText("Fill all values");

        } catch (Exception e) {
            output.setText("Invalid input");
        }
    }

    private EditText[][] createGrid(GridLayout grid, int size) {
        grid.removeAllViews();
        grid.setColumnCount(size);

        EditText[][] cells = new EditText[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                EditText e = new EditText(this);
                e.setWidth(120);
                e.setHeight(120);
                e.setGravity(Gravity.CENTER);
                e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                e.setText("");

                grid.addView(e);
                cells[i][j] = e;
            }
        }
        return cells;
    }

    // Benchmark

    private void runBenchmark() {
        try {
            int[][] A = read(ACells);
            int[][] B = read(BCells);

            long s1 = System.nanoTime();
            int[][] normal = normal(A, B);
            long e1 = System.nanoTime();

            long s2 = System.nanoTime();
            int[][] strassen = Strassen.multiplyHybrid(A, B, THRESHOLD);
            long e2 = System.nanoTime();

            double tN = (e1 - s1) / 1_000_000.0;
            double tS = (e2 - s2) / 1_000_000.0;

            String crossover = scalingTest(A, B);

            output.setText(
                    "NORMAL:\n" + format(normal) +
                            "Time: " + tN + " ms\n\n" +

                            "STRASSEN:\n" + format(strassen) +
                            "Time: " + tS + " ms\n\n" +

                            "WINNER: " + winner(tN, tS) + "\n\n" +

                            crossover
            );

        } catch (Exception e) {
            output.setText("Error: " + e.getMessage());
        }
    }

    // Crossover Engine

    private String scalingTest(int[][] A, int[][] B) {

        int[] sizes = {2, 4, 8, 16, 32};

        StringBuilder sb = new StringBuilder();
        sb.append("===== CROSSOVER TEST =====\n\n");

        for (int s : sizes) {

            int[][] a = expand(A, s);
            int[][] b = expand(B, s);

            long sN = System.nanoTime();
            normal(a, b);
            long eN = System.nanoTime();

            long sS = System.nanoTime();
            Strassen.multiplyHybrid(a, b, THRESHOLD);
            long eS = System.nanoTime();

            double tN = (eN - sN) / 1_000_000.0;
            double tS = (eS - sS) / 1_000_000.0;

            sb.append(s + "x" + s + "\n");
            sb.append("Normal: " + tN + " ms\n");
            sb.append("Strassen: " + tS + " ms\n");

            if (tS < tN) {
                sb.append("➡ STRASSEN WINS\n\n");
                sb.append("CROSSOVER ≈ " + s + "x" + s + "\n");
                break;
            } else {
                sb.append("➡ NORMAL WINS\n\n");
            }
        }

        return sb.toString();
    }

    // Matrix Expansion

    private int[][] expand(int[][] base, int size) {
        int[][] m = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                m[i][j] = base[i % base.length][j % base.length];

        return m;
    }

    // Safe Read

    private int[][] read(EditText[][] c) {
        int n = c.length;
        int[][] m = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {

                String t = c[i][j].getText().toString().trim();

                if (t.isEmpty())
                    throw new RuntimeException("Empty cell");

                m[i][j] = Integer.parseInt(t);
            }

        return m;
    }

    // Normal

    private int[][] normal(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];

        return C;
    }

    // Format

    private String format(int[][] m) {
        StringBuilder sb = new StringBuilder();

        for (int[] r : m) {
            sb.append("[ ");
            for (int v : r) sb.append(v).append(" ");
            sb.append("]\n");
        }

        return sb.toString();
    }

    private String winner(double n, double s) {
        return (n < s) ? "NORMAL" : "STRASSEN";
    }
}