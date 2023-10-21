package com.example.dictionnary_db;

import android.database.SQLException;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    EditText wordinput;
    Button find;
    ListView listviewofdefs;
    String word;
    DataAccessControlclass dataAccessControlclass;
    List<String> meaningsofword;
    ArrayAdapter<String> adapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordinput = findViewById(R.id.wordinput);
        find = findViewById(R.id.find);
        find.setEnabled(false);
        listviewofdefs = findViewById(R.id.listviewofdefs);

        find.setOnClickListener(v -> {
            word = wordinput.getText().toString();
            onclickFind(word);
        });


        listviewofdefs.setOnItemClickListener((parent, view, position, id) -> {
            if (dataAccessControlclass.isword == true) {

                String chosenword = parent.getItemAtPosition(position).toString();
                onclickFind(chosenword);
                wordinput.setText(chosenword);
                dataAccessControlclass.isword = false;

            }


        });

        TextWatcher textWatcher = new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                find.setEnabled(!wordinput.getText().toString().isEmpty());
                boolean v = wordinput.getText().toString().isEmpty();
                if (v) listviewofdefs.setVisibility(View.INVISIBLE);

                /*Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    public void run() {
                        //What you want to happen later
                        word = wordinput.getText().toString();
                        onclickfind (word);
                    }
                }, 100);

                 */

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //if (!hasFocus) {word = wordinput.getText().toString();
                //   onclickfind (word);}

                if (hasFocus) {
                    //set the row background to a different color
                    ((View) v.getParent()).setBackgroundColor(Color.parseColor("#EFF4F4"));
                }
                //onBlur

            }
        };


        wordinput.setOnFocusChangeListener(onFocusChangeListener);
        wordinput.addTextChangedListener(textWatcher);


    }

    public void onclickFind(String word) {

        dataAccessControlclass = new DataAccessControlclass(MainActivity.this);
        try {
            dataAccessControlclass.createDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {

            dataAccessControlclass.openDatabase();

        } catch (SQLException sqle) {

            throw sqle;

        }

        meaningsofword = dataAccessControlclass.find(word.trim());
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, meaningsofword);
        listviewofdefs.setAdapter(adapter);

        listviewofdefs.setVisibility(View.VISIBLE);


    }

    public void clear(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, null);
        }
    }
}



