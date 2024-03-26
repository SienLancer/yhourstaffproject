package com.example.yhourstaffproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.yhourstaffproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class CustomDialogFragment extends DialogFragment {

    Button add_shift_btn, cancel_btn;
    EditText ip_shift_et;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

//    public CustomDialogFragment(Activity activity) {
//super();
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.custom_popup_dialog, container, false);

        // Get references to UI elements
        TextView title_dialog_tv = view.findViewById(R.id.title_dialog_tv);
         ip_shift_et=view.findViewById(R.id.ip_shift_et);
         add_shift_btn =view.findViewById(R.id.add_shift_btn);
         cancel_btn =view.findViewById(R.id.cancel_btn);

        // Set title based on arguments (optional)
        String title = getArguments().getString("title");
        if (title != null) {
            title_dialog_tv.setText(title);
        }

        // Handle button click
        add_shift_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        return view;
    }
    public Button getButton() {
        return add_shift_btn;
    }

    public EditText getEditText() {
        return ip_shift_et;
    }

    public void setDataItem(){
        FirebaseUser user = mAuth.getCurrentUser();
        String dataItem = ip_shift_et.getText().toString();
        if (user != null) {
            firebaseDatabase.getReference().child("Calendar").child("week1").child("mon1").setValue(dataItem);
            firebaseDatabase.getReference().child("Calendar").child("week1").child("mon2").setValue(dataItem);
            Toast.makeText(getContext(), "Data item added successfully", Toast.LENGTH_SHORT).show();



        }else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

}

