package shotgunwsd.relatedness.kernel.kmeans;

import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class CosineDistance implements DistanceFunction {
    public double distance(double[] p1, double[] p2) {
        double sim = Transforms.cosineSim(Nd4j.create(p1), Nd4j.create(p2));

        return (Math.acos(sim) / Math.PI);
    }
}
