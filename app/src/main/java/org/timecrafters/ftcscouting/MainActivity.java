package org.timecrafters.ftcscouting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public HashMap<Integer, String> teamList = new HashMap<>();
    int READ_REQUEST_CODE = 42;
    String TAG = "MAIN";
    public static MainActivity MainActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityContext = this;

        Button scoutTeam = (Button) findViewById(R.id.scout_team);
        scoutTeam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScoutTeamAutonomousActivity.class));
            }
        });

        Button scoutMatch = (Button) findViewById(R.id.scout_match);
        scoutMatch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ScoutMatchAutonomousActivity.class));
            }
        });

        Button importButton = (Button) findViewById(R.id.import_list);
        importButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performFileSearch();
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
                parseTeamsList(uri);
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

    public void parseTeamsList(Uri uri) {
        try {
            String[] list = readFileContent(uri);
            String[] part;
            for (int i = 0; i < list.length; i++) {
                if (list[i] == null) {break;}

                String line = list[i];
                Log.i(TAG, line);
                // process the line.
                part = line.split(" ");
                String teamNumber = part[0];
                String[] _teamName   = Arrays.copyOfRange(part, 1, part.length);
                String teamName = "";
                for(int _i = 0; _i < _teamName.length; _i++) {
                    if (_teamName[_i] == " " || _teamName[_i] == null) { break; }
                    teamName+=" "+_teamName[_i];
                }
                Log.i(TAG, "#: "+Integer.parseInt(teamNumber)+" name:"+teamName);
                teamList.put(Integer.parseInt(teamNumber), teamName);

            }
            TextView filename = (TextView) findViewById(R.id.team_list_filename);
            TextView matchButton = (TextView) findViewById(R.id.scout_match);
            TextView teamButton = (TextView) findViewById(R.id.scout_team);
            TextView teamStatsButton = (TextView) findViewById(R.id.team_statistics);
            filename.setText(uri.getPath());
            matchButton.setEnabled(true);
            teamButton.setEnabled(true);
            teamStatsButton.setEnabled(true);
//            filename.setTextColor(9);
            Toast.makeText(getApplicationContext(), "Successfully parsed teams file", Toast.LENGTH_SHORT).show();

            // line is not visible here.
        } catch(IOException error) {
            Toast.makeText(getApplicationContext(), "Could not read file", Toast.LENGTH_LONG).show();
        }
    }
}
