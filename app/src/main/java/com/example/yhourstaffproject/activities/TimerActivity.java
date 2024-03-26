package com.example.yhourstaffproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.fragments.StaffHomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDate;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private boolean hasCheckedOut = false;
    private Button checkoutButton;
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

        startTimer();
    }

    private void startTimer() {
        countUpTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsedInMillis += 1000;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                // Not used in count up timer
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        if (!hasCheckedOut) {
            // Nếu chưa thực hiện checkout, không cho phép quay lại
            Toast.makeText(this, "Please checkout before leaving", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }




    private void stopTimer() {
        countUpTimer.cancel();
        hasCheckedOut = true;
        Intent intent = new Intent(this, StaffHomeFragment.class);
        startActivity(intent);
    }

    private void updateCountdownText() {
        int hours = (int) (timeElapsedInMillis / 1000) / 3600;
        int minutes = (int) ((timeElapsedInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeElapsedInMillis / 1000) % 60;

        String timeElapsedFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeElapsedFormatted);
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
        long totalTimeInSeconds = timeElapsedInMillis / 1000;

        // Chuyển đổi thời gian từ giây thành giờ
        double totalHours = totalTimeInSeconds / 3600.0;

        // Tính số tiền tương ứng
        double totalCost = totalHours * 15000; // Giả sử mỗi giờ là 15,000 VND

        // Hiển thị số tiền lên giao diện người dùng
        Toast.makeText(this, String.format(Locale.getDefault(), "Total cost: %.0f VND", totalCost), Toast.LENGTH_SHORT).show();

        // Dừng đồng hồ và thực hiện các hành động khác (nếu cần)
        // TODO: Handle QR code content, e.g., stop timer, navigate to checkout page
        stopTimer();
        // Navigate to checkout page or perform checkout-related actions
    }
}
