package visualisation.controller.timer;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmTimer extends AnimationTimer implements ITimerObservable {

    private static AlgorithmTimer _algorithmTimer;
    private long timestamp;
    private long time = 0;
    private String _string;
    private boolean _stopped = false;
    protected List<ITimerObserver> _observerList = new ArrayList<>();

    private AlgorithmTimer() {
        super();
    }

    public static AlgorithmTimer getAlgorithmTimer() {
        if (_algorithmTimer == null) {
            _algorithmTimer = new AlgorithmTimer();
        }
        return _algorithmTimer;
    }


    @Override
    public void start() {
        // current time adjusted by remaining time from last run
        timestamp = System.currentTimeMillis();
        super.start();
    }

    @Override
    public void handle(long now) {
        long newTime = System.currentTimeMillis();
        if (timestamp <= newTime) {
            time += (newTime - timestamp);
            timestamp += (newTime - timestamp);
            setTimerStatistic(time);
        }
    }

    /**
     * stop - stop the current timer (pause)
     */
    @Override
    public void stop() {
        _stopped = true;
        super.stop();
    }

    /**
     * setTimerStatistic - runs the timer of mins, seconds, milliseconds and notify observers of update for each time
     * @param currentTime
     */
    public void setTimerStatistic(long currentTime) {
        Platform.runLater(() -> {

            long minutes = (currentTime / 60000);
            long seconds = ((currentTime - minutes * 60) / 1000);
            long milliseconds = (currentTime - minutes * 60 - seconds * 1000) / 10;

            String minutesText ="";
            String secondsText = "";
            String millisecondsText = "";
            if (seconds % 60 < 10) { //Fix seconds
                secondsText = "0" + seconds % 60;
            } else {
                secondsText = Long.toString(seconds % 60);
            }

            if (minutes < 10) {//Fix minutes
                minutesText = "0" + minutes;
            } else {
                minutesText = Long.toString(minutes);
            }

            if (milliseconds < 10) {
                millisecondsText = "00" + millisecondsText;
            } else {
                millisecondsText = Long.toString(milliseconds);
            }

            _string =  minutesText + " : " + secondsText + " : " + millisecondsText;
            notifyObserversOfTimerUpdate();
        });
    }

    public void notifyObserversOfTimerUpdate() {
        if (!_stopped) {
            for (ITimerObserver observer : _observerList) {
                observer.updateTimer(_string);
            }
        }
    }

    public void add(ITimerObserver e) {
        _observerList.add(e);
    }
}
