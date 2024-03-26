package com.example.yhourstaffproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.fragments.StaffHomeFragment;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private boolean hasCheckedOut = false;
    private Button checkoutButton;
    private Calendar currentTime;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String PREFS_NAME = "MyPrefsFile";



    private CountDownTimer countUpTimer;
    private long timeElapsedInMillis;

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerTextView = findViewById(R.id.timer_text_view);
        checkoutButton = findViewById(R.id.checkout_button);

        currentTime = Calendar.getInstance(); // Lấy thời gian hiện tại


        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                handleQRCodeResult(result.getContents());
            } else {
                Toast.makeText(TimerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        getDataTimeKeeping();
    }

    public void getDataTimeKeeping() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            DatabaseReference userReference = firebaseDatabase.getReference("User").child(userId).child("timekeeping");

            // Sắp xếp theo thời gian giảm dần và giới hạn kết quả chỉ lấy 1 mục cuối cùng
            Query query = userReference.orderByChild("timestamp").limitToLast(1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Lặp qua kết quả (thoả mãn chỉ có 1 mục) để lấy dữ liệu
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String checkInTime = childSnapshot.child("checkIn").getValue(String.class);
                            // Hiển thị dữ liệu
                            timerTextView.setText(checkInTime);
                        }
                    } else {
                        Toast.makeText(TimerActivity.this, "Data doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TimerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(TimerActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }




//    @Override
//    public void onBackPressed() {
//        if (!hasCheckedOut) {
//            // Nếu chưa thực hiện checkout, không cho phép quay lại
//            Toast.makeText(this, "Please checkout before leaving", Toast.LENGTH_SHORT).show();
//        } else {
//            super.onBackPressed();
//        }
//    }




    private void stopTimer() {
//        countUpTimer.cancel();
        hasCheckedOut = true;
        Intent intent = new Intent(this, StaffHomeFragment.class);
        startActivity(intent);
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

    private void handleQRCodeResult(String contents) {
        // Lấy thời gian hiện tại từ timerTextView
        Calendar checkoutTime = Calendar.getInstance();
        String timerText = timerTextView.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date parsedDate = dateFormat.parse(timerText);
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
        double totalCost = Math.abs(totalHours) * 15000; // Sử dụng giá trị tuyệt đối của totalHours

        // Hiển thị số tiền lên giao diện người dùng
        Toast.makeText(this, String.format(Locale.getDefault(), "Total cost: %.0f VND", totalCost), Toast.LENGTH_SHORT).show();

        // Dừng đồng hồ và thực hiện các hành động khác (nếu cần)
        stopTimer();
        // Navigate to checkout page or perform checkout-related actions
    }

}
