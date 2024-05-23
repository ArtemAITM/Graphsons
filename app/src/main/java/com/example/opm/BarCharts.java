package com.example.opm;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityBarChartsBinding;
import com.example.opm.databinding.ActivityPieChartsBinding;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class BarCharts extends AppCompatActivity {
    private final ArrayList<String> valuesVisible = new ArrayList<>();
    private final ArrayList<Integer> keys = new ArrayList<>();
    private final ArrayList<Integer> values = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private final int summ = 0;
    ActivityBarChartsBinding binding;
    private final ArrayList<BarEntry> entries = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarChartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new ArrayAdapter<>(this, R.layout.function_item, R.id.FunctionString, valuesVisible);
        binding.listView.setAdapter(adapter);
        binding.add.setOnClickListener(v ->{
            if(!binding.value.getText().toString().isEmpty() && !binding.key.getText().toString().isEmpty()) {
                String Key = binding.value.getText().toString();
                String textValue = binding.key.getText().toString();
                valuesVisible.add(Key + ": " + textValue);
                keys.add(Integer.valueOf(Key));
                values.add(Integer.valueOf(textValue));
                adapter.notifyDataSetChanged();
                System.out.println(summ);
            }
        });
        binding.DrawBar.setOnClickListener(v -> {
            for (int i = 0; i < values.size(); i++) {
                entries.add(new BarEntry(values.get(i), keys.get(i)));
            }
            BarData data = new BarData(getBarDataSet());
            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);
            binding.barChart.setData(data);
            binding.barChart.invalidate();
        });
        binding.SettingsBar.setOnClickListener(v -> {
            new SettingsMenu().showMenu(this, v, binding.barChart);
        });
    }

    private BarDataSet getBarDataSet() {
        BarDataSet dataSet = new BarDataSet(entries, null);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        return dataSet;
    }

}