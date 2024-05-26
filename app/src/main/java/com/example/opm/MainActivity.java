package com.example.opm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.opm.databinding.ActivityMainBinding;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static List<String> items = new ArrayList<>();
    static ArrayAdapter<String> adapter;
    private ActivityMainBinding binding;
    private int VISIBILITY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new ArrayAdapter<>(this, R.layout.function_item, items);
        binding.listView.setAdapter(adapter);
        KeyboardView keyboardView = new KeyboardView(this);
        keyboardView.setTextView(binding.inputEditText1);

        binding.view.setOnKeyboardButtonClickListener(new KeyboardView.OnKeyboardButtonClickListener() {
            @Override
            public void onHomeButtonClick() {
                binding.chart.centerViewTo(0, 0, YAxis.AxisDependency.LEFT);
            }

            @Override
            public void onDeleteButtonClick() {
                String text = binding.inputEditText1.getText().toString();
                if (!text.isEmpty()) {
                    binding.inputEditText1.setText(text.substring(0, text.length() - 1));
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNormalButtonClick(int buttonId, String buttonText) {
                String text = binding.inputEditText1.getText().toString();
                binding.inputEditText1.setText(text + buttonText);
            }
        });

        binding.openKeyboard.setOnClickListener(v -> {
            if (VISIBILITY == 0) {
                VISIBILITY = 1;
                binding.functions.setVisibility(View.VISIBLE);
            }else{
                VISIBILITY = 0;
                binding.functions.setVisibility(View.GONE);

            }
        });
        binding.settings.setOnClickListener(v -> {
            new SettingsMenu().showMenu(this, v, binding.chart);
        });
        binding.Add.setOnClickListener(v -> {
            String text = binding.inputEditText1.getText().toString();
            if (!text.isEmpty()){
                items.add(text);
                adapter.notifyDataSetChanged();
            }
            binding.inputEditText1.setText("");
        });
        binding.drawAll.setOnClickListener(v -> {
            new Thread(() -> {
                BuildMathFunction buildMathFunction =
                        new BuildMathFunction(binding.chart,
                                MainActivity.this, MainActivity.this,
                                items,
                                adapter, items);
                try {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    });
                    buildMathFunction.plotFunction();
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });


        binding.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Удалить функцию?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });

    }
}
