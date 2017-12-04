package org.timecrafters.analyticalengine.athena;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.hermes.AppSync;
import org.timecrafters.analyticalengine.hermes.TeamsListAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TeamsListCreatorActivity extends AppCompatActivity {
    final ArrayList<String> teamsList = new ArrayList<String>();

    HashMap<String, String> teamNumbers = new HashMap<String, String>();
    HashMap<String, String> teamNames   = new HashMap<String, String>();
    ArrayList countries          = new ArrayList<>();
    ArrayList states_provinces   = new ArrayList<>();
    public Animation animation;
    public int selectedRow = 0;
    public ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list_creator);

        megaParser(loadUltimateTeamsList());

        String[] teamNumberList = new String[teamNumbers.keySet().size()];
        String[] teamNameList   = new String[teamNumbers.values().size()];
        int _i = 0;
        for(String key : teamNumbers.keySet()) {
            teamNumberList[_i] = (String) key;
            teamNameList[_i] = (String) teamNumbers.get(key);
            _i++;
        }

        final ArrayAdapter<String> numberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, teamNumberList);
        final AutoCompleteTextView numberView = (AutoCompleteTextView) findViewById(R.id.team_number);
        numberView.setAdapter(numberAdapter);

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, teamNameList);
        final AutoCompleteTextView nameView = (AutoCompleteTextView) findViewById(R.id.team_name);
        nameView.setAdapter(nameAdapter);

        numberView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String team = teamNumbers.get((String) adapterView.getItemAtPosition(i));
                nameView.setText(team);
            }
        });

        nameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String team = teamNames.get((String) adapterView.getItemAtPosition(i));
                numberView.setText(team);
            }
        });

        numberView.setThreshold(1);
        nameView.setThreshold(1);

        final TeamsListAdapter listAdapter = new TeamsListAdapter(teamsList, this);
        listView = (ListView) findViewById(R.id.teams_list);
        listView.setAdapter(listAdapter);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.slide_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listAdapter.notifyDataSetChanged();
            }
        });

        final Button addButton = (Button) findViewById(R.id.add);
        final int addButtonColor = ((ColorDrawable) addButton.getBackground()).getColor();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberView.getText().length() > 0 && nameView.getText().length() > 0) {
                    boolean uniqueEntry = true;
                    String entry = "" + numberView.getText() + " " + nameView.getText();

                    for(int i = 0; i < teamsList.size(); i++) {
                        if (entry.equals(teamsList.get(i))) {
                            uniqueEntry = false;
                        }
                    }

                    if (uniqueEntry) {
                        teamsList.add(entry);
                        numberView.setText("");
                        nameView.setText("");
                        listAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        recalculateTeamsCount();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Team "+numberView.getText()+" already on list", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        Runnable pulseButton = new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addButton.setBackgroundColor(Color.RED);
                                        AppSync.puts("RUNNABLE", "RED");
                                        AppSync.puts("COLOR_RED", ""+((ColorDrawable) addButton.getBackground()).getColor());
                                    }
                                });
                                try {
                                    Thread.sleep(512);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AppSync.puts("RUNNABLE", "RAN");
                                            addButton.setBackgroundColor(addButtonColor);
                                            AppSync.puts("COLOR_GREEN", ""+((ColorDrawable) addButton.getBackground()).getColor());
                                        }
                                    });
                                } catch(InterruptedException e){AppSync.puts("ERROR_CLOCK", "AN error occurred during science class.");}
                            }
                        };
                        AsyncTask.execute(pulseButton);
//                      set button color to red for 1 second then fade back to normal color;
                    }
                }
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSync.puts("Am I Called?");
                cancelCreation();
            }
        });

        final Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCreation();
                // TODO: Save teams list to /TimeCraftersAnalyticalEngine/lists/#{name}.txt or to user provided directory.
            }
        });
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

        teamNumbers.put(array[0], teamString);
        teamNames.put(teamString, array[0]);
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

    private void recalculateTeamsCount() {
        final TextView teamsCount = (TextView) findViewById(R.id.team_count);
        teamsCount.setText(""+teamsList.size()+" Teams");
    }

    private void saveCreation() {
        if (teamsList.size() > 0) {
            Collections.sort(teamsList);
        } else {
            AppSync.createAlertDialog(this, "List Empty!", "Can not save an empty list.", new Runnable() {
                @Override
                public void run() {
                    // Do nothing.
                }
            });
        }
    }

    private void cancelCreation() {
        if (teamsList.size() > 0) {
            AppSync.createConfirmDialog(this, "Abort List Creation?", "All entered data with be lost.",
                new Runnable(){
                @Override
                public void run() {
                    finish();
                }},

                new Runnable() {
                @Override
                public void run() {

                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        cancelCreation();
//        super.onBackPressed();
    }
}
