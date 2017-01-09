package configuration.operations;

/**
 * TODO add dics
 */
public abstract class ConfigurationOperation {
    protected double initialScore = 0;

    protected ConfigurationOperation() {
        // Exists only to defeat instantiation.
    }

    public abstract double applyOperation(double accumulator, double value);

    public double getInitialScore() {
        return initialScore;
    }
}
