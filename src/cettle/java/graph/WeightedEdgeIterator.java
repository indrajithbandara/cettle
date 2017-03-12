package cettle.java.graph;

import java.util.Iterator;

public interface WeightedEdgeIterator extends Iterator {
    public double getWeight();
    public void setWeight(double weight);
}


