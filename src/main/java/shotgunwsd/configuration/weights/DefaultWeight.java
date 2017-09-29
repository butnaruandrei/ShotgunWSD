package shotgunwsd.configuration.weights;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class DefaultWeight extends BaseWeight {
    public double weight(int size, int index1, int index2) {
        return 1.0d;
    }
}
