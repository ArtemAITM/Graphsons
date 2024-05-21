package com.example.opm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityUgadaiGraphicBinding;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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


        binding.Generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function = GenerateFunction.generateFunction(new Random());
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
                try {
                    Thread.sleep(3000);
                    System.out.println(entries);
                    LineDataSet dataSet = new LineDataSet(entries, "Сгенерированный график");
                    dataSet.setCircleColor(Color.TRANSPARENT);
                    dataSet.setColor(Color.RED);
                    dataSet.setCircleRadius(0f);
                    dataSet.setCircleHoleRadius(0f);
                    dataSet.setCircleHoleColor(Color.TRANSPARENT);
                    dataSet.setLineWidth(20f);
                    LineData data = new LineData(dataSet);
                    binding.GenChart.setData(data);
                    binding.GenChart.centerViewTo(0, 0, YAxis.AxisDependency.LEFT);
                    binding.GenChart.invalidate();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }


    private ArrayList<Entry> generateEntries() {
        ArrayList<Entry> e = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = -20; i <= 20; i ++) {
                        Socket socket = new Socket("192.168.0.173", 3524);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        String message = function.replaceAll(" ", "");
                        out.println(message + " " + i);
                        final String finalResponse = in.readLine();
                        float y = Float.parseFloat(finalResponse);
                        e.add(new Entry(i, y));
                        System.out.println(e);
                        in.close();
                        out.close();
                        socket.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return e;
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