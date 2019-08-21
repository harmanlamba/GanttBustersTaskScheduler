package visualisation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProcessorColourHelper {
    private final static int MAXMIMUM_COLOURS_NUM = 14;
    private final static String UNASSIGNED_COLOUR = "#000000";
    private Random rand = new Random();
    private List<String> _processorColours = new ArrayList<>();
    private List<String> _colours = new ArrayList<String>() {{
        //14 available colours
        add("#b35454");
        add("#b39854");
        add("#98b354");
        add("#75b354");
        add("#54b36a");
        add("#54b390");
        add("#54b1b3");
        add("#5498b3");
        add("#546db3");
        add("#5d54b3");
        add("#9854b3");
        add("#b354a2");
        add("#b35482");
        add("#b35469");
    }};

    public ProcessorColourHelper(int processCount) {
        setProcessorColours(processCount);
    }

    public String getProcessorColour(int processorIndex) {
        if (processorIndex != -1) {
            return _processorColours.get(processorIndex);
        } else {
            return UNASSIGNED_COLOUR;
        }
    }

    private void setProcessorColours(int processorCount) {
        //If only 1 processor count, then set to main node colour
        int randomColourNumber = rand.nextInt(100);
        if (processorCount > 1) {
            for (int i = 0; i < processorCount; i++) {
                _processorColours.add(_colours.get((i + randomColourNumber) % MAXMIMUM_COLOURS_NUM));
            }
        } else {
            _processorColours.add(_colours.get(1));
        }
    }

}
