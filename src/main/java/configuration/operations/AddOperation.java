package configuration.operations;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class AddOperation extends ConfigurationOperation {
    private static AddOperation instance = null;

    public static AddOperation getInstance(){
        if(instance == null) {
            instance = new AddOperation();
        }

        return instance;
    }

    public double applyOperation(double accumulator, double value) {
        return accumulator + value;
    }
}
