package com.example.opm;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BuildMathFunction {
    private final LineChart chart;
    private AppCompatActivity activity;
    private final int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};
    Thread[] threads;
    private List<String> items;
    private int count = 0;
    private final ArrayAdapter<String> adapter;
    private Context context;
    private List<String> functions;
    private List<ILineDataSet> dataSets = new ArrayList<>();

    public BuildMathFunction(LineChart chart, AppCompatActivity activity, Context context, List<String> items, ArrayAdapter<String> adapter, List<String> functions) {
        this.chart = chart;
        this.activity = activity;
        this.context = context;
        this.items = items;
        this.adapter = adapter;
        this.functions = functions;
        threads = new Thread[functions.size()];
    }

    public void plotFunction() throws InterruptedException {

        LineDataSet x = new LineDataSet(osiX(), null);
        LineDataSet y = new LineDataSet(osiY(), null);
        x.setCircleHoleColor(Color.TRANSPARENT);
        x.setCircleColor(Color.TRANSPARENT);
        x.setColor(Color.BLACK);
        x.setLineWidth(2f);
        y.setCircleHoleColor(Color.TRANSPARENT);
        y.setCircleColor(Color.TRANSPARENT);
        y.setColor(Color.BLACK);
        y.setLineWidth(2f);
        dataSets.add(x); dataSets.add(y);
        for (String function : functions) {
            threads[functions.indexOf(function)] = new Thread(new MyRunnable(function));
            threads[functions.indexOf(function)].start();
        }
        while (count != functions.size()){
            Thread.sleep(100);
        }
        LineData lineData = new LineData(dataSets);
        chart.setDrawingCacheEnabled(true);
        chart.setData(lineData);
        chart.setVisibleXRangeMinimum(0);
        chart.setVisibleXRangeMaximum(8F);
        chart.setVisibleYRangeMinimum(0f, LEFT);
        chart.setVisibleYRangeMaximum(8f, LEFT);
        chart.getVisibility();
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setAutoScaleMinMaxEnabled(false);
        chart.centerViewTo(0, 0, LEFT);


        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                chart.getDescription().setText("X: " + e.getX() + ", Y: " + e.getY());
            }

            @Override
            public void onNothingSelected() {
                chart.getDescription().setText("");
            }
        });
        chart.invalidate();

    }

    private List<Entry> osiY() {
        ArrayList<Entry> e = new ArrayList<>();
        for (int i = -200; i < 200; i++) {
            e.add(new Entry(0, i));
        }
        return e;
    }

    private List<Entry> osiX() {
        ArrayList<Entry> e = new ArrayList<>();
        for (int i = -200; i < 200; i++) {
            e.add(new Entry(i, 0));
        }
        return e;
    }
    public float CreateSeekDialog(String symbol) {
        final CompletableFuture<Float> future = new CompletableFuture<>();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Выберите значение " + symbol);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_seekbar, null);

                final SeekBar progressBar = dialogView.findViewById(R.id.seekBar);
                final TextView valueTextView = dialogView.findViewById(R.id.valueTextView);
                progressBar.setMax(100000);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setMin(-100000);
                }
                progressBar.setProgress(0);
                updateValueText(progressBar, valueTextView);
                progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        updateValueText(seekBar, valueTextView);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                builder.setView(dialogView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        future.complete(getValueFromProgress(progressBar));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        future.complete(0.0f);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateValueText(ProgressBar progressBar, TextView valueTextView) {
        float value = getValueFromProgress(progressBar);
        valueTextView.setText(String.format("%.2f", value));
    }

    private float getValueFromProgress(ProgressBar progressBar) {
        return (float) progressBar.getProgress() / 1000.0f;
    }




    private List<Entry> calculateFunctionPoints(String function) throws InterruptedException {
        List<Entry> entries = new ArrayList<>();
        float a = 0;
        float b = 0;
        float c = 0;
        float d = 0;
        float e = 0;
        float f = 0;
        float g = 0;
        if (function.contains("a")){
            a = CreateSeekDialog("a");
        }if (function.contains("b")){
            b = CreateSeekDialog("b");
        }if (function.contains("c")){
            c = CreateSeekDialog("c");
        }if (function.contains("d")){
            d = CreateSeekDialog("d");
        }if (function.contains("e")){
            e = CreateSeekDialog("e");
        }if (function.contains("f")){
            f = CreateSeekDialog("f");
        }if (function.contains("g")){
            g = CreateSeekDialog("g");
        }
        function = function.replaceAll("a", "y")
                .replaceAll("c", "z")
                .replaceAll("e", "u")
                .replaceAll("g", "w");
        for (float i = -100; i <= 100; i += 0.01f) {
            float y = evaluateFunction(function, i, a, b, c, d, e, f, g);
            entries.add(new Entry(i, y));
            System.out.println(y);
        }
        return entries;
    }
    public float evaluateFunction(String expression, float x, float y, float b, float z, float d, float u, float f, float w) {
        try {
            Expression exp = new ExpressionBuilder(expression)
                    .variables("x", "y", "b", "z", "d", "u", "f", "w")
                    .build()
                    .setVariable("x", x)
                    .setVariable("y", y)
                    .setVariable("b", b)
                    .setVariable("z", z)
                    .setVariable("d", d)
                    .setVariable("u", u)
                    .setVariable("f", f)
                    .setVariable("w", w);

            ValidationResult validationResult = exp.validate();
            if (!validationResult.isValid()) {
                throw new IllegalArgumentException("Неверное выражение: " + validationResult.getErrors());
            }

            return (float) exp.evaluate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Float.NaN;
        }
    }

    private class MyRunnable implements Runnable {
        String function;
        public MyRunnable(String function) {
            this.function = function;
        }
        @Override
        public void run() {
            List<Entry> entries = null;
            try {
                entries = calculateFunctionPoints(function);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LineDataSet dataSet = new LineDataSet(entries, function);
            dataSet.setColor(colors[functions.indexOf(function)]);
            dataSet.setCircleColor(Color.TRANSPARENT);
            dataSet.setLineWidth(3f);
            dataSet.setCircleHoleColor(Color.TRANSPARENT);
            dataSets.add(dataSet);
            try {
                Thread.sleep(500);
                System.out.println(entries);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;
        }
    }
}
