package visualisation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProcessorColourHelper {
    private final static int MAXMIMUM_COLOURS_NUM = 11;
    private final static String UNASSIGNED_COLOUR = "#000000";
    private Random rand = new Random();
    private List<String> _processorColours = new ArrayList<>();
    private List<String> _colours = new ArrayList<String>() {{
        //11 available colours
        add("#d66060");
        add("#d68360");
        add("#d6ab60");
        add("#d6c660");
        add("#bcd660");
        add("#60d67f");
        add("#60d6ba");
        add("#60c4d6");
        add("#608dd6");
        add("#9560d6");
        add("#d660a5");

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
