package org.timecrafters.analyticalengine.hermes;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.analyticalengine.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cyber on 1/24/2017.
 */

public class AppSync {
    public static final int SOFT_MAX_TEAMSLIST_SIZE = 1000;
    public static JSONObject appConfig;
    public static boolean useFilesDirectory = false; // Store data in apps 'files' directory or in 'External Storage'
    public static Uri teamsListUri;

    public static TreeMap<Integer, String> teamsList = new TreeMap<>();

    public static int matchID = 0; // TODO: Ask for match (round) number.
    public static int teamNumber;
    public static String teamName;

    public static String competitionName = "competition"; // The name of the event being tracked, E.G. North Super Regionals
    public static String defaultFolderPath = "TimeCraftersAnalyticalEngine";
    public static String currentMatchPath;

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
        if (currentMatchPath != null) {
            writeEvents(currentMatchPath);
        } else {
            currentMatchPath = getMatchDir()+ File.separator +matchID+"-"+ System.currentTimeMillis()/1000; // .json will be appended below
        }
    }

    public static void writeEvents(String path) {
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
                puts("EVENTS", "Fatal error: "+error.getMessage());
            }

            writeJSON(jsonObject, path + ".json", true);
        }

        if (overwrite) {
            eventsList = new ArrayList<>();
            puts("EVENTS", "Wrote eventLog to "+path+ ".json");
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

    // streaming means treat each line is treated as a JSONObject instead of the whole file
    public static ArrayList<JSONObject> readJSON(File file, boolean streaming) {
        StringBuilder text = new StringBuilder();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        boolean successful = true;

        if (streaming) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                br.close();

                for (String here : lines) {
                    jsonObjects.add(new JSONObject(here));
                }

            } catch (IOException | JSONException e) {
                successful = false;
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                jsonObjects.add(new JSONObject(text.toString()));
            } catch (IOException | JSONException e) {
                successful = false;
            }
        }

        if (successful) {
            return jsonObjects;
        } else {return null; }
    }

    public static JSONObject getConfig() {
        JSONObject config = null;
        String filePath = MainActivity.MainActivityContext.getFilesDir() + File.separator + "config.json";
        File file = new File(filePath);

        try {

            if (file.exists() && file.length() > 2) {
                config = readJSON(file, false).get(0);
            } else {
                config = new JSONObject();
                config.put("last_used_teams_list", "");
                config.put("use_external_storage", true);
                writeJSON(config, filePath, false);

                config = readJSON(file, false).get(0);
            }
        } catch (JSONException | NullPointerException error) {
            puts("APPSYNC", "Error in getConfig: "+ error.getMessage());
        }

        if (config != null) { appConfig = config; }
        return config;
    }

    public static JSONObject updateConfig(String key, Object value) {
        String filePath = MainActivity.MainActivityContext.getFilesDir() + File.separator + "config.json";
        File file = new File(filePath);
        JSONObject config = getConfig();
        try {
            config.put(key, value);
        } catch (JSONException error) {
            puts("APPSYNC", "Config error: "+ error.getMessage());
        }

        try {
            writeJSON(config, filePath, false);
            config = readJSON(file, false).get(0);
        } catch (NullPointerException error) {
            puts("APPSYNC", "Error in getConfig: "+ error.getMessage());
        }

        if (config != null) { appConfig = config; }
        return config;
    }

    public static JSONObject teamScoutingData(String period) {
        JSONObject jsonObject = null;
        File file = new File(getTeamDir()+File.separator+period+".json");
        ArrayList<JSONObject> objects = readJSON(file, false);
        if (objects != null) {
            jsonObject = objects.get(0);
        }

        return jsonObject;
    }

    public static ArrayList<ArrayList<EventStruct>> teamMatchData() {
        File[] matchesFiles = new File(getMatchDir()).listFiles();
        ArrayList<ArrayList<EventStruct>> matches = new ArrayList<ArrayList<EventStruct>>();
        for (File file : matchesFiles) {
            ArrayList<JSONObject> objects = readJSON(file, true);
            ArrayList<EventStruct> events = new ArrayList<>();

            if (objects != null) {
                for (JSONObject object : objects) {
                    EventStruct event = new EventStruct();
                    try {
                        event.period     = object.getString("period");
                        event.type       = object.getString("type");
                        event.subtype    = object.getString("subtype");
                        event.location   = object.getString("location");
                        event.points     = object.getInt("points");
                        event.description= object.getString("description");
                        events.add(event);
                    } catch (JSONException error) {
                        puts("MATCH", "Error in TeamMatchData: "+error.getMessage());
                    }
                }
            }
            matches.add(new ArrayList<EventStruct>(events));
            for (ArrayList event : matches) {
                puts("STATS", "Events Size: "+event.size());
            }
            puts("STATS", "Added events to match list");
            puts("STATS", "Events count: "+events.size()+" type: "+events.getClass());
            events.clear();
        }

        puts("STATS", "Size of matches: "+matches.size()+ " Size of match 0: "+matches.get(0).size());
        return matches;
    }

    public static boolean teamHasScoutingData() {
        boolean teamDataExists = false;
        File auto = new File(getTeamDir()+ File.separator +"autonomous.json");
        File tele = new File(getTeamDir()+ File.separator +"teleop.json");
        if (auto.exists() && auto.length() > 0) { teamDataExists = true; }
        if (tele.exists() && tele.length() > 0) { teamDataExists = true; }

        return teamDataExists;
    }
    public static boolean teamHasScoutingData(int _teamNumber) {
        boolean teamDataExists = false;
        File auto = new File(getRootDir()+ File.separator+ competitionName+ File.separator + _teamNumber+ File.separator +"autonomous.json");
        File tele = new File(getRootDir()+ File.separator+ competitionName+ File.separator +_teamNumber+ File.separator +"teleop.json");
        if (auto.exists() && auto.length() > 0) { teamDataExists = true; }
        if (tele.exists() && tele.length() > 0) { teamDataExists = true; }

        return teamDataExists;
    }

    public static boolean teamHasMatchData() {
        boolean teamDataExists = false;
        File[] matches = new File(getMatchDir()).listFiles();
        if (matches != null && matches.length > 0) { teamDataExists = true; }

        return teamDataExists;
    }
    public static boolean teamHasMatchData(int _teamNumber) {
        boolean teamDataExists = false;
        File[] matches = new File(getRootDir()+ File.separator+ competitionName+ File.separator+ _teamNumber+ File.separator +"matches").listFiles();
        if (matches != null && matches.length > 0) { teamDataExists = true; }

        return teamDataExists;
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

    public static void createConfirmDialog(AppCompatActivity activity, String title, String message, final Runnable acceptRunner, final Runnable declineRunner, String positiveButtonText, String negativeButtonText) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title).setMessage(message);
        alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                declineRunner.run();
            }
        });
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                acceptRunner.run();
            }
        });

        alert.show();
    }
    public static void createConfirmDialog(AppCompatActivity activity, String title, String message, final Runnable acceptRunner, final Runnable declineRunner) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title).setMessage(message);
        alert.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                declineRunner.run();
            }
        });
        alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                acceptRunner.run();
            }
        });

        alert.show();
    }

    public static void createAlertDialog(AppCompatActivity activity, String title, String message, final Runnable acceptRunner, String positiveButtonText) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title).setMessage(message);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                acceptRunner.run();
            }
        });

        alert.show();
    }
    public static void createAlertDialog(AppCompatActivity activity, String title, String message, final Runnable acceptRunner) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title).setMessage(message);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                acceptRunner.run();
            }
        });

        alert.show();
    }

    public static void createMessageDialog(AppCompatActivity activity, String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title).setMessage(message);
        alert.show();
    }
}
