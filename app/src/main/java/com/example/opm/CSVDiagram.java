package com.example.opm;

import static com.example.opm.XLSXDiagram.PIE_ENTRIES;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.opm.databinding.ActivityCsvdiagramBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CSVDiagram extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 1;
    private TextView textViewContent;
    private ActivityCsvdiagramBinding binding;
    private ArrayList<Float> values = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<PieEntry> entries = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCsvdiagramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCsvFile();
            }
        });
    }

    private void pickCsvFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            try {
                readCsvFile(fileUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            drawPieDiagram();
        }
    }

    private void drawPieDiagram(){
        for (int i = 0; i < XLSXDiagram.values.size(); i++) {
            entries.add(new PieEntry(Float.parseFloat(XLSXDiagram.values.get(i)), XLSXDiagram.keys.get(i)));
        }
        PieDataSet dataSet = getPieDataSet();
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        binding.CSVChart.setHoleRadius(40);
        binding.CSVChart.setData(data);
        binding.CSVChart.invalidate();
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

    private void readCsvFile(Uri fileUri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            String[] encodings = {"UTF-8", "windows-1251", "KOI8-R"};
            for (String encoding : encodings) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line;
                    int i = 0;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        String key = parts[0];
                        float value = Float.parseFloat(parts[1]);
                        if (keys.contains(key)){
                            values.set(i, value + values.get(i));
                        }
                        else{
                            values.add(value);
                            keys.add(key);
                        }
                        i++;
                    }
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }

        stringBuilder.toString();
    }
}