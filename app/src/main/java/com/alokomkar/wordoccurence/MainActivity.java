package com.alokomkar.wordoccurence;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView occurenceRecyclerView;
    private int REQUEST_CODE_SEARCH = 1000;
    private String TAG = MainActivity.class.getSimpleName();
    private Map<String, WordModel> wordCountMap;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));

        wordCountMap = new HashMap<>();
        occurenceRecyclerView = (RecyclerView) findViewById(R.id.occurenceRecyclerView);
        occurenceRecyclerView.setLayoutManager( new LinearLayoutManager(MainActivity.this) );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileIntent();
            }
        });
    }

    private void openFileIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent,
                "Load a file from directory"), REQUEST_CODE_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK ) {
            try {
                progressDialog.show();
                wordCountMap.clear();
                Uri uri = data.getData();
                if( uri != null ) {
                    Log.d(TAG, "File Uri : " +  uri.getEncodedPath() + " Path "+ uri.getPath());
                    String filepath = FileUtils.getPath(MainActivity.this, uri);
                    Log.d(TAG, "File path : " + filepath);
                    File file = new File(filepath);
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        Log.d(TAG, "Reading line : " + line);
                        calculateWordCount( line );
                    }
                    if( wordCountMap.size() > 0 ) {
                        wordCountMap = FileUtils.sortByValue(wordCountMap);
                        setupRecyclerView();
                    }
                }
                else {
                    progressDialog.dismiss();
                }
                // Rest of code that converts txt file's content into arraylist
            } catch (IOException e) {
                // Codes that handles IOException
                progressDialog.dismiss();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupRecyclerView() {
        occurenceRecyclerView.setAdapter(new OccurrenceRecyclerAdapter(wordCountMap));
        progressDialog.dismiss();
    }



    private void calculateWordCount( String sentence ) {

        String[] keyArray = sentence.split(" ");
        for( String key : keyArray ) {
            int count = 0;
            while(sentence.contains(key)){
                count++;
                sentence = sentence.substring(sentence.indexOf(key)+key.length());
            }
            if( count != 0 ) {
                wordCountMap.put(key, new WordModel(key, count));
            }
        }
    }
}
