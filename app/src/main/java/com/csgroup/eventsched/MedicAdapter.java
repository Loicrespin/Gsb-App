package com.csgroup.eventsched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by loicc on 08/04/2017.
 */

public class MedicAdapter extends ArrayAdapter<Medics> {

    private Object s;
    protected static int qty2;


    public MedicAdapter(Context context, ArrayList<Medics> medics){

        super(context, 0, medics);
    }

    public class Holder
    {
        Spinner spinner;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        //Get data item for this position
        final Medics medics = getItem(position);


        //Check data if an existing view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_medic, parent, false);
        }


        TextView tvnom = (TextView) convertView.findViewById(R.id.nommedic);
        TextView tvprix = (TextView) convertView.findViewById(R.id.prixmedic);

        tvnom.setText(medics.nom);
        tvprix.setText(medics.prix);

        final Holder holder = new Holder();

            holder.spinner = (Spinner) convertView.findViewById(R.id.qty);
            final List<String> itemList = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
            CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, itemList);

            holder.spinner.setAdapter(spinnerAdapter);

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position > 0)
                    {
                        CompteRendu.listeqty.add(Integer.valueOf(itemList.get(position)));
                        System.out.println(CompteRendu.listeqty);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    return;
                }

            });

                ImageButton clear = (ImageButton) convertView.findViewById(R.id.clear_medic);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CompteRendu.listeqty.isEmpty())
                {
                    CompteRendu.listeidmedic.remove(position);
                    remove(medics);

                } else {

                    CompteRendu.listeqty.remove(position);
                    CompteRendu.listeidmedic.remove(position);
                    remove(medics);
                }




            }
        });


        return convertView;


    }



}