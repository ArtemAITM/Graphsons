package com.example.opm;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityPieChartsBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class PieCharts extends AppCompatActivity {
    private final ArrayList<String> valuesVisible = new ArrayList<>();
    private final ArrayList<String> keys = new ArrayList<>();
    private final ArrayList<Integer> values = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private final int summ = 0;
    private final ArrayList<PieEntry> entries = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPieChartsBinding binding = ActivityPieChartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new ArrayAdapter<>(this, R.layout.function_item, R.id.FunctionString, valuesVisible);
        binding.listView.setAdapter(adapter);
        binding.add.setOnClickListener(v ->{
            if(!binding.value.getText().toString().isEmpty() && !binding.key.getText().toString().isEmpty()) {
                String textValue = binding.value.getText().toString();
                String Key = binding.key.getText().toString();
                valuesVisible.add(Key + ": " + textValue);
                keys.add(Key);
                values.add(Integer.valueOf(textValue));
                adapter.notifyDataSetChanged();
                System.out.println(summ);
            }
        });
        binding.DrawPie.setOnClickListener(v -> {
            for (int i = 0; i < values.size(); i++) {
                entries.add(new PieEntry(values.get(i), keys.get(i)));
            }
            PieDataSet dataSet = getPieDataSet();
            PieData data = new PieData(dataSet);
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            binding.pieChart.setHoleRadius(40);
            binding.pieChart.setData(data);
            binding.pieChart.invalidate();
        });
    }

    @NonNull
    private PieDataSet getPieDataSet() {
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 10));
        dataSet.setSelectionShift(6f);
        dataSet.setValueLinePart1OffsetPercentage(100f);
        dataSet.setValueLinePart1Length(0.6f);
        dataSet.setValueLinePart2Length(0.6f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        return dataSet;
    }
}