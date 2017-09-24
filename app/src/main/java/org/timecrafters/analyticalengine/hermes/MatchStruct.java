package org.timecrafters.analyticalengine.hermes;

/**
 * Created by cyber on 2/3/2017.
 */

public class MatchStruct {
    // Counters for Tally
    public int beaconsClaimed, beaconsMissed, beaconsStolen = 0;
    public int scoredInVortex, scoredInCorner, missedVortex,  missedCorner = 0;
    public int completelyOnPlatform, completelyOnRamp, onPlatform, onRamp, missedParking = 0;
    public int capballOnFloor, capballMissed, capballOffFloor, capballAboveCrossbar, capballCapped = 0;
    public int deadRobot = 0;

    // Booleans
    public boolean is_capballOnFloor, is_capbalOffFloor, is_capballAboveCrossbar, is_capballCapped = false;
    public boolean is_deadRobot = false;

    public String toString() {
        String string = "MatchScruct";
        string+="\nBeacons: claimed:"+beaconsClaimed+" missed: "+beaconsMissed+" stolen: "+beaconsStolen;
        string+="\nParticles: Scored: vortex: "+scoredInVortex+" corner: "+scoredInCorner+" Missed: vortex: "+missedVortex+" corner: "+missedCorner;
        string+="\nParking: score: completely on platform: "+completelyOnPlatform+" completely on ramp: "+completelyOnRamp+" on platform: "+onPlatform+" on ramp: "+onRamp+" missed: "+missedParking;
        string+="\nCapball: score: on floor: "+capballOnFloor+" off floor: "+capballOffFloor+" above crossbar: "+capballAboveCrossbar+" capped: "+capballCapped+" missed: "+capballMissed;
        string+="\nDead Robot: "+deadRobot;
        string+="\nBooleans: capball: on floor: "+is_capballOnFloor+" off floor:"+is_capbalOffFloor+" above crossbar: "+is_capballAboveCrossbar+" capped: "+is_capballCapped+" robot: dead robot: "+is_deadRobot;
        return string;
    }
}
