package visualisation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ProcessorColourHelper - class to manage colours to assign for each different processor (visualisation)
 */
public class ProcessorColourHelper {

    private final static int MAXIMUM_COLOURS_NUM = 11;
    private final static String UNASSIGNED_COLOUR = "#000000";
    private Random rand = new Random();
    private List<String> _processorColours = new ArrayList<>();
    private List<String> _colours = new ArrayList<String>() {{
        // 11 available colours
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

    /**
     * Returns the processor's colour if the processor is already assigned
     * or a new unassigned colour
     * @param processorIndex the assigned processor number
     * @return the colour as a String
     */
    public String getProcessorColour(int processorIndex) {
        if (processorIndex != -1) {
            return _processorColours.get(processorIndex);
        } else {
            return UNASSIGNED_COLOUR;
        }
    }

    /**
     * Setter method giving each processor its own colour
     * @param processorCount
     */
    private void setProcessorColours(int processorCount) {
        // If only 1 processor count, then set to main node colour
        int randomColourNumber = rand.nextInt(MAXIMUM_COLOURS_NUM);
        if (processorCount > 1) {
            for (int i = 0; i < processorCount; i++) {
                _processorColours.add(_colours.get((i + randomColourNumber) % MAXIMUM_COLOURS_NUM)); //set processor colour of 11 lists
            }
        } else {
            _processorColours.add(_colours.get(1));
        }
    }
}
