package relatedness.kernel.kmeans;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class KMeans {
    private double[][] _points;
    private double[][] _centroids;
    private int _k, _dimension;

    private int _iterations = 1000;

    private int[] _assignedCentroid, _previousAssignedCentroid = null;
    private DistanceFunction distanceFunction = new EuclidianDistance();
    private ArrayList<Integer>[] _clusters;

    public void set_iterations(int _iterations) {
        this._iterations = _iterations;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public int[] get_assignedCentroid() {
        return _assignedCentroid;
    }

    public double[][] get_centroids(){
        return _centroids;
    }

    public KMeans(double[][] points, int k) {
        this._points = points;
        this._k = k;

        // TODO throw exception is points are empty
        this._dimension = _points[0].length;
        this._centroids = new double[k][_dimension];
        this._assignedCentroid = new int[points.length];

        this._clusters = new ArrayList[k];
        resetClusters();
    }

    public void run() {
        setInitialCentroids();

        for (int i = 0; i < _iterations; i++) {
            assignClusters();

            if(stop()) {
                System.out.println("KMeans stop early at iteration " + i + "/" + _iterations);
                break;
            }

            recomputeCentroids();
        }
    }

    private void resetClusters() {
        for (int i = 0; i < _clusters.length; i++) {
            _clusters[i] = new ArrayList<>();
        }

        _previousAssignedCentroid = _assignedCentroid.clone();
    }

    private void assignClusters() {
        resetClusters();
        int nc;

        for (int i = 0; i < _points.length; i++) {
            nc = nearestCentroid(_points[i]);
            _assignedCentroid[i] = nc;
            _clusters[nc].add(i);
        }
    }

    private int nearestCentroid(double[] point) {
        double min_distance = Integer.MAX_VALUE, distance;
        int cluster = -1;

        for (int i = 0; i < _centroids.length; i++) {
            distance = distanceFunction.distance(point, _centroids[i]);
            if(distance < min_distance) {
                min_distance = distance;
                cluster = i;
            }
        }

        return cluster;
    }

    private void recomputeCentroids() {
        for (int i = 0; i < _clusters.length; i++) {
            _centroids[i] = computeCentroid(_clusters[i]);
        }
    }

    private double[] computeCentroid(ArrayList<Integer> cluster) {
        double[] centroid = new double[_dimension];
        for (int i = 0; i < _dimension; i++) {
            centroid[i] = 0;
        }

        for(Integer c : cluster){
            for (int i = 0; i < _dimension; i++) {
                centroid[i] += _points[c][i];
            }
        }

        for (int i = 0; i < _dimension; i++) {
            centroid[i] /= cluster.size();
        }

        return centroid;
    }

    private boolean stop(){
        for (int i = 0; i < _assignedCentroid.length; i++) {
            if(_assignedCentroid[i] != _previousAssignedCentroid[i])
                return false;
        }

        return true;
    }

    private void setInitialCentroids(){
        Random r = new Random();
        int p;

        for (int i = 0; i < _k; i++) {
            p = r.nextInt(_points.length);
            _centroids[i] = _points[p];

            _assignedCentroid[p] = i;
            _clusters[i].add(p);
        }
    }
}
