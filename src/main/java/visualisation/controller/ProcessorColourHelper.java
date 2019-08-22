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
        add("#ed5a5a");
        add("#5aed95");
        add("#ed975a");
        add("#5ab0ed");
        add("#ede65a");
        add("#5aeded");
        add("#77ed5a");
        add("#c15aed");
        add("#5acbed");
        add("#9f5aed");
        add("#735aed");
        add("#735aed");
        add("#ed5a92");
        add("#c6ed5a");
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
        int randomColourNumber = rand.nextInt(MAXMIMUM_COLOURS_NUM);
        if (processorCount > 1) {
            for (int i = 0; i < processorCount; i++) {
                _processorColours.add(_colours.get((i + randomColourNumber) % MAXMIMUM_COLOURS_NUM));
            }
        } else {
            _processorColours.add(_colours.get(1));
        }
    }

}
