package com.example.opm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityHomePageBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class HomePage extends AppCompatActivity {
    private DatabaseReference visitsRef;
    ActivityHomePageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        visitsRef = database.getReference("visits");
        addVisit();

        binding.MenuButton1.setOnClickListener(v -> {
            startActivity(new Intent(this, XLSXDiagram.class));
        });
        binding.MenuButton2.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        binding.MenuButton3.setOnClickListener(v -> {
            startActivity(new Intent(this, PieCharts.class));
        });
        binding.MenuButton4.setOnClickListener(v -> {
            startActivity(new Intent(this, UgadaiGraphic.class));
        });
    }

    private void addVisit() { //добавляет время входа в приложение в Firebase
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH));
        String visitData = calendar.get(Calendar.YEAR) + ":"
                + (calendar.get(Calendar.MONTH) + 1) + ":"
                + calendar.get( Calendar.DAY_OF_MONTH) + ":"
                + calendar.get(Calendar.HOUR_OF_DAY);

        String visitId = visitsRef.push().getKey();

        assert visitId != null;
        System.out.println(date.getHours());
        visitsRef.child(visitId).setValue(visitData);
    }

}