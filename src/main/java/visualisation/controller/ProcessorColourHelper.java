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
        add("#8e8aff");
        add("#b18aff");
        add("#ffb18a");
        add("#8ac1ff");
        add("#ff8a8a");
        add("#fff18a");
        add("#ccff8a");
        add("#8adaff");
        add("#8affb9");
        add("#ffd88a");
        add("#efff8a");
        add("#8aa9ff");
        add("#8af5ff");
        add("#9aff8a");
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
