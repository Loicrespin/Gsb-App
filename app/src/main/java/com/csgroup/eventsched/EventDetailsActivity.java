package com.csgroup.eventsched;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class EventDetailsActivity extends MenuActivity {

    private Event mEvent;
    private TextView tvTitle, tvDate, tvTime,
            tvDuration, tvLocation, tvBody;
    private String event;

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



                    new Drop().execute();

                    Context context = getApplicationContext();
                    CharSequence text = "Rendez-vous supprimé";
                    int duration = Toast.LENGTH_LONG;
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
    class Drop extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            List<NameValuePair> nameValuePair= new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("event",(event)));
            System.out.println(nameValuePair.toString());

            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://172.20.10.3:80/eventsched/v1/deleteEvent.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException","Client");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Ioexception","Ioexption");
                e.printStackTrace();
            }

            return null;
        }


    }

}
