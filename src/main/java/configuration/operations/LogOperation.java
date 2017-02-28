package configuration.operations;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class LogOperation extends ConfigurationOperation {
    private static LogOperation instance = null;

    public static LogOperation getInstance(){
        if(instance == null) {
            instance = new LogOperation();
        }

        return instance;
    }

    public double applyOperation(double accumulator, double value) {
        return accumulator + Math.log(2 + value);
    }
}
