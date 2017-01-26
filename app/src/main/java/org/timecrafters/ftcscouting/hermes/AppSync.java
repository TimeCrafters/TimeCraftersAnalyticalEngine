package org.timecrafters.ftcscouting.hermes;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by cyber on 1/24/2017.
 */

public class AppSync {
    public static int teamNumber;
    public static String teamName;

    public static String competitionName = "competition"; // The name of the event being tracked, E.G. North Super Regionals
    public static String defaultFolderPath = "FTCScouting";

    public static void puts(String tag, String string) {
        Log.i(tag, string);
    }

    // Ruby, I miss you. :'(
    // I wish there was a compilable version of you that didn't require a Mac.
    public static void puts(String string) {
        String tag = "APPLOG"; // App Log | for quick debugging
        puts(tag, string);
    }

    public static String getMatchDir() {
        return Environment.getExternalStorageDirectory().toString()+File.separator+ defaultFolderPath +File.separator +competitionName+ File.separator+ teamNumber+ File.separator +"matches";
    }

    public static String getTeamDir() {
        return Environment.getExternalStorageDirectory().toString()+File.separator+ defaultFolderPath +File.separator +competitionName+ File.separator+ teamNumber;
    }

    public static void createDirectory(String path) {
        File directory = new File(path);
        if (directory.mkdirs()) {
            puts("Created path: "+path);
        } else { puts("Failed to create directories for "+path); }
    }

    // Java, why do I need 20+ lines of code to simply write to a file......................... :'(
    public static boolean addEvent(int match, String period, String type, String subtype, int points, String description) {
        boolean failure      = false;
        boolean writeSuccess = false;
        JSONObject event  = new JSONObject();
        BufferedWriter bw = null;
        try {
            event.put("team", teamNumber);
            event.put("period", period);
            event.put("type", type);
            event.put("subtype", subtype);
            event.put("points", points);
            event.put("description", description);
        } catch(JSONException e) {
            writeSuccess = false;
        }

        try {
            puts(getMatchDir());
            createDirectory(getMatchDir());
            try {
                try {
                    bw = new BufferedWriter(new FileWriter(getMatchDir() + File.separator + match + ".json", true));
                } catch (IOException error) {
                    failure = true;
                    puts(error.toString());
                }

                if (!failure) {
                    bw.write(event.toString());
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException error) {
                    // Eating fish and chips, bbl.
                } finally {
                if (bw != null) {
                    bw.close();
                }
            }

        } catch (IOException error){
            writeSuccess = false;
            // Eh.
        }
        return writeSuccess;
    }

    public static boolean addTeam(Object[] autonomous, Object[] teleOp) {
        boolean writeSuccess = false;
        try {
            JSONObject event = new JSONObject();
            event.put("team", teamNumber);
            event.put("autonomous", autonomous);
            event.put("teleop", teleOp);
        } catch(JSONException e) {
            writeSuccess = false;
        }
        return writeSuccess;
    }

    public static boolean newMatch(int teamNumber) {
        return true;
    }
}
