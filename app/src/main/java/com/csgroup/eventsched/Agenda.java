package com.csgroup.eventsched;

import android.content.Intent;
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
import android.widget.ImageButton;

public class Agenda extends MenuActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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


        // start service that fetches new events and notifies user
        this.StartBackgroundService();

        //commande d'affichage du menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.agenda, menu);
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

        } else if (id == R.id.nav_medic) {

        } else if (id == R.id.nav_nav) {

        } else if (id == R.id.nav_loc) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
