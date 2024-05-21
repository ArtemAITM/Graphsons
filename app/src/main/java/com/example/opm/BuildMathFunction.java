package com.example.opm;

import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class BuildMathFunction {
    private final LineChart chart;

    public BuildMathFunction(LineChart chart) {
        this.chart = chart;
    }

    public void plotFunction(List<String> functions) {
        List<ILineDataSet> dataSets = new ArrayList<>();

        for (String function : functions) {
            List<Entry> entries = calculateFunctionPoints(function);
            LineDataSet dataSet = new LineDataSet(entries, function);
            dataSet.setColor(Color.BLACK);
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
        chart.centerViewTo(0, 0, LEFT);
        chart.invalidate();
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
