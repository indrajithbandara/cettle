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

        wamg.addEdge(a, b, 22);
        wamg.addEdge(c, b, 55);
        wamg.addEdge(c, a, 11);
        wamg.addEdge(a, d, 9);


        System.out.println(wamg.a);
    }
}
