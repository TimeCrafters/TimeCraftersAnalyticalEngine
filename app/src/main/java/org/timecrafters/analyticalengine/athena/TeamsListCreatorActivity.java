package org.timecrafters.analyticalengine.athena;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.hermes.AppSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamsListCreatorActivity extends AppCompatActivity {
    HashMap<String, String> teams                = new HashMap<String, String>();
    ArrayList countries          = new ArrayList<>();
    ArrayList states_provinces   = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list_creator);

        megaParser(loadUltimateTeamsList());

        String[] teamNumberList = new String[teams.keySet().size()];
        String[] teamNameList   = new String[teams.values().size()];

        int _i = 0;
        for(String key : teams.keySet()) {
            teamNumberList[_i] = (String) key;
            teamNameList[_i] = (String) teams.get(key);
            _i++;
        }

        ArrayAdapter<String> numberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, teamNumberList);
        AutoCompleteTextView numberView = (AutoCompleteTextView) findViewById(R.id.team_number);
        numberView.setAdapter(numberAdapter);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, teamNameList);
        AutoCompleteTextView nameView = (AutoCompleteTextView) findViewById(R.id.team_name);
        nameView.setAdapter(nameAdapter);

        numberView.setThreshold(1);
        nameView.setThreshold(2);

    }

    void megaParser(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            lineParser((String) arrayList.get(i));
        }
    }

    private void lineParser(String line) {
        String[] array = line.split(" ");
        countryAndRegionParser(array[1]);

        String teamString = "";
        for(int i = 2; i < array.length; i++) {
            teamString+=array[i];
        }

        teams.put(array[0], teamString);
    }

    private void countryAndRegionParser(String s) {
    }

    private ArrayList loadUltimateTeamsList() {
        String readLine = null;
        ArrayList arrayList= new ArrayList();
        InputStream file = (getResources().openRawResource(R.raw.ultimate_teams_list));
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));

        try {
            while ((readLine = reader.readLine()) != null) {
                arrayList.add(readLine);
            }
            file.close();
            reader.close();
        } catch(IOException e) {
            AppSync.puts("IOException "+e);
        }

        return arrayList;
    }

}
