package configuration.operations;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class SumSquaredOperation extends ConfigurationOperation {
    private static SumSquaredOperation instance = null;

    public static SumSquaredOperation getInstance(){
        if(instance == null) {
            instance = new SumSquaredOperation();
        }

        return instance;
    }

    public double applyOperation(double accumulator, double value) {
        return accumulator + value * value;
    }
}
