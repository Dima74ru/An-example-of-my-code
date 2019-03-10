package com.karbox.carspeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class adapter_item_model extends ArrayAdapter<item_model> {
    private Context context;
    private List<item_model> items_model;

    public adapter_item_model(Context context, List<item_model> items_model){
        super(context, R.layout.model,items_model);
        this.context = context;
        this.items_model = items_model;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                );

        final item_model item = items_model.get(position);

        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate( R.layout.model,parent,false );

        final TextView text_label = (TextView) view.findViewById( R.id.text_label );
        TextView text_info = (TextView) view.findViewById( R.id.text_info );
        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switch1);
        final EditText editText = (EditText) view.findViewById(R.id.edit1);

        text_label.setText(item.getText_label());
        text_info.setText(item.getText_info());

        if(item.getMode_model()) {
            editText.setVisibility(View.GONE);

            switchCompat.setChecked(item.getFlg_switch());

            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        item.setFlg_switch(true);
                    } else {
                        item.setFlg_switch(false);
                    }
                }
            });
            switchCompat.setChecked(item.getFlg_switch());

        } else
        {
            switchCompat.setVisibility(View.GONE);
            editText.setHint(item.getText_edit());


            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER))
                    {
                        item.setText_edit(String.valueOf(editText.getText()));
                        editText.setText(item.getText_edit());
                        return true;
                    }
                    return false;
                }
            });
        }

        return view;
    }

}
