package com.example.yhourstaffproject.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.yhourstaffproject.R;
import com.example.yhourstaffproject.activities.TimerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffHomeFragment extends Fragment {
    private View mView;
    ImageButton scanQr_imgBtn;
    TextView scan_txt;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.fragment_staff_home, container, false);
        scanQr_imgBtn = mView.findViewById(R.id.scanQr_imgBtn);
        scan_txt = mView.findViewById(R.id.scan_txt);

        scanQr_imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndShowActivity(getContext());

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
       if (result.getContents()==null){
           Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
       }else {
           setResult(result.getContents());
       }
    });

    private void setResult(String contents) {

        Toast.makeText(getContext(), "Timekeeping successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), TimerActivity.class));
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
            LocalDate today = LocalDate.now();

            int year = today.getYear();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();

            String dateString = day + "/" + month + "/" + year;
            // Lấy giờ và phút hiện tại
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            // Thêm giờ và phút vào chuỗi dateString
            dateString += " " + hour + ":" + minute;

            FirebaseUser user = mAuth.getCurrentUser();
            String userId = user.getUid();
            String qrcode = dateString + userId;
            String encodedString = Base64.encodeToString(qrcode.getBytes(), Base64.DEFAULT);
            firebaseDatabase.getReference().child("QRCode").child("codescan").setValue(encodedString);
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        }else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (intentResult != null){
//            String a = intentResult.getContents();
//            if (a == null){
//                content.setText("null");
//            }else {
//                content.setText("not null");
//            }
//        }else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}