package com.example.yhourstaffproject.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yhourstaffproject.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button checkoutButton;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerTextView = findViewById(R.id.timer_text_view);
        checkoutButton = findViewById(R.id.checkout_button);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                // TODO: Thực hiện các hành động khi kết thúc
            }
        });

        startTimer();
    }

    private void startTimer() {
        timeLeftInMillis = TimeUnit.HOURS.toMillis(1); // Đếm ngược trong 1 giờ
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                // TODO: Thực hiện các hành động khi kết thúc đếm thời gian
            }
        }.start();
    }

    private void stopTimer() {
        countDownTimer.cancel();
    }

    private void updateCountdownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }
}
