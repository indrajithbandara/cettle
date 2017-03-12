package cettle.java.graph;

import java.util.*;

/**
 * Created by adebesing on 12/03/2017.
 */
public class Core {

//    public static double[][] weightedDistanceMatrix (String[] verts, boolean isDirected, double nonEdge) {
//        List<Vertex> vs = new ArrayList<Vertex>(verts.length);
//        WeightedDistanceMatrix wdm = new WeightedDistanceMatrix(verts.length, isDirected, nonEdge);
//        for(int i = 0; i < vs.size(); i++) {
//            vs.get(i).setName(verts[i]);
//            wdm.addVertex(vs.get(i));
//            //wdm.addEdge();
//        }
//        return null;
//    }

    private static Set<String> uniqueVertices(ArrayList<String> from, ArrayList<String> to){
        Set<String> uv = new HashSet<>();
        uv.addAll(from);
        uv.addAll(to);
        return uv;
    }

    private static List<Vertex> addVertices(Set<String> vertices) {
        Iterator iter = vertices.iterator();
        Vertex v = new Vertex();
        List<Vertex> vs = new ArrayList<>(vertices.size());
        while (iter.hasNext()) {
            v.setName((String) iter.next());
            vs.add(v);
        }
        return vs;
    }

    public static double[][] weightedDistanceMatrix (ArrayList<String> from, ArrayList<String> to, ArrayList<Double> weight, boolean isDirected, double nonEdge) {
        //Object[][] ftw = new Object[][]{from, to, weight};

        Set<String> uv = uniqueVertices(from, to);
        int uvSize = uv.size();

        List<Vertex> vs = new ArrayList<>(uvSize);

        WeightedDistanceMatrix wdm = new WeightedDistanceMatrix(uvSize, isDirected, nonEdge);
        for(int i = 0; i < vs.size(); i++) {
            vs.get(i).setName(uv.iterator().next());
        }
        for(int i = 0; i < from.size(); i++) {
           // wdm.addEdge(from.get(i), to.get(i), weight.get(i));
        }
        return null;
    }
}
