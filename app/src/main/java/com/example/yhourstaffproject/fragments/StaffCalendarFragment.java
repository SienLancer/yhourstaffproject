package com.example.yhourstaffproject.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffCalendarFragment extends Fragment {
    private View mView;
    EditText ip_shift_et;
    Button add_shift_btn,cancel_btn;
    Dialog dialog;
    //CustomDialogFragment dialogFragment;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ValueEventListener listener;
    TextView Sun1,Sun2,Sun3,
            Mon1,Mon2,Mon3,
            Tue1,Tue2,Tue3,
            Wed1,Wed2,Wed3,
            Thu1,Thu2,Thu3,
            Fri1,Fri2,Fri3,
            Sat1,Sat2,Sat3;



    public StaffCalendarFragment() {
        // Required empty public constructor
    }


    public static StaffCalendarFragment newInstance(String param1, String param2) {
        StaffCalendarFragment fragment = new StaffCalendarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_staff_calendar, container, false);
        init();
        //dialogFragment = (CustomDialogFragment) getChildFragmentManager().findFragmentByTag("custom_popup_dialog");
//        CustomDialogFragment dialogFragment = (CustomDialogFragment) getTargetFragment();
//
//        ip_shift_et=dialogFragment.getEditText();
//        add_shift_btn =dialogFragment.getButton();

//        cancel_btn =dialogFragment.getView().findViewById(R.id.cancel_btn);
        dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_popup_dialog);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        ip_shift_et=dialog.findViewById(R.id.ip_shift_et);
        add_shift_btn =dialog.findViewById(R.id.add_shift_btn);
        cancel_btn =dialog.findViewById(R.id.cancel_btn);
        getDataTable();
        itemClick();




        return mView;
    }

    public void getDataTable(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                Mon1.setText(snapshot.child("Calendar").child("week1").child("mon1").getValue(String.class));
                Mon2.setText(snapshot.child("Calendar").child("week1").child("mon2").getValue(String.class));
                Mon3.setText(snapshot.child("Calendar").child("week1").child("mon3").getValue(String.class));
                Tue1.setText(snapshot.child("Calendar").child("week1").child("tue1").getValue(String.class));
                Tue2.setText(snapshot.child("Calendar").child("week1").child("tue2").getValue(String.class));
                Tue3.setText(snapshot.child("Calendar").child("week1").child("tue3").getValue(String.class));
                Wed1.setText(snapshot.child("Calendar").child("week1").child("wed1").getValue(String.class));
                Wed2.setText(snapshot.child("Calendar").child("week1").child("wed2").getValue(String.class));
                Wed3.setText(snapshot.child("Calendar").child("week1").child("wed3").getValue(String.class));
                Thu1.setText(snapshot.child("Calendar").child("week1").child("thu1").getValue(String.class));
                Thu2.setText(snapshot.child("Calendar").child("week1").child("thu2").getValue(String.class));
                Thu3.setText(snapshot.child("Calendar").child("week1").child("thu3").getValue(String.class));
                Fri1.setText(snapshot.child("Calendar").child("week1").child("fri1").getValue(String.class));
                Fri2.setText(snapshot.child("Calendar").child("week1").child("fri2").getValue(String.class));
                Fri3.setText(snapshot.child("Calendar").child("week1").child("fri3").getValue(String.class));
                Sat1.setText(snapshot.child("Calendar").child("week1").child("sat1").getValue(String.class));
                Sat2.setText(snapshot.child("Calendar").child("week1").child("sat2").getValue(String.class));
                Sat3.setText(snapshot.child("Calendar").child("week1").child("sat3").getValue(String.class));
                Sun1.setText(snapshot.child("Calendar").child("week1").child("sun1").getValue(String.class));
                Sun2.setText(snapshot.child("Calendar").child("week1").child("sun2").getValue(String.class));
                Sun3.setText(snapshot.child("Calendar").child("week1").child("sun3").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });



        }else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void itemClick() {
        Mon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Mon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Mon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Tue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Tue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Tue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Wed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Wed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Wed3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Thu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Thu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Thu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Fri1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Fri2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Fri3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sun1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sun2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });
        Sun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String dataItem = ip_shift_et.getText().toString();
                            firebaseDatabase.getReference().child("Calendar").child("week1").child("sun3").setValue(dataItem);
                            Toast.makeText(getContext(), "Data item added successfully", Toast.LENGTH_SHORT).show();



                        }else {
                            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    public void showDialog(){
        //CustomDialogFragment a = (CustomDialogFragment) getTargetFragment();
//        dialogFragment = new CustomDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("title", "Enter a name to select a shift");
//        dialogFragment.setArguments(args);
//        dialogFragment.show(getChildFragmentManager(), "dialog");


        dialog.show();


    }

    public void init(){
        Sun1=mView.findViewById(R.id.Sunday1);
        Sun2=mView.findViewById(R.id.Sunday2);
        Sun3=mView.findViewById(R.id.Sunday3);

        Mon1=mView.findViewById(R.id.Monday1);
        Mon2=mView.findViewById(R.id.Monday2);
        Mon3=mView.findViewById(R.id.Monday3);

        Tue1=mView.findViewById(R.id.Tuesday1);
        Tue2=mView.findViewById(R.id.Tuesday2);
        Tue3=mView.findViewById(R.id.Tuesday3);


        Wed1=mView.findViewById(R.id.Wednesday1);
        Wed2=mView.findViewById(R.id.Wednesday2);
        Wed3=mView.findViewById(R.id.Wednesday3);

        Thu1=mView.findViewById(R.id.Thursday1);
        Thu2=mView.findViewById(R.id.Thursday2);
        Thu3=mView.findViewById(R.id.Thursday3);

        Fri1=mView.findViewById(R.id.Friday1);
        Fri2=mView.findViewById(R.id.Friday2);
        Fri3=mView.findViewById(R.id.Friday3);

        Sat1=mView.findViewById(R.id.Saturday1);
        Sat2=mView.findViewById(R.id.Saturday2);
        Sat3=mView.findViewById(R.id.Saturday3);





    }


}