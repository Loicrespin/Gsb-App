package com.csgroup.eventsched;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class CoordActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    //For the search
    SearchView searchView = null;

    Button contact;
    Button navigation;
    final Context context = this;

    //VISIT CARD
    TextView NOM = null;
    TextView PRENOM = null;
    TextView ADRESSE = null;
    TextView VILLE = null;
    TextView PORT = null;
    TextView TEL = null;
    TextView MAIL = null;
    TextView COEFF = null;
    TextView LIEUX = null;

    //TRANSFERT DE DONNEES
    String nomrecup;
    String prenomrecup;
    String adresserecup = "";
    String villerecup = "";
    String portrecup = "";
    String telrecup = "";
    String mailrecup = "";
    String coeffrecup;
    String lieurecup;

    private SimpleCursorAdapter myAdapter;
    private String[] strArrData = {"No Suggestions"};
    private String[] search = {"No Suggestions"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coord);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Element pour le search
        final String[] from = new String[]{"id"};
        final int[] to = new int[]{android.R.id.text1};

        //Element pour la carte de visite
        NOM = (TextView) findViewById(R.id.nom);
        PRENOM = (TextView) findViewById(R.id.prenom);
        ADRESSE = (TextView) findViewById(R.id.adresse);
        VILLE = (TextView) findViewById(R.id.ville);
        PORT = (TextView) findViewById(R.id.port);
        TEL = (TextView) findViewById(R.id.tel);
        MAIL = (TextView) findViewById(R.id.mail);
        COEFF = (TextView) findViewById(R.id.coeff);
        LIEUX = (TextView) findViewById(R.id.lieux);

        contact = (Button) findViewById(R.id.contact);
        navigation = (Button) findViewById(R.id.naviagte);

        // setup SimpleCursorAdapter
        myAdapter = new SimpleCursorAdapter(CoordActivity.this, android.R.layout.simple_spinner_dropdown_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Fetch data from mysql table using AsyncTask
        new AsyncFetch().execute();

        //commande d'affichage du menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Intent intent = new Intent(Intent.ACTION_CALL);

        contact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_contact);

                ImageButton newcall = (ImageButton) dialog.findViewById(R.id.phoner);
                final ImageButton newmail = (ImageButton) dialog.findViewById(R.id.mail);
                final ImageButton newsms = (ImageButton) dialog.findViewById(R.id.sms);

                /** MAke a call **/
                newcall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (telrecup == "") {
                            Toast.makeText(context, "Veuillez chercher un contact", Toast.LENGTH_SHORT).show();

                        } else {

                            intent.setData(Uri.parse("tel:" + telrecup));
                            startActivity(intent);

                        }
                    }

                });

                newmail.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {

                                                   if (mailrecup == "") {

                                                       Toast.makeText(context, "Veuillez chercher un contact", Toast.LENGTH_SHORT).show();

                                                   } else {

                                                       Intent i = new Intent(Intent.ACTION_SEND);
                                                       i.setType("message/rfc822");
                                                       i.putExtra(Intent.EXTRA_EMAIL, new String[]{mailrecup});
                                                       i.putExtra(Intent.EXTRA_SUBJECT, "Compte rendu");
                                                       i.putExtra(Intent.EXTRA_TEXT, "");
                                                       try {
                                                           startActivity(Intent.createChooser(i, "Send mail..."));
                                                       } catch (android.content.ActivityNotFoundException ex) {
                                                           Toast.makeText(CoordActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                                       }

                                                   }

                                               }

                                           });

                newsms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (portrecup == "") {

                            Toast.makeText(context, "Veuillez chercher un contact", Toast.LENGTH_SHORT).show();

                        } else {

                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.putExtra("address", portrecup);
                            sendIntent.setData(Uri.parse("sms:"));
                            startActivity(sendIntent);

                        }

                    }

                });


                Button dialogButton = (Button) dialog.findViewById(R.id.cancel);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        /** MAke a naviagtion **/
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adresserecup == "" && villerecup == "") {

                    Toast.makeText(context, "Veuillez chercher un contact", Toast.LENGTH_SHORT).show();

                } else {

                    String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + adresserecup + " " + villerecup);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);
                    onPause();

                }


            }

        });

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
        getMenuInflater().inflate(R.menu.coord, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) CoordActivity.this.getSystemService(Context.SEARCH_SERVICE);


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(CoordActivity.this.getComponentName()));
            searchView.setIconified(false);
            searchView.setSuggestionsAdapter(myAdapter);


            // Getting selected (CLICKED) item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

                @Override
                public boolean onSuggestionClick(int position) {
                    // Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("id")),false);

                    NOM.setText(nomrecup);
                    PRENOM.setText(prenomrecup);
                    ADRESSE.setText(adresserecup);
                    VILLE.setText(villerecup);
                    PORT.setText(portrecup);
                    TEL.setText(telrecup);
                    MAIL.setText(mailrecup);
                    COEFF.setText(coeffrecup);
                    LIEUX.setText(lieurecup);


                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    // Filter data
                    final MatrixCursor mc = new MatrixCursor(new String[]{BaseColumns._ID, "id"});

                    for (int i=0; i<search.length; i++) {
                        if (search[i].toLowerCase().startsWith(s.toLowerCase())) {
                            mc.addRow(new Object[]{i, search[i]});
                        }
                    }

                    for (int i=0; i<strArrData.length; i++) {
                        if (strArrData[i].toLowerCase().startsWith(s.toLowerCase())) {

                            //récupération du nom
                            for (int j = 0; j <= i; j++)
                            {
                                nomrecup = strArrData[j];
                            }

                            //récupération du prénom
                            for (int j = 0; j <= i+1; j++)
                            {
                                prenomrecup = strArrData[j];
                            }

                            //récupération de l'adresse
                            for (int j = 0; j <= i+2; j++)
                            {
                                adresserecup = strArrData[j];
                            }

                            //récupération de la ville
                            for (int j = 0; j <= i+3; j++)
                            {
                                villerecup = strArrData[j];
                            }

                            //récupération du port
                            for (int j = 0; j <= i+4; j++)
                            {
                                portrecup = strArrData[j];
                            }

                            //récupération du tel
                            for (int j = 0; j <= i+5; j++)
                            {
                                telrecup = strArrData[j];
                            }

                            for (int j = 0; j <= i+6; j++){

                                mailrecup = strArrData[j];
                            }

                            //Recupération du coeff
                            for (int j = 0; j <= i+7; j++)
                            {
                                coeffrecup = strArrData[j];
                            }

                            //Récupération du lieux
                            for (int j = 0; j <= i+8; j++)
                            {
                                lieurecup = strArrData[j];
                            }


                            break;

                        }



                    }

                    myAdapter.changeCursor(mc);
                    return false;
                }
            });
        }

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

    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            // User entered text and pressed search button. Perform task ex: fetching data from database and display

        }
    }

    //Navigation menu slider
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {

            Intent intent = new Intent(CoordActivity.this, Agenda.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_coord) {

        } else if (id == R.id.nav_cpt) {

            Intent intent = new Intent(CoordActivity.this, CompteRendu.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_medic) {

            Intent intent = new Intent(CoordActivity.this, Medicaments.class);
            startActivity(intent);
            onPause();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(CoordActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tChargement...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("http://10.0.2.2:80/eventsched/v1/spinner.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we receive data
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        /** Récupéreratio des données pour les praticiens **/
        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            ArrayList<String> dataList = new ArrayList<String>();
            ArrayList<String> dataList2 = new ArrayList<String>();
            pdLoading.dismiss();


            if(result.equals("no rows")) {

                // Do some action if no data from database

            }else{

                try {

                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json_data = jArray.getJSONObject(i);
                        dataList.add(json_data.getString("id"));
                        dataList.add(json_data.getString("nom"));
                        dataList2.add(json_data.getString("nom"));
                        dataList.add(json_data.getString("prenom"));
                        dataList2.add(json_data.getString("prenom"));
                        dataList.add(json_data.getString("adresse"));
                        dataList.add(json_data.getString("ville"));
                        dataList.add(json_data.getString("port"));
                        dataList.add(json_data.getString("tel"));
                        dataList.add(json_data.getString("mail"));
                        dataList.add(json_data.getString("coeff"));
                        dataList.add(json_data.getString("lieux"));
                    }


                    strArrData = dataList.toArray(new String[dataList.size()]);
                    search = dataList2.toArray(new String[dataList2.size()]);


                } catch (JSONException e10) {
                    // You to understand what actually error is and handle it appropriately
                    e10.printStackTrace();
                    Toast.makeText(CoordActivity.this, e10.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(CoordActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                }

            }


        }

    }
}

