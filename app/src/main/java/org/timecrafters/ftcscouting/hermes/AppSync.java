package org.timecrafters.ftcscouting.hermes;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.ftcscouting.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cyber on 1/24/2017.
 */

public class AppSync {
    public static boolean useFilesDirectory = false;

    public static TreeMap<Integer, String> teamsList = new TreeMap<>();

    public static int matchID = 0; // TODO: Ask for match (round) number.
    public static int teamNumber;
    public static String teamName;

    public static String competitionName = "competition"; // The name of the event being tracked, E.G. North Super Regionals
    public static String defaultFolderPath = "FTCScouting";

    public static ArrayList<EventStruct> eventsList = new ArrayList<>();

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
        if (useFilesDirectory) {
            return MainActivity.MainActivityContext.getFilesDir() + File.separator + defaultFolderPath + File.separator + competitionName + File.separator + teamNumber + File.separator + "matches";
        } else {
            return Environment.getExternalStorageDirectory().toString() + File.separator + defaultFolderPath + File.separator + competitionName + File.separator + teamNumber + File.separator + "matches";
        }
    }

    public static String getTeamDir() {
        if (useFilesDirectory) {
            return MainActivity.MainActivityContext.getFilesDir() + File.separator + defaultFolderPath + File.separator + competitionName + File.separator + teamNumber;
        } else {
            return Environment.getExternalStorageDirectory().toString() + File.separator + defaultFolderPath + File.separator + competitionName + File.separator + teamNumber;
        }
    }

    public static String getRootDir() {
        if (useFilesDirectory) {
            return MainActivity.MainActivityContext.getFilesDir() + File.separator + defaultFolderPath;
        } else {
            return Environment.getExternalStorageDirectory().toString() + File.separator + defaultFolderPath;
        }
    }

    public static void createDirectory(String path) {
        File directory = new File(path);
        if (directory.mkdirs()) {
            puts("Created path: " + path);
        } else {
            puts("Failed to create directories for " + path);
        }
    }

    // Java, why do I need 20+ lines of code to simply write to a file......................... :'(
    public static void addEvent(int match, String period, String type, String subtype, String location, int points, String description) {
        EventStruct event = new EventStruct();
        matchID = match;
        event.team = teamNumber;
        event.period = period;
        event.type = type;
        event.subtype = subtype;
        event.location = location;
        event.points = points;
        event.description = description;

        eventsList.add(event);
    }

    public static void writeEvents() {
        boolean overwrite = true;
        createDirectory(getMatchDir()); // Ensure directory exists.

        for (EventStruct event : eventsList) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("team", teamNumber);
                jsonObject.put("period", event.period);
                jsonObject.put("type", event.type);
                jsonObject.put("subtype", event.subtype);
                jsonObject.put("location", event.location);
                jsonObject.put("points", event.points);
                jsonObject.put("description", event.description);
            } catch (JSONException error) {
                overwrite = false;
            }

            writeJSON(jsonObject, getMatchDir() + File.separator + "" + matchID + "-" + System.currentTimeMillis() / 1000 + ".json", true);
        }

        if (overwrite) {
            eventsList = new ArrayList<>();
        } else {
            MainActivity.MainActivityContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.MainActivityContext.getApplicationContext(), "Failed to write events list!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static boolean writeJSON(JSONObject jsonObject, String path, boolean appendMode) {
        BufferedWriter bw = null;
        boolean failure = false;
        boolean writeSuccess = false;

        try {
            try {
                bw = new BufferedWriter(new FileWriter(path, appendMode));
            } catch (IOException error) {
                failure = true;
                puts(error.toString());
            }

            if (!failure) {
                bw.write(jsonObject.toString());
                bw.newLine();
                bw.flush();
                writeSuccess = true;
            }
        } catch (IOException error) {
            // Eating fish and chips, bbl.
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException error) {
                    puts("Ugg.");
                }
            }
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
        } catch (JSONException e) {
            writeSuccess = false;
        }
        return writeSuccess;
    }

    public static boolean newMatch(int teamNumber) {
        return true;
    }

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException errorObject) {
            // Java, you're funny.
        }
    }
}
