package visualisation.controller.timer;

public interface ITimerObserver {
    /**
     * Method used by observers to let know a timer update has occurred
     * @param s
     */
    void updateTimer(String s);
}
