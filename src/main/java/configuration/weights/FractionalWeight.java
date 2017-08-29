package configuration.weights;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class FractionalWeight extends BaseWeight {
    public double weight(int size, int index1, int index2) {
        double w = (double)(size - Math.abs(index1 - index2)) / size;
        return w;
    }
}