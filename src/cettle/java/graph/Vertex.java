package cettle.java.graph;

public class Vertex {
    public static final int UNKNOWN_INDEX = -1;

    private int index;

    private String name;

    public Vertex(String name) {
        index = UNKNOWN_INDEX;
        this.name = name;
    }

    public Vertex(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + " (index = " + index + ")";
    }
}


