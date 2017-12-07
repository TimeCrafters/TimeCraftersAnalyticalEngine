package org.timecrafters.analyticalengine.athena;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.timecrafters.analyticalengine.MainActivity;
import org.timecrafters.analyticalengine.R;
import org.timecrafters.analyticalengine.hermes.AppSync;
import org.timecrafters.analyticalengine.hermes.TeamsListAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
                                    }
                                });
                                try {
                                    Thread.sleep(512);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            addButton.setBackgroundColor(addButtonColor);
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

        if (AppSync.setListMode == 0) {
        } else if (AppSync.setListMode == 1) {
            for (int key : AppSync.teamsList.keySet()) {
                teamsList.add(""+ key +" "+ AppSync.teamsList.get(key));
            }
        }
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
            final String path = Environment.getExternalStorageDirectory().toString() + File.separator + AppSync.defaultFolderPath + File.separator + "lists";
            final AppCompatActivity localActivity = this;

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Name List").setMessage("Save path: "+path);
            final EditText input = new EditText(this);
            if (AppSync.setListMode == 1) {
                try {
                    String list = AppSync.getConfig().getString("last_used_teams_list");
                    if (!list.equals("")) {
                        try {
                            String[] s1 = list.split(""+File.separator);
                            String[] s2 = s1[s1.length-1].split(".txt");
                            input.setText(s2[s2.length-1]);
                        } catch (SecurityException error) {
                            AppSync.puts("LIST", "Failed to load list.\n"+error.getMessage());
                        }
                    }
                } catch (JSONException error) {/* Fault */}
            }
            input.setTextColor(Color.BLACK);
            alert.setView(input);

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (AppSync.lastInputText != null) {
                        AppSync.puts("DIALOG", "lastInputText: lastInputText is null!");
                    }
                    if (input.length() > 0) {
                        File file = new File(path +File.separator+ AppSync.lastInputText +".txt");
                        boolean abort = false;
                        if (file.exists()) {
                            abort = true;
                        }

                        if (!abort) {
                            saveList(path,input.getText().toString() +".txt");
                        } else {
                            AppSync.createConfirmDialog(localActivity, "File already exists!", "Overwrite?", new Runnable() {
                                @Override
                                public void run() {
                                    saveList(path,input.getText().toString() +".txt");
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    // cancelled
                                }
                            }, "Overwrite", "Cancel");
                        }

                    } else {
                        AppSync.createAlertDialog(localActivity, "Name is empty!", "Can't save file without a name.", new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                }
            }
            });

            alert.show();
        } else {
            AppSync.createAlertDialog(this, "List Empty!", "Can not save an empty list.", new Runnable() {
                @Override
                public void run() {
                    // Do nothing.
                }
            });
        }
    }

    private void saveList(String path, String filename) {
        AppSync.createDirectory(path);
        File file = new File(path +File.separator+ filename);
        BufferedWriter bw = null;
        boolean failure = false;
        boolean writeSuccess = false;

        AppSync.puts("saveList","Pending");

        try {
            try {
                bw = new BufferedWriter(new FileWriter(file.getPath(), false));
            } catch (IOException error) {
                failure = true;
                AppSync.puts("ERROR", error.toString());
            }

            if (!failure) {
                for (int i = 0; i < teamsList.size(); i++) {
                    AppSync.puts("saveList", "Writing: "+teamsList.get(i));
                    bw.write(teamsList.get(i));
                    bw.newLine();
                    bw.flush();
                }
                writeSuccess = true;
                // Set active teams list
                MainActivity.MainActivityContext.parseTeamsList(Uri.fromFile(file), false);
                AppSync.puts("saveList", "Success");
            } else {
                AppSync.puts("saveListERROR", "WRITE ERROR | WRITE ERROR");
            }
        } catch (IOException error) {
            // Eating fish and chips, bbl.
            AppSync.puts("saveListERROR", error.toString());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException error) {
                    AppSync.puts("saveListERROR", "Ugg.");
                }
            }
        }
        if (writeSuccess) {
            finish();
        } else {
            AppSync.createAlertDialog(this, "Failed to save!", "Try again.", new Runnable() {
                @Override
                public void run() {

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
