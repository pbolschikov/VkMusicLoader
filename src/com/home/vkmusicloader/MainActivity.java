package com.home.vkmusicloader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String[] tracks = new String[]{
        		"������-�����",
        		"������",
        		"����-����",
        		"����-����",
        		"����-����",
        		"���������� �����",
        		"������, � �� ����������",
        		"��! �� �� ��� ��� ����� �����������?",
        		"������-�����",
        		"������",
        		"����-����",
        		"����-����",
        		"����-����",
        		"���������� �����",
        		"������, � �� ����������",
        		"��! �� �� ��� ��� ����� �����������?"
        };
        ListView tracksListView = (ListView)findViewById(R.id.track_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                R.layout.track_textview, tracks);
        tracksListView.setAdapter(adapter);
    }
}
