package visualisation.controller;

import java.util.ArrayList;
import java.util.List;

public class ProcessorColourHelper {
    private List<String> _processorColours = new ArrayList<>();
    private List<String> _colours = new ArrayList<String>() {{
        //TODO: As there are only 6 colours here, it throws an error if I try to schedule the tasks onto 7 processors as there is not enough colours. A better solution needs to be thought of for this.
        add("#90EE90");
        add("#708090");
        add("#8A2BE2");
        add("#FFF8DC");
        add("#FF6347");
        add("#00CED1");
    }};

    public ProcessorColourHelper(int processCount) {
        setProcessorColours(processCount);
    }

    public String getProcessorColour(int processorIndex) {
        return _processorColours.get(processorIndex);
    }

    private void setProcessorColours(int processorCount) {
        //If only 1 processor count, then set to main node colour
        if (processorCount > 1) {
            for (int i = 0; i < processorCount; i++) {
                _processorColours.add(_colours.get(i));
            }
        } else {
            _processorColours.add(_colours.get(1));
        }
    }

}
