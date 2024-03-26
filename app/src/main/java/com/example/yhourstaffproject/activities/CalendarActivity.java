package com.example.yhourstaffproject.activities;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourstaffproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CalendarActivity extends AppCompatActivity {
    EditText ip_shift_et;
    Button add_shift_btn,cancel_btn;
    TextView realtime_table;
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
            Sat1,Sat2,Sat3,
            morningSstart, morningSend, afternoonSstart, afternoonSend, eveningSstart, eveningSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();

        dialog=new Dialog(CalendarActivity.this);
        dialog.setContentView(R.layout.custom_popup_dialog);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        ip_shift_et=dialog.findViewById(R.id.ip_shift_et);
        add_shift_btn =dialog.findViewById(R.id.add_shift_btn);
        cancel_btn =dialog.findViewById(R.id.cancel_btn);
        getDataTable();
        itemClick();

    }



    public void getDataTable(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");
                        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Lấy tất cả các tuần
                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                DataSnapshot lastWeekSnapshot = null;

                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                for (DataSnapshot weekSnapshot : weeks) {
                                    lastWeekSnapshot = weekSnapshot;
                                }

                                if (lastWeekSnapshot != null) {
                                    // Hiển thị dữ liệu từ tuần cuối cùng lên giao diện người dùng
                                    // Lấy dữ liệu từ tuần cuối cùng và hiển thị lên giao diện
                                    realtime_table.setText(lastWeekSnapshot.child("startDay").getValue(String.class));
                                    Mon1.setText(lastWeekSnapshot.child("mon1").getValue(String.class));
                                    Mon2.setText(lastWeekSnapshot.child("mon2").getValue(String.class));
                                    Mon3.setText(lastWeekSnapshot.child("mon3").getValue(String.class));
                                    Tue1.setText(lastWeekSnapshot.child("tue1").getValue(String.class));

                                    Tue2.setText(lastWeekSnapshot.child("tue2").getValue(String.class));
                                    Tue3.setText(lastWeekSnapshot.child("tue3").getValue(String.class));

                                    Wed1.setText(lastWeekSnapshot.child("wed1").getValue(String.class));

                                    Wed2.setText(lastWeekSnapshot.child("wed2").getValue(String.class));
                                    Wed3.setText(lastWeekSnapshot.child("wed3").getValue(String.class));
                                    Thu1.setText(lastWeekSnapshot.child("thu1").getValue(String.class));
                                    Thu2.setText(lastWeekSnapshot.child("thu2").getValue(String.class));
                                    Thu3.setText(lastWeekSnapshot.child("thu3").getValue(String.class));

                                    Fri1.setText(lastWeekSnapshot.child("fri1").getValue(String.class));
                                    Fri2.setText(lastWeekSnapshot.child("fri2").getValue(String.class));

                                    Fri3.setText(lastWeekSnapshot.child("fri3").getValue(String.class));
                                    Sat1.setText(lastWeekSnapshot.child("sat1").getValue(String.class));
                                    Sat2.setText(lastWeekSnapshot.child("sat2").getValue(String.class));
                                    Sat3.setText(lastWeekSnapshot.child("sat3").getValue(String.class));
                                    Sun1.setText(lastWeekSnapshot.child("sun1").getValue(String.class));
                                    Sun2.setText(lastWeekSnapshot.child("sun2").getValue(String.class));
                                    Sun3.setText(lastWeekSnapshot.child("sun3").getValue(String.class));
                                    morningSstart.setText(lastWeekSnapshot.child("morningSstart").getValue(String.class));
                                    morningSend.setText(lastWeekSnapshot.child("morningSend").getValue(String.class));
                                    afternoonSstart.setText(lastWeekSnapshot.child("afternoonSstart").getValue(String.class));
                                    afternoonSend.setText(lastWeekSnapshot.child("afternoonSend").getValue(String.class));
                                    eveningSstart.setText(lastWeekSnapshot.child("eveningSstart").getValue(String.class));
                                    eveningSend.setText(lastWeekSnapshot.child("eveningSend").getValue(String.class));


                                    // Tiếp tục với các TextView khác tương tự
                                    // ...
                                } else {
                                    Toast.makeText(CalendarActivity.this, "No weeks found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(CalendarActivity.this, "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
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
                dialog.show();
                ip_shift_et.setText(Fri2.getText().toString());

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
//        Sun3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//
//                add_shift_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    String userId = user.getUid();
//                                    String dataItem = ip_shift_et.getText().toString();
//                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
//                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("sun3").setValue(dataItem);
//                                    dialog.dismiss();
//                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }else {
//                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//            }
//        });

        Sun3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị dialog để nhập dữ liệu
                dialog.show();
                ip_shift_et.setText(Sun3.getText().toString());

                // Xử lý sự kiện khi người dùng nhấn vào nút add_shift_btn trong dialog
                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            String dataItem = ip_shift_et.getText().toString();
                            DatabaseReference userRef = firebaseDatabase.getReference().child("User").child(userId).child("shopID");
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String ownerShopId = snapshot.getValue(String.class);
                                    if (ownerShopId != null) {
                                        DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar");

                                        shopRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                // Lấy tất cả các tuần
                                                Iterable<DataSnapshot> weeks = dataSnapshot.getChildren();
                                                DataSnapshot lastWeekSnapshot = null;

                                                // Lặp qua tất cả các tuần và lưu lại tuần cuối cùng
                                                for (DataSnapshot weekSnapshot : weeks) {
                                                    lastWeekSnapshot = weekSnapshot;
                                                }

                                                if (lastWeekSnapshot != null) {
                                                    // Cập nhật dữ liệu của Sun3 trong tuần cuối cùng
                                                    DatabaseReference sun3Ref = lastWeekSnapshot.child("sun3").getRef();
                                                    sun3Ref.setValue(dataItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                dialog.dismiss();
                                                                Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                                Sun3.setText(dataItem);
                                                            } else {
                                                                Toast.makeText(CalendarActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(CalendarActivity.this, "No weeks found", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(CalendarActivity.this, "Shop ID not found for this user", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        morningSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("morningSstart").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        morningSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("morningSend").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        afternoonSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("afternoonSstart").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        afternoonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("afternoonSend").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        eveningSstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("eveningSstart").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        eveningSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

                add_shift_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener = firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userId = user.getUid();
                                    String dataItem = ip_shift_et.getText().toString();
                                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                                    firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("Calendar").child("week1").child("eveningSend").setValue(dataItem);
                                    dialog.dismiss();
                                    Toast.makeText(CalendarActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CalendarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(CalendarActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
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
        Sun1=findViewById(R.id.Sunday1);
        Sun2=findViewById(R.id.Sunday2);
        Sun3=findViewById(R.id.Sunday3);

        Mon1=findViewById(R.id.Monday1);
        Mon2=findViewById(R.id.Monday2);
        Mon3=findViewById(R.id.Monday3);

        Tue1=findViewById(R.id.Tuesday1);
        Tue2=findViewById(R.id.Tuesday2);
        Tue3=findViewById(R.id.Tuesday3);

        Wed1=findViewById(R.id.Wednesday1);
        Wed2=findViewById(R.id.Wednesday2);
        Wed3=findViewById(R.id.Wednesday3);


        Thu1=findViewById(R.id.Thursday1);
        Thu2=findViewById(R.id.Thursday2);
        Thu3=findViewById(R.id.Thursday3);


        Fri1=findViewById(R.id.Friday1);
        Fri2=findViewById(R.id.Friday2);
        Fri3=findViewById(R.id.Friday3);

        Sat1=findViewById(R.id.Saturday1);
        Sat2=findViewById(R.id.Saturday2);
        Sat3=findViewById(R.id.Saturday3);

        morningSstart=findViewById(R.id.morningSstart);
        morningSend=findViewById(R.id.morningSend);
        afternoonSstart=findViewById(R.id.afternoonSstart);
        afternoonSend=findViewById(R.id.afternoonSend);
        eveningSstart=findViewById(R.id.eveningSstart);
        eveningSend=findViewById(R.id.eveningSend);

        realtime_table = findViewById(R.id.realtime_table);




    }
}