package com.example.opm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityUgadaiGraphicBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

import org.apache.poi.ss.formula.functions.T;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UgadaiGraphic extends AppCompatActivity {
    private ActivityUgadaiGraphicBinding binding;
    ArrayList<Entry> entries = new ArrayList<>();
    String function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUgadaiGraphicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.check.setOnClickListener(v -> {
            String user_func = binding.function.getText().toString().replaceAll(" ", "");
            if(user_func.equals(function)){
                Toast.makeText(this, "Правильно", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this, "Неправильно", Toast.LENGTH_SHORT).show();
            }
        });
        binding.settingsUgadai.setOnClickListener(v -> {
            new SettingsMenu().showMenu(this, v, binding.GenChart);
        });
        binding.showAnswer.setOnClickListener(v -> {
            if (function != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Правильный ответ: " + function);
                builder.setPositiveButton("Попробовать ещё раз", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Выйти из режима", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(UgadaiGraphic.this, HomePage.class));
                    }
                });
                builder.show();
            }
        });
        binding.Generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function = GenerateFunction.generateFunction(new Random()).replaceAll(" ", "");
                System.out.println(function);
                ExecutorService service = Executors.newSingleThreadExecutor();
                Future<Void> future = service.submit(() -> {
                    entries = generateEntries();
                    return null;
                });
                try {
                    future.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                LineDataSet dataSet = new LineDataSet(entries, "Сгенерированный график");
                LineDataSet X = new LineDataSet(osiX(), null);
                LineDataSet Y = new LineDataSet(osiY(), null);
                X.setColor(Color.BLACK);
                X.setCircleHoleRadius(0);
                X.setCircleColor(Color.TRANSPARENT);
                X.setCircleHoleColor(Color.TRANSPARENT);
                Y.setColor(Color.BLACK);
                Y.setCircleHoleRadius(0);
                Y.setCircleColor(Color.TRANSPARENT);
                Y.setCircleHoleColor(Color.TRANSPARENT);


                dataSet.setCircleColor(Color.TRANSPARENT);
                dataSet.setColor(Color.RED);
                dataSet.setCircleRadius(0f);
                dataSet.setCircleHoleRadius(0f);
                dataSet.setCircleHoleColor(Color.TRANSPARENT);
                dataSet.setLineWidth(3);
                LineData data = new LineData(dataSet);
                data.addDataSet(X);
                data.addDataSet(Y);
                binding.GenChart.getDescription().setEnabled(false);
                Legend legend = binding.GenChart.getLegend();
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);
                binding.GenChart.getAxisRight().setEnabled(false);
                binding.GenChart.setData(data);
                binding.GenChart.centerViewTo(0, 0, YAxis.AxisDependency.LEFT);
                binding.GenChart.invalidate();
                binding.GenChart.invalidate();
                binding.GenChart.invalidate();
            }
        });

    }


    private ArrayList<Entry> generateEntries() {
        ArrayList<Entry> e = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                generateLocalEntries();
            }
        }).start();
        return e;
    }

    private void generateLocalEntries() {
        for(float i = -20; i <= 20; i+=0.1f){
            Entry entry = new Entry(i, calculateFunction(function, i));
            System.out.println(entry);
            entries.add(entry);
        }
        System.out.println(entries);
    }
    private static float calculateFunction(String function, float x) {
        try {
            Expression e = new ExpressionBuilder(function)
                    .variables("x")
                    .build()
                    .setVariable("x", x);
            ValidationResult validationResult = e.validate();
            if (!validationResult.isValid()) {
                throw new IllegalArgumentException("Неверное выражение: " + validationResult.getErrors());
            }
            return (float) e.evaluate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Float.NaN;
        }
    }

    public ArrayList<Entry> osiX(){
        ArrayList<Entry> e = new ArrayList<>();
        for (int i = -30; i <= 30; i++) {
            e.add(new Entry(i, 0));
        }
        return e;
    }
    public ArrayList<Entry> osiY(){
        ArrayList<Entry> e = new ArrayList<>();
        for (int i = -30; i <= 30; i++) {
            e.add(new Entry(0, i));
        }
        return e;
    }
}