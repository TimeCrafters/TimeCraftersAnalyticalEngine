package org.timecrafters.analyticalengine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.timecrafters.analyticalengine.athena.ScoutMatchAutonomousActivity;
import org.timecrafters.analyticalengine.athena.ScoutTeamAutonomousActivity;
import org.timecrafters.analyticalengine.athena.TeamStatisticsActivity;
import org.timecrafters.analyticalengine.hermes.AppSync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    final int READ_REQUEST_CODE = 42;
    final int REQUEST_WRITE_PERMISSION = 70;
    String TAG = "MAIN";
    public static MainActivity MainActivityContext;

    public TextView filename;
    public TextView teamStatsButton;

    public Button scoutTeamButton;
    public Button scoutMatchButton;
    public Button importButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityContext = this;
        AppSync.getConfig();
        try {
            String list = AppSync.getConfig().getString("last_used_teams_list");
            if (!list.equals("")) {
                try {
                    parseTeamsList(Uri.parse(list), true);
                } catch (SecurityException error) {
                    AppSync.puts("LIST", "Failed to load list.\n"+error.getMessage());
                }
            }
        } catch (JSONException error) {/* Fault */}

        filename = (TextView) findViewById(R.id.team_list_filename);
        teamStatsButton = (TextView) findViewById(R.id.team_statistics);
        importButton = (Button) findViewById(R.id.import_list);

        scoutTeamButton = (Button) findViewById(R.id.scout_team);
        scoutMatchButton = (Button) findViewById(R.id.scout_match);

        if (AppSync.teamsListUri != null) {
            filename.setText(AppSync.teamsListUri.getPath());
            scoutMatchButton.setEnabled(true);
            scoutTeamButton.setEnabled(true);
            teamStatsButton.setEnabled(true);
        }

        scoutTeamButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScoutTeamAutonomousActivity.class));
            }
        });

        scoutMatchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScoutMatchAutonomousActivity.class));
            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean canUseExternalStorage = true;
                JSONObject config = AppSync.getConfig();
                try {
                    if (config.getBoolean("use_external_storage")) {
                        canUseExternalStorage = true;
                    } else { canUseExternalStorage = false; }
                } catch (JSONException error) { AppSync.puts(TAG, "Error in Config: "+error.getMessage());}
                if (canUseExternalStorage && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {


                    AppSync.createConfirmDialog(MainActivityContext, "Write Permissions", "This app requires write access to read/write files accessible in userspace.\n\n If you continue without allowing write access, you'll only be able to access the written files on a Rooted device and data will be destroyed if you uninstall the app.", new Runnable() {
                        @Override
                        public void run() {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_PERMISSION);

                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            AppSync.createAlertDialog(MainActivityContext, "Permission not Granted", "Data will be stored in " + getFilesDir() + "\n\nData loss WILL occur if you uninstall.", new Runnable() {
                                @Override
                                public void run() {
                            performFileSearch();
                            AppSync.updateConfig("use_external_storage", false);
                            AppSync.useFilesDirectory = true;
                                }
                            });
                        }
                    }, "Ask", "Decline");

                } else {
                    performFileSearch();
                }
            }
        });

        teamStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, teamStatsButton);
                popupMenu.getMenu().setQwertyMode(false);

                for (HashMap.Entry<Integer, String> entry : AppSync.teamsList.entrySet()) {
                    MenuItem temp = popupMenu.getMenu().add("" + entry.getKey() + " | " + entry.getValue());
                    SpannableString team_number = new SpannableString(""+entry.getKey());
                    SpannableString team_name   = new SpannableString(""+entry.getValue());
                    if (AppSync.teamHasScoutingData(entry.getKey())) {
                        team_number.setSpan(new ForegroundColorSpan(Color.rgb(0, 150, 0)), 0, team_number.length(), 0);
                    } else {
                        team_number.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, team_number.length(), 0);
                    }
                    if (AppSync.teamHasMatchData(entry.getKey())) {
                        team_name.setSpan(new ForegroundColorSpan(Color.rgb(200, 80, 0)), 0, team_name.length(), 0);
                    } else {
                        team_name.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, team_name.length(), 0);
                    }
                    temp.setTitle(TextUtils.concat(team_number, " | ", team_name));
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int teamNumber;
                        String teamName;

                        String[] t = item.getTitle().toString().split("\\|");
                        teamNumber = Integer.parseInt(t[0].replaceAll("\\s+", ""));
                        teamName = t[1].substring(2); //.replace(" ","");
                        AppSync.teamNumber = teamNumber;
                        AppSync.teamName = teamName;

                        startActivity(new Intent(getBaseContext(), TeamStatisticsActivity.class));

                        return true;
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        if (0 == AppSync.teamNumber) {
//                            AppSync.createMessageDialog(MainActivityContext, "No Team Selected", "Can't load data for a phantom team.");
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppSync.updateConfig("use_external_storage", true);
                    AppSync.useFilesDirectory = false;
                    performFileSearch();
                } else {
                    AppSync.updateConfig("use_external_storage", true);
                    AppSync.useFilesDirectory = true;
                    performFileSearch();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                parseTeamsList(uri, false);
            }
        }
    }

    private String[] readFileContent(Uri uri) throws IOException {
        String[] list = new String[1000];
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentLine;

        for (int i = 0; (currentLine = reader.readLine()) != null; i++) {
            if (!currentLine.equals("")) {
                list[i] = currentLine;
            }
        }
        inputStream.close();
        return list;
    }

    public void parseTeamsList(Uri uri, boolean loadingLastUsedList) {
        boolean failed = false;
        AppSync.teamsList.clear();
        try {
            String[] list = readFileContent(uri);
            String[] part;
            for (int i = 0; i < list.length; i++) {
                if (list[i] == null) {
                    break;
                }

                String line = list[i];
                // process the line.
                part = line.split(" ");
                String teamNumber = part[0];
                String[] _teamName = Arrays.copyOfRange(part, 1, part.length);
                String teamName = "";
                for (int _i = 0; _i < _teamName.length; _i++) {
                    if (_teamName[_i] == " " || _teamName[_i] == null) {
                        break;
                    }
                    teamName += " " + _teamName[_i];
                }
                AppSync.teamsList.put(Integer.parseInt(teamNumber), teamName);
            }

            TextView filename = (TextView) findViewById(R.id.team_list_filename);
            TextView matchButton = (TextView) findViewById(R.id.scout_match);
            TextView teamButton = (TextView) findViewById(R.id.scout_team);
            TextView teamStatsButton = (TextView) findViewById(R.id.team_statistics);
            filename.setText(uri.getPath());
            matchButton.setEnabled(true);
            teamButton.setEnabled(true);
            teamStatsButton.setEnabled(true);
            if (loadingLastUsedList) {
                Toast.makeText(getApplicationContext(), "Successfully parsed last used teams file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Successfully parsed teams file", Toast.LENGTH_SHORT).show();
            }
            matchButton.setBackgroundColor(getResources().getColor(R.color.colorMainButton));
            teamButton.setBackgroundColor(getResources().getColor(R.color.colorMainButton));
            teamStatsButton.setBackgroundColor(getResources().getColor(R.color.colorMainButton));

        } catch (IOException | NumberFormatException error) {
            failed = true;
            AppSync.createMessageDialog(this, "Parsing Error", "An error occurred while parsing \"" + uri.getPath() + "\"\n\n   " +
                    "This might be because of non numeric characters on the beginning of a line.\n\n" +
                    "Error Caught: " + error.getMessage());
        }

        if (!failed) {
            AppSync.teamsListUri = uri;
            AppSync.updateConfig("last_used_teams_list", uri.toString());
        }
    }
}
