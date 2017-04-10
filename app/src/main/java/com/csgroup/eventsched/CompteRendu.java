package com.csgroup.eventsched;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CompteRendu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TextWatcher {


    /** Compteur de caractère **/
    private EditText status;
    private TextView nbCharTxt;

    /** For spinner Prat **/
    private  ArrayList<String> listItems=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Spinner sp;

    /** For spinner Medic **/
    private  ArrayList<String> listItems2=new ArrayList<>();
    private ArrayAdapter<String> adapter2;
    private Spinner sp2;

    /**Button ajout medic**/
    private Button Add;
    private Medics test = new Medics("","");

    private  ArrayList<String> listItems3=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte_rendu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** Nombre de caractère **/
        status = (EditText) findViewById(R.id.bilan);
        status.addTextChangedListener(this);
        nbCharTxt = (TextView) findViewById(R.id.indicator);
        nbCharTxt.setTextColor(Color.GREEN);

        Add = (Button) findViewById(R.id.add);

        /**Add medic in Listview **/
        // Construct the data source
        final ArrayList<Medics> medicslist = new ArrayList<Medics>();
// Create the adapter to convert the array to views
        final MedicAdapter adapter3 = new MedicAdapter(this, medicslist);
// Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.listview_medic);
        listView.setAdapter(adapter3);


        /** Instance the Spinner Praticien **/
        sp = (Spinner) findViewById(R.id.spinner);
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems);
        sp.setAdapter(adapter);
        sp.setPrompt("Selectionner un Praticien");

        /**Instance the Spinner Medic **/
        sp2 = (Spinner) findViewById(R.id.spinner_medic);
        adapter2=new ArrayAdapter<String>(this,R.layout.spinner_layout_medic,R.id.txt,listItems2);
        sp2.setAdapter(adapter2);
        sp2.setPrompt("Selectionner un Médicament");


        /** Ajout du medicament par son id dans la liste **/
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final Object item = parent.getItemAtPosition(pos);

                test.nom = (String) item;
                test.prix = listItems3.get((int) id);


                Add.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        adapter3.add(new Medics(test.nom,test.prix));
                        adapter3.notifyDataSetChanged();

                    }
                });
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    /**commande d'affichage du menu**/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    public void onStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }

    /** Activition des tâches en Background **/
    private class BackTask extends AsyncTask<Void,Void,Void> {
        ArrayList<String> list;
        ArrayList<String> list2;
        ArrayList<String> list3;


        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
            list2=new ArrayList<>();
            list3=new ArrayList<>();

        }

        /**
         * Connection for spinner
         **/
        protected Void doInBackground(Void... params) {
            InputStream is = null;
            String result = "";
            String result2 = "";
            String result3 = "";

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.3.2:80/eventsched/v1/spinner.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // Get our response as a String.
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                is.close();
                //result=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = jArray.getJSONObject(i);

                    /** add praticien in array list **/
                    String nomPrenom = jsonObject.getString("nom") + " " + jsonObject.getString("prenom");
                    list.add(nomPrenom);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /** For Spinner MEdic **/
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.3.2:80/eventsched/v1/medicament.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // Get our response as a String.
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result2 += line;
                }
                is.close();
                //result=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data
            try {
                JSONArray jArray = new JSONArray(result2);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = jArray.getJSONObject(i);

                    /** add medic in array list **/
                    String medic = jsonObject.getString("nom");
                    list2.add(medic);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /** For Spinner MEdic **/
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://10.0.3.2:80/eventsched/v1/medicament.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // Get our response as a String.
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }


            /** for price medic **/
            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result3 += line;
                }
                is.close();
                //result=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // parse json data
            try {
                JSONArray jArray = new JSONArray(result3);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonObject = jArray.getJSONObject(i);

                    /** add medic in array list **/
                    String medic = jsonObject.getString("prix");
                    list3.add(medic);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(Void result) {
            listItems.addAll(list);
            listItems2.addAll(list2);
            listItems3.addAll(list3);

            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        }
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
            finish();

        } else if (id == R.id.nav_cpt) {


        } else if (id == R.id.nav_medic) {

            Intent intent = new Intent(CompteRendu.this, Medicaments.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_loc) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
