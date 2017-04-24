package com.csgroup.eventsched;

/**
 * Created by loicc on 21/04/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

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

/** Classe pour ajouter echantillon **/
public class Drop_medic extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("medicament", (Medicaments.nomrecup)));
        System.out.println(nameValuePair.toString());

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://172.20.10.3:80/eventsched/v1/deleteMedic.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", "Client");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Ioexception", "Ioexption");
            e.printStackTrace();
        }

        return null;
    }

}