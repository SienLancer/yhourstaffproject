package com.example.yhourstaffproject.fragments;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.BottomTabActivity;
import com.example.yhourstaffproject.activities.SalaryActivity;
import com.example.yhourstaffproject.activities.SignInForStaffActivity;
import com.example.yhourstaffproject.adapter.TimekeeppingAdapter;
import com.example.yhourstaffproject.object.Timekeeping;
import com.example.yhourstaffproject.utility.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class StaffHomeFragment extends Fragment {
    private View mView;
    ImageButton scanQr_imgBtn, on_shift_imgBtn;
    ImageButton checkout_btn, cancel_shift_btn;
    private RecyclerView recyclerView;
    private TimekeeppingAdapter adapter;
    private Handler handler = new Handler();
    private Runnable runnable;
    String hourText, dateText, totalTime;
    private List<Timekeeping> timekeepingList = new ArrayList<>();
    Button network_dialog_btn, button_yes, button_no;

    TextView total_salary_imgv, title_name_home_tv, title_checkin_tv, time_hour_tv, time_date_tv;
    TextView scan_txt, total_salary_home_tv, slogan_tv, dialog_title, dialog_message;
    Dialog dialog, networkDialog, yesNoDialog;
    Calendar currentTime;
    ImageView loading_imgv;
    AlertDialog loadDialog;
    Animation animation;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.fragment_staff_home, container, false);
        init();
        loadDialog();
        setupTimerButtonVisibilityListener();
        getUsername();
        loadDataSalary();
        loadDataFromFirebase();
        checkNetworkPeriodically(getContext());

        network_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkDialog.dismiss();
            }
        });

        cancel_shift_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_title.setText("Cancel shift");
                dialog_message.setText("Are you sure you want to cancel your shift?");
                yesNoDialog.show();

            }
        });

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelShift();
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesNoDialog.dismiss();
            }
        });


        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndShowActivity(getContext());
            }
        });


        on_shift_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAnimation();
                getDataTimeKeeping();

            }
        });


        scanQr_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndShowActivity(getContext());

            }
        });

        total_salary_imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SalaryActivity.class);
            startActivity(intent);
            }
        });

        return mView;
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
               if (isGranted){
                    showCamera();
               }else {

               }
            });

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        try {
            if (result.getContents() == null) {
                showCustomToast("Cancelled");
            } else {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

                    Query query = userReference.orderByChild("timestamp").limitToLast(1);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                boolean checkoutExists = false;
                                // Kiểm tra nếu có ít nhất một mục có trường checkout không rỗng
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    String checkoutTime = childSnapshot.child("checkOut").getValue(String.class);
                                    if (checkoutTime != null && !checkoutTime.isEmpty()) {
                                        checkoutExists = true;
                                        break;
                                    }
                                }
                                // Cập nhật giao diện dựa trên tồn tại của checkout
                                if (checkoutExists) { //checkout tồn tại
                                    setResult(result.getContents());
                                } else { // checkout không tồn tại
                                    handleQRCodeResult(result.getContents());
                                }
                            } else {
                                // Không có dữ liệu, hiển thị nút Timer
                                setResult(result.getContents());
                                on_shift_imgBtn.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý lỗi nếu có
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    public void init(){
        scanQr_imgBtn = mView.findViewById(R.id.scanQr_imgBtn);
        scan_txt = mView.findViewById(R.id.scan_txt);
        on_shift_imgBtn = mView.findViewById(R.id.on_shift_imgBtn);
        total_salary_imgv = mView.findViewById(R.id.total_salary_home_tv);
        title_name_home_tv = mView.findViewById(R.id.title_name_home_tv);
        total_salary_home_tv = mView.findViewById(R.id.total_salary_home_tv);
        slogan_tv = mView.findViewById(R.id.slogan_tv);


        recyclerView = mView.findViewById(R.id.timekeeping_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TimekeeppingAdapter(timekeepingList);
        recyclerView.setAdapter(adapter);


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_on_shift_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        title_checkin_tv = dialog.findViewById(R.id.title_checkin_tv);
        time_hour_tv = dialog.findViewById(R.id.time_hour_tv);
        time_date_tv = dialog.findViewById(R.id.time_date_tv);
        checkout_btn = dialog.findViewById(R.id.checkout_btn);
        cancel_shift_btn = dialog.findViewById(R.id.cancel_shift_btn);

        yesNoDialog=new Dialog(getContext());
        yesNoDialog.setContentView(R.layout.custom_yes_no_dialog);
        yesNoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        button_yes = yesNoDialog.findViewById(R.id.button_yes);
        button_no = yesNoDialog.findViewById(R.id.button_no);
        dialog_title = yesNoDialog.findViewById(R.id.dialog_title);
        dialog_message = yesNoDialog.findViewById(R.id.dialog_message);

        networkDialog = new Dialog(getContext());
        networkDialog.setContentView(R.layout.custom_network_dialog);
        networkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        network_dialog_btn = networkDialog.findViewById(R.id.network_dialog_btn);
    }


    private void checkNetworkPeriodically(Context context) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            networkDialog.show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!NetworkUtils.isNetworkAvailable(context)) {
                        networkDialog.show();
                    }else {
                        networkDialog.dismiss();
                        showCustomToast("Network is available");
                        return;
                    }
                    handler.postDelayed(this, 7000); // Lặp lại sau mỗi 5 giây
                }
            }, 7000); // Lặp lại sau mỗi 5 giây
        }
    }

    public void loadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); // Tùy chỉnh tùy theo nhu cầu của bạn
        View view = getLayoutInflater().inflate(R.layout.custom_loading_dialog, null);
        loading_imgv = view.findViewById(R.id.loading_imgv);

        builder.setView(view);
        loadDialog = builder.create();
        //dialog.getWindow().setWindowAnimations(R.style.RotateAnimation);
        loadDialog.getWindow().setLayout(130, 130);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Load animation
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        // Set listener to restart animation when it ends
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Restart animation when it ends
                loading_imgv.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        // Start animation
        loading_imgv.startAnimation(animation);
    }


    private void setResult(String contents) {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            if (ownerShopId != null) {
                                firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    String realtimeqr = snapshot.getValue(String.class);
                                                    if (contents.equals(realtimeqr)) {
                                                        showCustomToast("Check In successful!");
                                                        addDataTimeKeeping();

                                                    } else {
                                                        showCustomToast("Scan failed!");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle onCancelled event
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled event
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCamera() {


        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setPrompt("Scan QR");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);

        qrCodeLauncher.launch(options);
    }

    public StaffHomeFragment() {
        // Required empty public constructor
    }


    public static StaffHomeFragment newInstance(String param1, String param2) {
        StaffHomeFragment fragment = new StaffHomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED){
            showCamera();
            setDataQrCode();
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }


    public void addDataTimeKeeping() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            long timestamp = System.currentTimeMillis();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            LocalDateTime now = LocalDateTime.now();
                            int year = now.getYear();
                            int month = now.getMonthValue();
                            int day = now.getDayOfMonth();
                            int hour = now.getHour();
                            int minute = now.getMinute();
                            String minuteFormatted = String.format("%02d", minute);
                            String dayFormatted = String.format("%02d", day);
                            String monthFormatted = String.format("%02d", month);

                            String dateForTimeKeeping = day + " " + month + " " + year + " " + hour + ":" + minuteFormatted;
                            String dateForCheckIn = dayFormatted + "/" + monthFormatted + "/" + year + " " + hour + ":" + minuteFormatted;

                            DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        String id = timestamp + " " + dateForTimeKeeping; // Tạo id mới
                                        DatabaseReference newTimekeepingRef = userReference.child(id);

                                        if (!snapshot.child(id).exists()) {
                                            String checkIn = dateForCheckIn; // Giờ check in mặc định
                                            String checkOut = ""; // Không có giờ check out khi mới thêm

                                            Map<String, Object> timekeepingData = new HashMap<>();
                                            timekeepingData.put("id", id);
                                            timekeepingData.put("checkIn", checkIn);
                                            timekeepingData.put("checkOut", checkOut);

                                            newTimekeepingRef.setValue(timekeepingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Data added successfully");
                                                    } else {
                                                        Log.d(TAG, "Failed to add data");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Data already exists for today");
                                            Toast.makeText(getContext(), "Data already exists for today", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupTimerButtonVisibilityListener() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

                Query query = userReference.orderByChild("timestamp").limitToLast(1);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                boolean checkoutExists = false;
                                // Kiểm tra nếu có ít nhất một mục có trường checkout không rỗng
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    String checkoutTime = childSnapshot.child("checkOut").getValue(String.class);
                                    if (checkoutTime != null && !checkoutTime.isEmpty()) {
                                        checkoutExists = true;
                                        break;
                                    }
                                }
                                // Cập nhật giao diện dựa trên tồn tại của checkout
                                if (checkoutExists) { //checkout tồn tại
                                    on_shift_imgBtn.setVisibility(View.GONE);
                                    scanQr_imgBtn.setVisibility(View.VISIBLE);
                                    scan_txt.setText("Scan QR");
                                } else { // checkout không tồn tại
                                    on_shift_imgBtn.setVisibility(View.VISIBLE);
                                    scanQr_imgBtn.setVisibility(View.GONE);
                                    scan_txt.setText("On shift");
                                }
                            } else {
                                // Không có dữ liệu, hiển thị nút Timer
                                on_shift_imgBtn.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu có
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                };

                // Gắn lắng nghe vào truy vấn
                query.addValueEventListener(valueEventListener);

                // Lưu trữ ValueEventListener để có thể loại bỏ lắng nghe sau này nếu cần
                // (ví dụ: trong phương thức onDestroy của Activity hoặc Fragment)
                // Đảm bảo rằng bạn cần loại bỏ lắng nghe khi không cần thiết để tránh rò rỉ bộ nhớ.
                // Đối với Fragment, bạn có thể sử dụng onViewCreated hoặc onCreate để gắn lắng nghe
                // và sử dụng onDestroyView hoặc onDestroy để loại bỏ nó.
                // Đối với Activity, bạn có thể sử dụng onStart và onStop tương tự.
                // Ví dụ:
                // query.removeEventListener(valueEventListener);
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataQrCode() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            if(ownerShopId != null){
                                DatabaseReference shopRef = firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan");
                                // Thực hiện thay đổi dữ liệu
                                LocalDate today = LocalDate.now();

                                int year = today.getYear();
                                int month = today.getMonthValue();
                                int day = today.getDayOfMonth();
                                String dateForTimeKeeping = day + "/" + month + "/" + year;

                                String dateString = day + "/" + month + "/" + year;
                                // Lấy giờ và phút hiện tại
                                Calendar currentTime = Calendar.getInstance();
                                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                                int minute = currentTime.get(Calendar.MINUTE);

                                // Thêm giờ và phút vào chuỗi dateString
                                dateString += " " + hour + ":" + minute;


                                String qrcode = dateString + userId;
                                String encodedString = Base64.encodeToString(qrcode.getBytes(), Base64.DEFAULT);
                                shopRef.setValue(encodedString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(getContext(), "QR Code updated successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), "Failed to update QR Code", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFirebase() {
        try {
            loadDialog.show();
            FirebaseUser user = mAuth.getCurrentUser();
            assert user != null;
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            Log.d(TAG, "Owner Shop ID: " + ownerShopId);
                            if (ownerShopId != null) {
                                firebaseDatabase.getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            List<Timekeeping> reversedTimekeepingList = new ArrayList<>();
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String userKey = userSnapshot.getKey();
                                                if (userKey != null && userKey.equals(userId)) {
                                                    for (DataSnapshot timekeepingSnapshot : snapshot.child(userKey).child("timekeeping").getChildren()) {
                                                        try {
                                                            String checkIn = timekeepingSnapshot.child("checkIn").getValue(String.class);
                                                            String checkOut = timekeepingSnapshot.child("checkOut").getValue(String.class);
                                                            String[] parts = checkIn.split(" ");
                                                            String datePart = parts[0];
                                                            Log.d(TAG, "Date: " + datePart);
                                                            Log.d(TAG, "Check In: " + checkIn);
                                                            Log.d(TAG, "Check Out: " + checkOut);
                                                            reversedTimekeepingList.add(0, new Timekeeping(datePart, checkIn, checkOut));
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                            timekeepingList.clear();
                                            timekeepingList.addAll(reversedTimekeepingList);
                                            loadDialog.dismiss();
                                            adapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                                loadDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void dialogAnimation(){
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.DialogAnimation); // Thiết lập animation cho dialog
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.TOP | Gravity.START); // Thiết lập dialog nằm ở bên trái

        }

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //layoutParams.x = 100; // Vị trí theo chiều ngang
        layoutParams.y = 200; // Vị trí theo chiều dọc
        window.setAttributes(layoutParams);

        dialog.show();
    }

    public void getDataTimeKeeping() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

                // Sắp xếp theo thời gian giảm dần và giới hạn kết quả chỉ lấy 1 mục cuối cùng
                Query query = userReference.orderByChild("timestamp").limitToLast(1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                // Lặp qua kết quả (thoả mãn chỉ có 1 mục) để lấy dữ liệu
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    try {
                                        String checkInTime = childSnapshot.child("checkIn").getValue(String.class);
                                        String[] parts = checkInTime.split(" "); // Tách chuỗi theo dấu cách
                                        String hourPart = parts[1]; // Ghép lại phần ngày tháng năm
                                        String datePart = parts[0]; // Ghép lại phần ngày tháng năm
                                        // Hiển thị dữ liệu
                                        time_hour_tv.setText(hourPart);
                                        time_date_tv.setText(datePart);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void getUsername() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId);

                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                title_name_home_tv.setText("Hello, " + name);
                            } else {
                                Toast.makeText(getContext(), "Data doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleQRCodeResult(String contents) {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                            if(ownerShopId != null){
                                firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    String realtimeqr = snapshot.getValue(String.class);
                                                    if (contents.equals(realtimeqr)){
                                                        showCustomToast("Check Out successful!");
                                                        setDataForCheckout();
                                                        totalCost();
                                                        startActivity(new Intent(getActivity(), BottomTabActivity.class));
                                                    }else {
                                                        showCustomToast("Scan failed!");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                            else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataForCheckout() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").
                        child(userId).child("timekeeping");

                // Đặt dữ liệu cho checkout
                LocalDateTime now = LocalDateTime.now();
                int year = now.getYear();
                int month = now.getMonthValue();
                int day = now.getDayOfMonth();
                int hour = now.getHour();
                int minute = now.getMinute();
                String minuteFormatted = String.format("%02d", minute);
                String dayFormatted = String.format("%02d", day);
                String monthFormatted = String.format("%02d", month);

                String checkoutTime = dayFormatted + "/" + monthFormatted + "/" + year + " " + hour + ":" + minuteFormatted;

                // Tạo một đối tượng chứa dữ liệu để đẩy lên Firebase
                Map<String, Object> checkoutData = new HashMap<>();
                checkoutData.put("checkOut", checkoutTime);

                // Thực hiện truy vấn để lấy mục cuối cùng
                Query query = userReference.orderByChild("timestamp").limitToLast(1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                // Lặp qua kết quả (thoả mãn chỉ có 1 mục) để cập nhật dữ liệu checkout
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    try {
                                        String lastKey = childSnapshot.getKey();

                                        // Thêm dữ liệu checkout vào mục cuối cùng
                                        userReference.child(lastKey).updateChildren(checkoutData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Thành công
                                                        //hasCheckedOut = true;

                                                        Toast.makeText(getContext(), "Checkout data set successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Lỗi xảy ra
                                                        Toast.makeText(getContext(), "Failed to set checkout data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                // Không tìm thấy dữ liệu
                                Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Lỗi xảy ra
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataSalary() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference userRef = firebaseDatabase.getReference("User");

                userRef.child(userId).child("shopID").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.getValue(String.class);
                            if (ownerShopId != null) {
                                userRef.child(userId).child("salary").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            for (DataSnapshot salarySnapshot : snapshot.getChildren()) {
                                                String status = salarySnapshot.child("status").getValue(String.class);
                                                if (status != null && status.equals("Not paid yet")) {
                                                    // Only process data if status is "Not paid yet"

                                                    Integer currentSalary = salarySnapshot.child("currentSalary").getValue(Integer.class);

                                                    DecimalFormat formatter = new DecimalFormat("#,###");
                                                    String formattedSalary = formatter.format(currentSalary);

                                                    // Update UI with data from relevant key
                                                    total_salary_home_tv.setText(formattedSalary + " VND");


                                                    // Exit loop after processing one entry (optional, depending on your requirement)
                                                    break;
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void totalCost() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference userRef = firebaseDatabase.getReference("User");

                userRef.child(userId).child("shopID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String ownerShopId = snapshot.getValue(String.class);
                            if (ownerShopId != null) {
                                userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            Integer hourlySalary = snapshot.child("hourlySalary").getValue(Integer.class);
                                            Calendar checkoutTime = Calendar.getInstance();
                                            currentTime = Calendar.getInstance(); // Lấy thời gian hiện tại
                                            hourText = time_hour_tv.getText().toString();
                                            dateText = time_date_tv.getText().toString();
                                            totalTime = dateText + " " + hourText;
                                            //slogan_tv.setText(totalTime);
                                            Log.d(TAG, "totalTime: " + totalTime);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                            try {
                                                Date parsedDate = dateFormat.parse(totalTime);
                                                checkoutTime.setTime(parsedDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                                return;
                                            }

                                            // Tính toán thời gian giữa checkoutTime và thời gian đã set trước đó
                                            long timeDifferenceInMillis = checkoutTime.getTimeInMillis() - currentTime.getTimeInMillis();

                                            // Chuyển đổi thời gian từ millis thành giờ
                                            double totalHours = timeDifferenceInMillis / (1000.0 * 3600);

                                            // Tính số tiền tương ứng
                                            double totalCost = Math.abs(totalHours) * hourlySalary; // Sử dụng giá trị tuyệt đối của totalHours

                                            updateSalaryWithTotalCost(totalCost);

                                            // Hiển thị số tiền lên giao diện người dùng
                                            Toast.makeText(getContext(), String.format(Locale.getDefault(), "Total cost: %.0f VND", totalCost), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tính toán và cập nhật totalCost vào currentSalary trên cơ sở dữ liệu Firebase
    public void updateSalaryWithTotalCost(double totalCost) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("User");

            userRef.child(userId).child("shopID").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.getValue(String.class);
                    if (ownerShopId != null) {
                        userRef.child(userId).child("salary").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot salarySnapshot : snapshot.getChildren()) {
                                    String status = salarySnapshot.child("status").getValue(String.class);
                                    if (status != null && status.equals("Not paid yet")) {
                                        // Only process data if status is "Not paid yet"

                                        Integer currentSalary = salarySnapshot.child("currentSalary").getValue(Integer.class);

                                        // Cộng totalCost vào currentSalary
                                        int newSalary = currentSalary + (int) totalCost;

                                        // Cập nhật currentSalary mới lên cơ sở dữ liệu
                                        salarySnapshot.getRef().child("currentSalary").setValue(newSalary);

                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase Database Error: " + error.getMessage());
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelShift() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            if (user != null) {
                DatabaseReference userReference = firebaseDatabase.getReference("User").
                        child(userId).child("timekeeping");

                // Thực hiện truy vấn để lấy mục cuối cùng
                Query query = userReference.orderByChild("timestamp").limitToLast(1);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                // Lặp qua kết quả (thoả mãn chỉ có 1 mục) để xóa
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    try {
                                        String lastKey = childSnapshot.getKey();

                                        // Xóa mục cuối cùng
                                        userReference.child(lastKey).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Thành công
                                                        dialog.dismiss();
                                                        yesNoDialog.dismiss();
                                                        showCustomToast("Shift canceled successfully");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Lỗi xảy ra
                                                        Toast.makeText(getContext(), "Failed to remove last timekeeping", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                // Không tìm thấy dữ liệu
                                Toast.makeText(getContext(), "No timekeeping data found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Lỗi xảy ra
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void showCustomToast(String message) {
        // Inflate layout cho Toast
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, requireActivity().findViewById(R.id.custom_toast_container));

        // Thiết lập nội dung của Toast
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);

        // Tạo một Toast và đặt layout của nó
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


}