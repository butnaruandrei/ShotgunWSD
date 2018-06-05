package shotgunwsd;

import org.apache.lucene.util.ArrayUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class IVectorSimilarity {

    // F:\Research\VarDial-ADI\dialectID-master\data\kernels\train.dev\K_ivec_dist.txt
    public static void main(String[] args) {
        String ivec_file_path = "F:\\Research\\VarDial-2018\\new-ivectors\\vardial2018\\train_dev_test.embedding";
        String output_path = "F:\\Research\\VarDial-2018\\train_dev_test\\kernels\\K_emb_new.txt";

        IVectorSimilarity similarity = new IVectorSimilarity(ivec_file_path, output_path);
    }

    public IVectorSimilarity(String input_path, String output_path) {
        Vector<String> samples = read(input_path);
        double[][] features = getFeatures(samples);

        double[][] distances = computeDistanceMatrix(features);

        writeDistanceMatrix(distances, output_path);
    }

    public void writeDistanceMatrix(double[][] distances, String outputPath) {
        try {

            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputPath), true));

            for (int i = 0; i < distances.length; i++) {
                if(i%1000 == 0)
                    System.out.println(i);

                writer.write(Arrays.toString(distances[i]).join(" "));
                writer.write("\n");
            }

            writer.close();
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double[][] computeDistanceMatrix(double[][] features) {
        double[][] distances = new double[features.length][features.length];
        double distance;

        for (int i = 0; i < features.length; i++) {
            if(i % 1000 == 0)
                System.out.println(i);
            
            for (int j = i; j < features.length; j++) {
                if (i == j){
                    distance = 0;
                } else {
                    distance = euclidianDistance(features[i], features[j]);
                }

                distances[i][j] = distances[j][i] = distance;
            }
        }

        return distances;
    }

    public double euclidianDistance(double[] features1, double[] features2){
        double sum = 0;

        for (int i = 0; i < features1.length; i++) {
            sum += Math.pow(features1[i] - features2[i], 2);
        }

        return Math.sqrt(sum);
    }

    public double[][] getFeatures(Vector<String> samples){
        double[][] features = new double[samples.size()][600];

        String[] splits;
        for(int i = 0; i < samples.size(); i++){
            splits = samples.get(i).split(" ");

            for (int j = 0; j < splits.length; j++) {
                features[i][j] = Double.parseDouble(splits[j]);
            }
        }

        return features;
    }

    public Vector<String> read(String input_path){
        String text, line;
        String[] params;

        int n = 0;

        Vector<String> samples = new Vector<String>();

        BufferedReader in;

        try
        {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(input_path), "UTF-8"));
            line = in.readLine();

            while (line != null)
            {
                line = line.trim();
                params = line.split("\t");

                // Add sample
                text = params[0];
                text = text.trim();

                text = text.replaceAll("\\s+", " ");
                samples.add(text);

                n++;
                line = in.readLine();
            }
            in.close();

            System.out.println("Loaded " + n + " samples from " + input_path);
        }
        catch (IOException e)
        {
            System.out.println(e);
            System.exit(1);
        }

        return samples;
    }
}
