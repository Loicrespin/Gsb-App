package com.csgroup.eventsched;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Agenda extends MenuActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    TextView emailing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Bouton image de l'activity agenda pour l'ajout dévénement
        ImageButton ibNewEvent = (ImageButton) findViewById(R.id.ibNewEvent);
        ibNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Agenda.this, AddEventActivity.class);
                startActivity(intent);
            }
        });

        //Bouton image de l'activity agenda pour accès au calendrier
        ImageButton ibNewEvent2 = (ImageButton) findViewById(R.id.imageButtonCal);
        ibNewEvent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Agenda.this, Calendrier.class);
                startActivity(intent);

            }
        });


        // Activation des tâches en arrière plan pour récupérer les événement
        this.StartBackgroundService();

        //commande d'affichage du menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onPause();
    }


    private void StartBackgroundService() {
        Log.d("Agenda Activity: ", "service is running: " + RetrieveNewEventsService.isRunning );
        if (!RetrieveNewEventsService.isRunning) {
            Intent serviceIntent = new Intent(this, RetrieveNewEventsService.class);
            startService(serviceIntent);
        }
    }

    @Override
    public void onBackPressed() {

      final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_deco);

        Button oui = (Button) dialog.findViewById(R.id.oui);
        Button non = (Button) dialog.findViewById(R.id.non);

        oui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext(),null);
                preferencesManager.logout();
                dialog.dismiss();
                finish();
            }

        });

        // if button is clicked, close the custom dialog
        non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();


            }
        });

        //dialog.show();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String emailtete = settings.getString("silentmail", null);

        getMenuInflater().inflate(R.menu.agenda, menu);
        emailing = (TextView) findViewById(R.id.menuemail);
        emailing.setText(emailtete);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_coord) {

            Intent intent = new Intent(Agenda.this, CoordActivity.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_cpt) {

            Intent intent = new Intent(Agenda.this, CompteRendu.class);
            startActivity(intent);
            onPause();

        } else if (id == R.id.nav_medic) {

            Intent intent = new Intent(Agenda.this, Medicaments.class);
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

    //Detruire la task pour le logout
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferencesManager preferencesManager = new PreferencesManager(this,null);
        if (preferencesManager.getRememberLogin() == false) {
            // stop service and clear preferences
            preferencesManager.logout();
        }
    }
}
