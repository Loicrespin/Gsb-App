package com.csgroup.eventsched;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class EventDetailsActivity extends MenuActivity {

    private Event mEvent;
    private TextView tvTitle, tvDate, tvTime,
            tvDuration, tvLocation, tvBody;

    private String event;
    private int id_event;

    private Switch doneSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        tvTitle = (TextView) findViewById(R.id.tvEventdetailsTitle);
        tvDate = (TextView) findViewById(R.id.tvEventdetailsDate);
        tvTime = (TextView) findViewById(R.id.tvEventdetailsTime);
        tvDuration = (TextView) findViewById(R.id.tvEventdetailsDuration);
        tvLocation = (TextView) findViewById(R.id.tvEventdetailsLocation);
        tvBody = (TextView) findViewById(R.id.tvEventdetailsBody);

        doneSwitch = (Switch) findViewById(R.id.done);


        doneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){


                    postDeleteComment(getApplicationContext(), id_event);
                    postDeleteEvent(getApplicationContext(), event);

                    Context context = getApplicationContext();
                    CharSequence text = "Rendez-vous supprimé";
                    int duration = Toast.LENGTH_SHORT;
                    finish();

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }else{

                }

            }
        });

        Intent intent = getIntent();
        mEvent = intent.getParcelableExtra("event");
        this.setEventDetails();
        onStop();
    }

    private void setEventDetails() {
        event = mEvent.getTitle();
        id_event = mEvent.getId();
        tvTitle.setText(this.mEvent.getTitle());
        tvDate.setText(this.mEvent.getDateString());
        tvTime.setText(this.mEvent.getTimeString());
        // TODO find better formatting for duration
        tvDuration.setText(Integer.toString(this.mEvent.getDuration()) + " Minutes");
        tvLocation.setText(this.mEvent.getLocation());
        tvBody.setText(this.mEvent.getDetails());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_event_details, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public Event getEvent() {
        return mEvent;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /** Classe pour ajouter echantillon **/
    public static void postDeleteEvent(Context context, final String event){


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,"http://lcworks.ovh/eventsched/v1/deleteEvent.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("event", event);

                System.out.println(params);
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(sr);
    }

    /** Classe pour supprimer commentaires **/
    public static void postDeleteComment(Context context, final int event_id){


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST,"http://lcworks.ovh/eventsched/v1/deleteComment.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("id", String.valueOf(event_id));

                System.out.println(params);
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(sr);
    }

}
