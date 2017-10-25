package org.timecrafters.analyticalengine.hermes;

/**
 * Created by cyber on 2/3/2017.
 */

public class MatchStruct {
    // Counters for Tally
    public int jewelScored, jewelMissed = 0;
    public int glyphScored, glyphCryptoboxKey, glyphMissed = 0;
    public int relicZone1, relicZone2, relicZone3, relicUpright, relicMissed = 0;
    public int parkSafeZone, parkMissed = 0;
    public int deadRobot = 0;

    // Booleans
    public boolean is_deadRobot, is_relicUpright = false;

    public String toString() {
        String string = "MatchScruct";
        string+="\nJewels: Scored:"+jewelScored+" missed: "+jewelMissed;
        string+="\nGlyphs: Scored: "+glyphScored+" missed: "+glyphMissed+" cryptobox key: "+glyphCryptoboxKey;
//        string+="\nRelic: Scored: "+glyphScored+" missed: "+glyphMissed+" cryptobox key: "+glyphCryptoboxKey;
        string+="\nParking: Scored: "+parkSafeZone+" missed: "+parkMissed;
        string+="\nDead Robot: "+deadRobot;
        string+="\nBooleans: robot: dead robot: "+is_deadRobot;
        return string;
    }
}
