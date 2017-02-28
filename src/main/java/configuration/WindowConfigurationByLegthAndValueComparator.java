package configuration;

import java.util.Comparator;

/**
 * Created by Butnaru Andrei-Madalin.
 */
public class WindowConfigurationByLegthAndValueComparator implements Comparator<WindowConfiguration> {
    public int compare(WindowConfiguration c1, WindowConfiguration c2) {
        double s1 = c1.getScore(), s2 = c2.getScore();
        int l1 = c1.getLength(), l2 = c2.getLength();

        if (l2 == l1) {
            if (s2 == s1) {
                return 0;
            } else if (s2 > s1) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (l2 > l1)
                return 1;
            else
                return -1;
        }
    }
}
