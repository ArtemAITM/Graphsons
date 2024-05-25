package com.example.opm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;

import com.example.opm.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> items = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ActivityMainBinding binding;
    private int VISIBILITY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        binding.listView.setAdapter(adapter);

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BuildMathFunction buildMathFunction = new BuildMathFunction(binding.chart);
                    try {
                        buildMathFunction.plotFunction(items);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
