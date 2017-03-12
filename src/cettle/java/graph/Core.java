package cettle.java.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adebesing on 12/03/2017.
 */
public class Core {

    public static double[][] weightedDistanceMatrix (String[] verts, boolean isDirected, double nonEdge) {
        List<Vertex> vs = new ArrayList<Vertex>(verts.length);
        WeightedDistanceMatrix wdm = new WeightedDistanceMatrix(verts.length, isDirected, nonEdge);
        for(int i = 0; i < vs.size(); i++) {
            vs.get(i).setName(verts[i]);
            wdm.addVertex(vs.get(i));
            //wdm.addEdge();
        }
        return null;
    }
}
