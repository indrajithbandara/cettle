package cettle.java;

import java.util.Iterator;

public class DistanceMatrix {

    protected boolean directed;
    protected int lastAdded;
    protected int e;
    protected Vertex[] vertices;
    protected boolean[][] a;

    public DistanceMatrix(int cardV, boolean directed) {
        this.directed = directed;
        lastAdded = -1;
        vertices = new Vertex[cardV];
        a = new boolean[cardV][cardV];

        for (int i = 0; i < cardV; i++)
            for (int j = 0; j < cardV; j++)
                a[i][j] = false;
        e = 0;
    }

    public Vertex addVertex(String name) {
        lastAdded++;
        vertices[lastAdded] = new Vertex(lastAdded, name);
        return vertices[lastAdded];
    }

    public Vertex addVertex(int index, String name) {
        lastAdded = index;
        vertices[lastAdded] = new Vertex(lastAdded, name);
        return vertices[lastAdded];
    }

    public Vertex addVertex(Vertex v) {
        if (v.getIndex() == Vertex.UNKNOWN_INDEX) {
            lastAdded++;
            v.setIndex(lastAdded);
        }
        else
            lastAdded = v.getIndex();
        vertices[lastAdded] = v;
        return v;
    }

    public Vertex getVertex(int index) { return vertices[index]; }
    public void addEdge(Vertex u, Vertex v) { addEdge(u.getIndex(), v.getIndex()); }

    public void addEdge(int u, int v) {
        a[u][v] = true;
        if (!directed)
            a[v][u] = true;
        e++;
    }

    public Iterator vertexIterator() { return new VertexIterator(); }

    public class VertexIterator implements Iterator {

        protected int lastVisited;

        public VertexIterator() {lastVisited = -1;}
        public boolean hasNext() { return lastVisited < vertices.length-1; }
        public Object next() { return vertices[++lastVisited]; }
        public void remove() { throw new UnsupportedOperationException(); }
    }

    public Iterator edgeIterator(Vertex u) { return new EdgeIterator(u.getIndex());}
    public Iterator edgeIterator(int u) { return new EdgeIterator(u); }

    public class EdgeIterator implements Iterator {

        protected int current;
        int u;

        public EdgeIterator(int u) {
            this.u = u;
            current = -1;
        }

        public boolean hasNext() {
            int v = current + 1; // next vertex to visit
            while (v < a[u].length && !a[u][v])
                v++;
            return v < a[u].length;
        }

        public Object next() {
            current++;
            while (!a[u][current])
                current++;
            return vertices[current];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public int getCardV() { return vertices.length; }
    public int getCardE() { return e; }
    public boolean isDirected() { return directed; }
    public boolean edgeExists(Vertex u, Vertex v) { return edgeExists(u.getIndex(), v.getIndex()); }
    public boolean edgeExists(int u, int v) { return a[u][v]; }

    public String toString() {
        String result = "";
        Iterator vertexIter = vertexIterator();
        while (vertexIter.hasNext()) {
            Vertex u = (Vertex) vertexIter.next();
            result += u + ":\n";
            Iterator edgeIter = edgeIterator(u);
            while (edgeIter.hasNext()) {
                Vertex v = (Vertex) edgeIter.next();
                result += "    " + v + "\n";
            }
        }
        return result;
    }
}


