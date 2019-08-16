package visualisation.controller;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper extends Thread {
    private int _currentTime;
    private Timer _timer;
    private MainController _mainController;

    TimerHelper(MainController mainController) {
        _timer = new Timer("mainControllerTimer");
        _mainController = mainController; //To check if mainController is active
    }

    public void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                _currentTime++;
                if (_mainController != null) {
                    //Set the timer for mainController
                    _mainController.setTimerStatistic(_currentTime);
                }
            }
        };

        _timer.scheduleAtFixedRate(timerTask, 0, 10);
    }

    public void stopTimer() {
        _timer.cancel();
    }

}
