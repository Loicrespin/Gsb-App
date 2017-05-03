package com.csgroup.eventsched;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.NavigationView;
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
import android.widget.TabHost;
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

import static com.csgroup.eventsched.R.id.tabHost;

public class Medicaments extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    //For the search
    SearchView searchView = null;

    Button makecall;
    TextView ID = null;
    TextView NOM = null;
    TextView FAMILLE = null;
    TextView COMPOSITION = null;
    TextView EFFET = null;
    TextView CONTRE = null;
    TextView PRIX = null;

    //TRANSFERT DE DONNEES
    protected static String nomrecup = "";
    String famillerecup;
    String compositionrecup;
    String effetrecup;
    String contrerecup;
    String prixrecup;

    Button supp;

    private SimpleCursorAdapter myAdapter;
    private String[] strArrData = {"No Suggestions"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicaments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Element pour le search
        final String[] from = new String[]{"nom"};
        final int[] to = new int[]{android.R.id.text1};

        final TabHost host = (TabHost) findViewById(tabHost);
        host.setup();

        //Element pour la carte de visite
        NOM = (TextView) findViewById(R.id.nomm);
        FAMILLE = (TextView) findViewById(R.id.famille);
        COMPOSITION = (TextView) findViewById(R.id.compo);
        EFFET = (TextView) findViewById(R.id.effet);
        CONTRE = (TextView) findViewById(R.id.contre);
        PRIX = (TextView) findViewById(R.id.prix);

        // setup SimpleCursorAdapter
        myAdapter = new SimpleCursorAdapter(Medicaments.this, android.R.layout.simple_spinner_dropdown_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Fetch data from mysql table using AsyncTask
        new AsyncFetch().execute();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Composition");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Effets indésirables");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Contre indication");
        host.addTab(spec);

        supp = (Button) findViewById(R.id.suppress);

        setTabColor(host);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(host);
            }
        });


        supp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(nomrecup == "")
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Veuillez chercher un médicament !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }

                else {

                    new Drop_medic().execute();

                    Context context = getApplicationContext();
                    CharSequence text = "Médicament supprimé !";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    NOM.setText("");
                    FAMILLE.setText("");
                    COMPOSITION.setText("");
                    EFFET.setText("");
                    CONTRE.setText("");
                    PRIX.setText("");

                }

            }

        });

                //commande d'affichage du menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public static void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.tabwidget); // unselected
        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.drawable.tabwidgetselected); // selected

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            onPause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.coord, menu);

        // Get Search item from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) Medicaments.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(Medicaments.this.getComponentName()));
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
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("nom")), false);

                    NOM.setText(nomrecup);
                    FAMILLE.setText(famillerecup);
                    COMPOSITION.setText(compositionrecup);
                    EFFET.setText(effetrecup);
                    CONTRE.setText(contrerecup);
                    PRIX.setText(prixrecup);

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
                    final MatrixCursor mc = new MatrixCursor(new String[]{BaseColumns._ID, "nom"});

                    for (int i = 0; i < strArrData.length; i++) {
                        if (strArrData[i].toLowerCase().startsWith(s.toLowerCase())) {
                            mc.addRow(new Object[]{i, strArrData[i]});
                        }
                    }

                    for (int i = 0; i < strArrData.length; i++) {
                        if (strArrData[i].toLowerCase().startsWith(s.toLowerCase())) {

                            //récupération du nom
                            for (int j = 0; j <= i; j++) {
                                nomrecup = strArrData[j];
                            }

                            //récupération du prénom
                            for (int j = 0; j <= i + 1; j++) {
                                famillerecup = strArrData[j];
                            }

                            //récupération de l'adresse
                            for (int j = 0; j <= i + 2; j++) {
                                compositionrecup = strArrData[j];
                            }

                            //récupération de la ville
                            for (int j = 0; j <= i + 3; j++) {
                                effetrecup = strArrData[j];
                            }

                            //récupération du port
                            for (int j = 0; j <= i + 4; j++) {
                                contrerecup = strArrData[j];
                            }

                            //récupération du tel
                            for (int j = 0; j <= i + 5; j++) {
                                prixrecup = strArrData[j];
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {

            Intent intent = new Intent(Medicaments.this, Agenda.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_coord) {

            Intent intent = new Intent(Medicaments.this, CoordActivity.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_cpt) {

            Intent intent = new Intent(Medicaments.this, CompteRendu.class);
            startActivity(intent);
            onPause();


        } else if (id == R.id.nav_medic) {


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

        ProgressDialog pdLoading = new ProgressDialog(Medicaments.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("http://lcworks.ovh/eventsched/v1/medicament.php");

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
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            ArrayList<String> dataList = new ArrayList<String>();
            pdLoading.dismiss();


            if (result.equals("no rows")) {

                // Do some action if no data from database

            } else {

                try {

                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        dataList.add(json_data.getString("id"));
                        dataList.add(json_data.getString("nom"));
                        dataList.add(json_data.getString("famille"));
                        dataList.add(json_data.getString("composition"));
                        dataList.add(json_data.getString("effet_indesirable"));
                        dataList.add(json_data.getString("contre_indication"));
                        dataList.add(json_data.getString("prix"));
                    }


                    strArrData = dataList.toArray(new String[dataList.size()]);


                } catch (JSONException e10) {
                    // You to understand what actually error is and handle it appropriately
                    e10.printStackTrace();
                    Toast.makeText(Medicaments.this, e10.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(Medicaments.this, result.toString(), Toast.LENGTH_LONG).show();
                }

            }


        }

    }

}

