package com.alokomkar.wordoccurence;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView occurenceRecyclerView;
    private int REQUEST_CODE_SEARCH = 1000;
    private String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, WordModel> wordCountMap;
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

        if( FileUtils.checkSelfPermission(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE})) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            startActivityForResult(Intent.createChooser(intent,
                    "Load a file from directory"), REQUEST_CODE_SEARCH);
        }
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
            wordCountMap.clear();
            Uri uri = data.getData();
            if( uri != null ) {
                Log.d(TAG, "File Uri : " +  uri.getEncodedPath() + " Path "+ uri.getPath());
                String filepath = FileUtils.getPath(MainActivity.this, uri);
                executeTask(filepath);
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void executeTask(final String filepath) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(TAG, "File path : " + filepath);
                InputStream fis = null;
                try {
                    fis = new FileInputStream(filepath);
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    String completeLine = "";
                    while ((line = br.readLine()) != null) {
                        completeLine += line.trim() + " ";
                    }
                    calculateWordCount( completeLine );
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                if( wordCountMap.size() > 0 ) {
                    wordCountMap = FileUtils.sortByValue(wordCountMap);
                    Log.d(TAG, "Word Count Sorted : " + wordCountMap);
                    setupRecyclerView();
                }
            }
        }.execute();
    }

    private void setupRecyclerView() {
        occurenceRecyclerView.setAdapter(new OccurrenceRecyclerAdapter(wordCountMap));
        progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FileUtils.PERMISSION_REQUEST) {
            if (FileUtils.checkDeniedPermissions(MainActivity.this, permissions).length == 0) {
                openFileIntent();
            } else {
                if (permissions.length == 3) {
                    Toast.makeText(MainActivity.this, "Some permissions were denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private void calculateWordCount( String sentence ) {

        String completeSentence = sentence;
        List<String> list = Arrays.asList(completeSentence.split(" "));
        Set<String> uniqueWords = new HashSet<String>(list);
        for (String word : uniqueWords) {
            wordCountMap.put(word, new WordModel(word, Collections.frequency(list, word)));
            Log.d(TAG, "Collections : " + word + ": " + Collections.frequency(list, word));
        }
    }
}
