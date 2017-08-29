package configuration.weights;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class ExponentialWeight extends BaseWeight {
    public double weight(int size, int index1, int index2) {
        double w = Math.pow(1 - alpha(size), Math.abs(index1 - index2) - 1);
        return w;
    }

    private double alpha(int size) {
        double a = 1 - Math.pow(0.1, 1.0d / (size - 1));
        return a;
    }
}
