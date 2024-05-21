package com.example.opm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;

import com.example.opm.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        binding.settings.setOnClickListener(this::showSettingsMenu);
        binding.Add.setOnClickListener(v -> {
            String text = binding.inputEditText.getText().toString();
            if (!text.isEmpty()){
                items.add(text);
                adapter.notifyDataSetChanged();
            }
        });
        binding.drawAll.setOnClickListener(v -> {
            BuildMathFunction buildMathFunction = new BuildMathFunction(binding.chart);
            buildMathFunction.plotFunction(items);
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
    public void showSettingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FileOutputStream out;
                if (item.getItemId() == R.id.save) {
                    Bitmap bitmap = Bitmap.createBitmap(binding.chart.getWidth()
                            , binding.chart.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.TRANSPARENT);
                    binding.chart.draw(canvas);
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String name = String.valueOf(new Date().getTime());
                    File file = new File(folder, name + ".png");
                    return true;
                } else if (item.getItemId() == R.id.share) {
                    share(binding.chart);
                }else if(item.getItemId() == R.id.print){
                    Bitmap bitmap = Bitmap.createBitmap(binding.chart.getWidth()
                            , binding.chart.getHeight(), Bitmap.Config.ARGB_4444);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.TRANSPARENT);
                    binding.chart.draw(canvas);
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String name = String.valueOf(new Date().getTime());
                    File file = new File(folder, name + ".png");
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        PrintHelper.PrintHelper(MainActivity.this, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    file.delete();
                }
                return false;
            }
        });

        popupMenu.show();
    }
    public void share(View graphView) {

        Bitmap bitmap = Bitmap.createBitmap(graphView.getWidth(), graphView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        graphView.draw(canvas);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "graph_screenshot.png",
                null
        ));
        String text = "Изображение от одного из наших продуктов - присоединяйся к нам: ";
        String link = "https://graphsons.webflow.io/";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + link);
        startActivity(Intent.createChooser(shareIntent, "Поделиться графиком"));
    }
}
