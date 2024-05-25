package com.example.opm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opm.databinding.ActivityHomePageBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class HomePage extends AppCompatActivity {
    private DatabaseReference visitsRef;
    private ActivityHomePageBinding binding;
    private static final String TG = "https://t.me/ArtemItCod";
    private List<String> modes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        visitsRef = database.getReference("visits");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeightPx = displayMetrics.heightPixels;
        float screenHeightDp = screenHeightPx / displayMetrics.density;
        float phoneVerticalPaddingCoefficient = 0.05f; // 1% от высоты экрана
System.out.println(screenHeightDp);
        float tabletVerticalPaddingCoefficient = 0.1f; // 2% от высоты экрана
        int itemVerticalPadding;
        if (displayMetrics.heightPixels < 900) {
            itemVerticalPadding = (int) (screenHeightDp * phoneVerticalPaddingCoefficient);
        } else {
            itemVerticalPadding = (int) (screenHeightDp * tabletVerticalPaddingCoefficient);
        }
        binding.listView.setDividerHeight(itemVerticalPadding);

        addVisit();
        SetupKeys();

    }

    private void SetupKeys(){
        modes = Arrays.asList(getText(R.string.FirstButton).toString(),
                getText(R.string.SecondButton).toString(), getText(R.string.ThirdButton).toString(),
                getText(R.string.FourButton).toString(), getText(R.string.FiveButton).toString());
        List<Item> items = new ArrayList<>();
        List<Class> classes = Arrays.asList(XLSXDiagram.class, MainActivity.class, PieCharts.class, BarCharts.class, RadarGraphActivity.class);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            items.add(new ButtonAndImageButtonItem(
                    this,
                    modes.get(i),
                    R.drawable.info2,
                    v -> startActivity(new Intent(this, classes.get(finalI))),
                    v -> new DialogManager(this).showDialogInfo(finalI)
            ));
        }
        ItemAdapter adapter = new ItemAdapter(this, items);
        binding.listView.setAdapter(adapter);

        binding.tg.setOnClickListener(v -> {
            openSite();
        });
        binding.info.setOnClickListener(v -> {
            ShowInfo();
        });

    }

    private void openSite() {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HomePage.TG));
        startActivity(webIntent);
    }
    private void addVisit() { //добавляет время входа в приложение в Firebase
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        String visitData = calendar.get(Calendar.YEAR) + ":"
                + (calendar.get(Calendar.MONTH) + 1) + ":"
                + calendar.get(Calendar.DAY_OF_MONTH) + ":"
                + calendar.get(Calendar.HOUR_OF_DAY);

        String visitId = visitsRef.push().getKey();

        assert visitId != null;
        System.out.println(date.getHours());
        visitsRef.child(visitId).setValue(visitData);
    }
    private void ShowInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Информация о проекте");
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.info_alert, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
