package cettle.java;

import java.util.Arrays;

/**
 * Created by adebesing on 19/03/2017.
 */
public class Core {

    public static void main(String[] args) {

        WeightedDistanceMatrix wamg = new WeightedDistanceMatrix(4, false, 0.0);

        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");


        wamg.addVertex(a);
        wamg.addVertex(b);
        wamg.addVertex(c);
        wamg.addVertex(d);

        wamg.addEdge(a, b, 2.2);
        wamg.addEdge(c, b, 3.3);
        wamg.addEdge(c, a, 2.8);
        wamg.addEdge(a, d, 8.9);


       System.out.println(wamg.a);
        //  A     B    C      D
        //[[0.0, 22.0, 11.0, 9.0],
        // [22.0, 0.0, 55.0, 0.0],
        // [11.0, 55.0, 0.0, 0.0],
        // [9.0, 0.0, 0.0, 0.0]]

//          a     b    c    d
//        [[0.0, 2.2, 2.8, 8.9],
//        [2.2, 0.0, 3.3, 0.0],
//        [2.8, 3.3, 0.0, 0.0],
//        [8.9, 0.0, 0.0, 0.0]]
    }
}
