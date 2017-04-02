package com.csgroup.eventsched;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CompteRendu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextWatcher {


    /** Compteur de caractère **/
    private EditText status;
    private TextView nbCharTxt;

    /**Pour la recherche **/
    Spinner spinner1;
    EditText e;
    InputStream is=null;
    String result=null;
    String line=null;
    HttpURLConnection urlConnection = null;

    String text;
    String [] city = {"No suggestion"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte_rendu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        final List<String> list1 = new ArrayList<String>();
        e = (EditText) findViewById(R.id.editText);
        Button b = (Button) findViewById(R.id.search);


        status = (EditText) findViewById(R.id.bilan);
        status.addTextChangedListener(this);
        nbCharTxt = (TextView) findViewById(R.id.cpt);
        nbCharTxt.setTextColor(Color.GREEN);


      Spinner spinner = (Spinner) findViewById(R.id.quantity);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.quantity_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        /**commande d'affichage du menu**/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//Toast.makeText(getApplicationContext(), "Invalid IP Address",Toast.LENGTH_LONG).show();

                text = e.getText().toString(); //Here i am storing the entered text in the string format in text variable

                ContentValues values = new ContentValues();
                values.put("1", text);  // This will append the entered text in the url for filter purpose


                try {

                    URL url = new URL("http://10.0.3.2:80/eventsched/v1/compterendu.php?string1="+text);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.connect();
                    is = urlConnection.getInputStream();
                }
                catch (Exception e)
                {
                    Log.e("Fail 1", e.toString());
                }

                try
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                }
                catch(Exception e)
                {
                    Log.e("Fail 2", e.toString());
                }


                try
                {
                    JSONArray JA=new JSONArray(result);
                    JSONObject json= null;

                    city = new String[JA.length()];


                    for(int i=0;i<JA.length();i++)
                    {
                        json=JA.getJSONObject(i);
                        city[i] = json.getString("nom");


                    }
//                    Toast.makeText(getApplicationContext(), "Data Loaded", Toast.LENGTH_LONG).show();

                    for(int i=0;i<city.length;i++)
                    {
                        list1.add(city[i]);

                    }

                    spinner_fn();

                }
                catch(Exception e)
                {

                    Log.e("Fail 3", e.toString());


                }


            }
        });

        }


    /** Changement du compteur de caractère **/
   @Override
    public void afterTextChanged(Editable editable) {
        int nbChar = status.getText().toString().length();
        int leftChar = 380 - nbChar;
        nbCharTxt.setText(Integer.toString(leftChar) + " caracteres restant");
        nbCharTxt.setTextColor(Color.GREEN);
        if (leftChar < 10 && leftChar >= 0)
            nbCharTxt.setTextColor(Color.YELLOW);
        else if (leftChar <= 0)
        {
            nbCharTxt.setTextColor(Color.RED);
            nbCharTxt.setText(Integer.toString(Math.abs(leftChar)) + " caracteres en trop");
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSeq, int arg1, int arg2,
                                  int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence charSeq, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.agenda, menu);
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

    /** Remplissage de spinner **/
    private void spinner_fn() {
// TODO Auto-generated method stub

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, city);

        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter1);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {

            Intent intent = new Intent(CompteRendu.this, Agenda.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_coord) {

            Intent intent = new Intent(CompteRendu.this, CoordActivity.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_cpt) {


        } else if (id == R.id.nav_medic) {

            Intent intent = new Intent(CompteRendu.this, Medicaments.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_loc) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
