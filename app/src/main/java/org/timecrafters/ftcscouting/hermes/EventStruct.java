package org.timecrafters.ftcscouting.hermes;

/**
 * Created by cyber on 1/25/2017.
 */

public class EventStruct {
    public int team;
    public String period;
    public String type;
    public String subtype;
    public String location;
    public int points;
    public String description;

    public EventStruct(int _team, String _period, String _type, String _subtype, String _location, int _points, String _description) {
        team = _team;
        period = _period;
        type = _type;
        subtype = _subtype;
        location = _location;
        points = _points;
        description = _description;
    }

    public EventStruct() {
    }
}
