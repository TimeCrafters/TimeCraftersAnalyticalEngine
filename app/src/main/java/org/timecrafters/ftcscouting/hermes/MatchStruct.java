package org.timecrafters.ftcscouting.hermes;

/**
 * Created by cyber on 2/3/2017.
 */

public class MatchStruct {
    // Counters for Tally
    public int beaconsClaimed, beaconsMissed, beaconsStolen = 0;
    public int scoredInVortex, scoredInCorner, missedVortex,  missedCorner = 0;
    public int completelyOnPlatform, completelyOnRamp, onPlatform, onRamp, missedPlatform, missedRamp = 0;
    public int capballOnFloor, capballMissed, capballAboveCrossbar, capballCapped = 0;
    public int deadRobot = 0;

    // Booleans
    public boolean is_capballOnFloor, is_capballAboveCrossbar, is_capballCapped = false;
    public boolean is_deadRobot = false;

}
