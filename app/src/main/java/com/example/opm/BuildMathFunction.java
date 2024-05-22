package com.example.opm;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;

import android.graphics.Color;
import android.view.View;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
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

public class BuildMathFunction {
    private final LineChart chart;
    private final int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};


    public BuildMathFunction(LineChart chart) {
        this.chart = chart;
    }

    public void plotFunction(List<String> functions) {
        List<ILineDataSet> dataSets = new ArrayList<>();
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
            List<Entry> entries = calculateFunctionPoints(function);
            LineDataSet dataSet = new LineDataSet(entries, function);
            dataSet.setColor(colors[functions.indexOf(function)]);
            dataSet.setCircleColor(Color.TRANSPARENT);
            dataSet.setCircleHoleColor(Color.TRANSPARENT);
            dataSets.add(dataSet);
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
        for (int i = -100; i < 100; i++) {
            e.add(new Entry(0, i));
        }
        return e;
    }

    private List<Entry> osiX() {
        ArrayList<Entry> e = new ArrayList<>();
        for (int i = -100; i < 100; i++) {
            e.add(new Entry(i, 0));
        }
        return e;
    }

    private List<Entry> calculateFunctionPoints(String function) {
        List<Entry> entries = new ArrayList<>();

        for (float i = -200; i < 200; i+=0.01f) {
            float y = evaluateFunction(function, i);
            entries.add(new Entry(i, y));
        }

        return entries;
    }

    private float evaluateFunction(String function, double x) {
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
}
