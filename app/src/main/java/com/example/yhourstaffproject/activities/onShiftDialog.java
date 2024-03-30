package com.example.yhourstaffproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.fragments.StaffHomeFragment;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class onShiftDialog extends DialogFragment {

     TextView time_date_tv, time_hour_tv;
     Button checkoutButton;
     Calendar currentTime;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ActivityResultLauncher<ScanOptions> qrCodeLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_on_shift_dialog, container, false);

        time_hour_tv = view.findViewById(R.id.time_hour_tv);
        time_date_tv = view.findViewById(R.id.time_date_tv);


        currentTime = Calendar.getInstance(); // Lấy thời gian hiện tại


//        checkoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkPermissionAndShowActivity(getContext());
//            }
//        });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if (isGranted){
                showCamera();
            }else {

            }
        });

        qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                handleQRCodeResult(result.getContents());
            } else {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();

// Đợi 5 giây (ví dụ)
        long delayMillis = 2000; // 5000 milliseconds = 5 seconds

// Sử dụng Handler để chạy getDataTimeKeeping() sau khi đợi 5 giây
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Gọi hàm getDataTimeKeeping() sau khi đợi 5 giây
                getDataTimeKeeping();
            }
        }, delayMillis);

        return view;
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
                            time_hour_tv.setText(checkInTime);
                        }
                    } else {
                        Toast.makeText(getContext(), "Data doesn't exist", Toast.LENGTH_SHORT).show();
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
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ownerShopId = snapshot.child("User").child(userId).child("shopID").getValue(String.class);
                    if(ownerShopId != null){
                        firebaseDatabase.getReference().child("Shop").child(ownerShopId).child("QRCode").child("codeScan")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String realtimeqr = snapshot.getValue(String.class);
                                        if (contents.equals(realtimeqr)){
                                            Toast.makeText(getContext(), "Check out successful!", Toast.LENGTH_SHORT).show();
                                            setDataForCheckout();
                                            totalCost();
                                            startActivity(new Intent(getActivity(), BottomTabActivity.class));
                                        }else {
                                            Toast.makeText(getContext(), "Scan failed!", Toast.LENGTH_SHORT).show();
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

    public void setDataForCheckout() {
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

            String checkoutTime = day + "/" + month + "/" + year + " " + hour + ":" + minuteFormatted;

            // Tạo một đối tượng chứa dữ liệu để đẩy lên Firebase
            Map<String, Object> checkoutData = new HashMap<>();
            checkoutData.put("checkOut", checkoutTime);

            // Thực hiện truy vấn để lấy mục cuối cùng
            Query query = userReference.orderByChild("timestamp").limitToLast(1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Lặp qua kết quả (thoả mãn chỉ có 1 mục) để cập nhật dữ liệu checkout
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
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
                        }
                    } else {
                        // Không tìm thấy dữ liệu
                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
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
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED){
            showCamera();
            setDataQrCodeCheckOut();

        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }


    }

    public void totalCost() {
        Calendar checkoutTime = Calendar.getInstance();
        String timerText = time_hour_tv.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
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
        Toast.makeText(getContext(), String.format(Locale.getDefault(), "Total cost: %.0f VND", totalCost), Toast.LENGTH_SHORT).show();
    }

    public void setDataQrCodeCheckOut(){

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        if (user != null) {
            firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                    Toast.makeText(getContext(), "QR Code updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to update QR Code", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Shop not found", Toast.LENGTH_SHORT).show();
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

    }


}
