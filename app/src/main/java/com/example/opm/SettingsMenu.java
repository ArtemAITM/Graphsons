package com.example.opm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.github.mikephil.charting.charts.Chart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SettingsMenu {
    public void showMenu(Context context, View view, Chart chart){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save) {
                    Bitmap bitmap = Bitmap.createBitmap(chart.getWidth()
                            , chart.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    chart.draw(canvas);
                    File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String name = String.valueOf(new Date().getTime());
                    File file = new File(folder, name + ".png");
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        notifyGallery(file, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return true;
                } else if (item.getItemId() == R.id.share) {
                    share(chart, context);
                }else if(item.getItemId() == R.id.print){
                    print(chart, context);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void print(Chart chart, Context context) {
        Bitmap bitmap = Bitmap.createBitmap(chart.getWidth()
                , chart.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        chart.draw(canvas);
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String name = String.valueOf(new Date().getTime());
        File file = new File(folder, name + ".png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            PrintHelper.PrintHelper(context, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
    }

    private void notifyGallery(File file, Context context) {
        MediaScannerConnection.scanFile(
                context, new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> {
                    Toast.makeText(context, "Сохранено!", Toast.LENGTH_SHORT).show();
                }
        );
    }
    public void share(View graphView, Context context) {

        Bitmap bitmap = Bitmap.createBitmap(graphView.getWidth(), graphView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        graphView.draw(canvas);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(
                context.getContentResolver(),
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
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться графиком"));
    }
}
