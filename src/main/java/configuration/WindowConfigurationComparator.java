package configuration;

import java.util.Comparator;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class WindowConfigurationComparator implements Comparator<WindowConfiguration> {
    public int compare(WindowConfiguration c1, WindowConfiguration c2) {
        double s1 = c1.getScore(), s2 = c2.getScore();

        if(s1 == s2){
            return 0;
        } else if (s1 < s2){
            return -1;
        } else {
            return 1;
        }
    }
}
