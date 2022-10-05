package cmpt;

import com.microsoft.z3.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class GraphColoring {

    public static void main(String[] args) throws FileNotFoundException {
        File input = new File("input.txt");
        Scanner fileReader = new Scanner(input);

        int N = 0; // num of vertices
        int M = 0; // num of colors
        int[] vertices = new int[0];
        int[] colors = new int[0];
        List<int[]> edges = new ArrayList<>();

        // first line (N and M)
        if (fileReader.hasNextLine()) {
            String[] data = fileReader.nextLine().split("\\s+", 2);
            N = Integer.parseInt(data[0]);
            M = Integer.parseInt(data[1]);

            // create array with [1, 2, ... , N]
            vertices = IntStream.rangeClosed(1, N).toArray();
            colors = IntStream.rangeClosed(1, M).toArray();
        }

        // remaining lines (edges)
        while (fileReader.hasNextLine()) {
            String[] data = fileReader.nextLine().split("\\s+", 2);
            int v1 = Integer.parseInt(data[0]);
            int v2 = Integer.parseInt(data[1]);
            edges.add(new int[]{v1, v2});
        }

        Context context = new Context();
        Solver solver = context.mkSolver();
        // create z3 bools for all possible vertex and color pairings
        BoolExpr[][] p = new BoolExpr[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                p[i][j] = context.mkBoolConst("p_" + vertices[i] + "_" + colors[j]);
            }
        }

        // create negations of adjacent vertices with same color
        // not(v1 and v2), where v1 and v2 are same color
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < M; j++) {
//                BoolExpr notv1 = context.mkNot(props[edges.get(i)[0]][colors[j]]);
//                BoolExpr notv2 = context.mkNot(props[edges.get(i)[1]][colors[j]]);
//                solver.add(context.mkOr(notv1, notv2));
//                System.out.println("adding p[" + (edges.get(i)[0] - 1) + "][" + (colors[j] - 1) + "]");
                BoolExpr v1v2 = context.mkAnd(
                        p[edges.get(i)[0] - 1][colors[j] - 1],
                        p[edges.get(i)[1] - 1][colors[j] - 1]
                );
                solver.add(context.mkNot(v1v2));
            }
        }

        // make sure each vertex has min 1 color
        // or (p_1_1 p_1_2 ... p_1_M)
        // or ( ... )
        // or (p_N_1 p_N_2 ... p_N_M)
//        for (int i = 0; i < N; i++) {
//            solver.add(context.mkOr(p[i]));
////            System.out.println(p[i]);
//        }

        // make sure each vertex has max 1 color
        // or (not(p_1_1) not(p_1_2))
        // or (...)
        // or (not(p_1_1) not(p_1_M))
        // ...
        // or (not(P_N-1_M) not(p_N_M))
        for (int vertex = 0; vertex < N; vertex++) {
            // min 1 color
            solver.add(context.mkOr(p[vertex]));
            for (int i = 0; i < M; i++) {
                for (int j = i + 1; j < M; j++) {
                    BoolExpr v1 = context.mkNot(p[vertex][i]);
                    BoolExpr v2 = context.mkNot(p[vertex][j]);
                    solver.add(context.mkOr(v1, v2));
//                    System.out.println(context.mkOr(v1, v2));
                }
            }
        }

        Status status = solver.check();
//        System.out.println(status);

        // write to file
        PrintWriter writer = new PrintWriter("output.txt");
        if (status.toString().equals("SATISFIABLE")) {
            Model model = solver.getModel();
//        System.out.println(model);
            for (FuncDecl constant : model.getConstDecls()) {
                if (model.getConstInterp(constant).toString().equals("true")) {
//                System.out.println(constant.getName());
                    String[] vertex = constant.getName().toString().split("_", 3);
                    writer.println(vertex[1] + "\t" + vertex[2]);
                }
            }
        } else {
            writer.println("No Solution");
        }
        writer.close();
    }
}
