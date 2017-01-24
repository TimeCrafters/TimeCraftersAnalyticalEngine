package org.timecrafters.ftcscouting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoutMatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_match);

        final Button teamSelection = (Button) findViewById(R.id.team_selection);

        final Button teamsListButton = (Button) findViewById(R.id.team_selection);
        teamsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScoutMatchActivity.this, teamsListButton);

                for(HashMap.Entry<Integer, String> entry : MainActivity.MainActivityContext.teamList.entrySet()) {
                    popupMenu.getMenu().add(""+entry.getKey()+" | "+entry.getValue());
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        teamSelection.setText(item.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }
}
