package graph;

import java.util.Objects;

public class Temp {

    private String _id;
    private int _startTime;

    public Temp(String id, int startTime) {
        _id = id;
        _startTime = startTime;
    }

    public String getId() {
        return _id;
    }

    public int getStartTime() {
        return _startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temp temp = (Temp) o;
        return _startTime == temp._startTime &&
                _id.equals(temp._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _startTime);
    }
}
