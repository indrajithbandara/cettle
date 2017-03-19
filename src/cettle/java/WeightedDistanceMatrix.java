package cettle.java;

import java.util.*;

public class WeightedDistanceMatrix extends DistanceMatrix {

    protected Double absentValue;
    List<List<Double>> a;

    public WeightedDistanceMatrix(Integer cardV, Boolean directed, Double absent) {
        super(cardV, directed);
        super.a = null;
        absentValue = absent;
        a = new ArrayList<List<Double>>(cardV);
//        for(int i = 0; i < cardV; i++) {
//          a.add(new ArrayList<Double>());
//        }
        for (int i = 0; i < cardV; i++) {
            a.add(i, new ArrayList<Double>());
            for (int j = 0; j < cardV; j++) {
                a.get(i).add(absent);
            }
        }
    }

    public void addEdge(Vertex u, Vertex v) { throw new UnsupportedOperationException(); }
    public void addEdge(Integer u, Integer v) { throw new UnsupportedOperationException(); }

    public void addEdge(Vertex u, Vertex v, Double weight) {
        int uIndex = u.getIndex();
        int vIndex = v.getIndex();
        a.get(uIndex).set(vIndex, weight);
        if (!directed)
            a.get(vIndex).set(uIndex, weight);
    }

    public void addEdge(Integer u, Integer v, double weight) {
        a.get(u).set(v, weight);
        if (!directed)
            a.get(v).set(u, weight);
    }

    public Iterator edgeIterator(Vertex u) { return new EdgeIterator(u.getIndex()); }
    public Iterator edgeIterator(Integer u) { return new EdgeIterator(u); }

    public WeightedEdgeIterator weightedEdgeIterator(Vertex u) { return weightedEdgeIterator(u.getIndex()); }

    public WeightedEdgeIterator weightedEdgeIterator(Integer u) { return new EdgeIterator(u); }

    public class EdgeIterator extends DistanceMatrix.EdgeIterator implements WeightedEdgeIterator {

        public EdgeIterator(Integer v) { super(v); }

        public boolean hasNext() {
            int v = current + 1;
            while (v < a.get(u).size() && a.get(u).get(v) == absentValue)
                v++;
            return v < a.get(u).size();
        }

        public Object next() {
            current++;
            while (a.get(u).get(current) == absentValue)
                current++;
            return vertices[current];
        }

        public Double getWeight() { return a.get(u).get(current); }
        public void setWeight(double weight) { a.get(u).set(current, weight); }
    }

    public Boolean edgeExists(Vertex u, Vertex v) { return edgeExists(u.getIndex(), v.getIndex()); }
    public Boolean edgeExists(Integer u, Integer v) { return !a.get(u).get(v).equals(absentValue); }
    public Double getWeight(Vertex u, Vertex v) { return getWeight(u.getIndex(), v.getIndex()); }
    public Double getWeight(Integer u, Integer v) { return a.get(u).get(v); }

    private static Set<Vertex> uniqueVertices(ArrayList<Vertex> from, ArrayList<Vertex> to){
        Set<Vertex> uv = new HashSet<Vertex>();
        uv.addAll(from);
        uv.addAll(to);
        return uv;
    }

    private List<Vertex> addVertices(Set<String> vertices) {
        Iterator iter = vertices.iterator();
        Vertex v = new Vertex();
        List<Vertex> vs = new ArrayList<>(vertices.size());
        while (iter.hasNext()) {
            v.setName((String) iter.next());
            vs.add(v);
        }
        return vs;
    }

    public static List<List<Double>> build (ArrayList<Vertex> from, ArrayList<Vertex> to, ArrayList<Double> weight, Boolean isDir, Double nonEdge, int numVerts) {
        int numEdges = (from.size() + to.size()) / 2;
        Set<Vertex> uv = uniqueVertices(from, to);
        WeightedDistanceMatrix wdm = new WeightedDistanceMatrix(numVerts, isDir, nonEdge);
        for (Vertex v : uv) {
            wdm.addVertex(v);
        }
        for(int index=0; index<numEdges; index++) {
            wdm.addEdge(from.get(index), to.get(index), weight.get(index));
        }
        return wdm.a;

        // A -> B A -> C B -> D
    }

    public String toString() {
        String result = "";
        Iterator vertexIter = vertexIterator();
        while (vertexIter.hasNext()) {
            Vertex u = (Vertex) vertexIter.next();
            result += u + ":\n";
            WeightedEdgeIterator edgeIter = weightedEdgeIterator(u);
            while (edgeIter.hasNext()) {
                Vertex v = (Vertex) edgeIter.next();
                double w = edgeIter.getWeight();
                result += "    " + v + ", weight = " + w + "\n";
            }
        }
        return result;
    }
}


