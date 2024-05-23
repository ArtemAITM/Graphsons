package com.example.opm;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityRadarGraphBinding;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadarGraphActivity extends AppCompatActivity {
    private ActivityRadarGraphBinding binding;
    private ArrayList<RadarEntry> radarEntries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<>();
    private final int PICK_FILE_REQUEST_CODE = 1;
    int Y;
    private int NumberSheet;
    private Uri fileUri;
    String textXLSX = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRadarGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buildGraphButton.setOnClickListener(v -> {
            buildRadarGraph();
        });
        binding.FileDrawing.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });
        binding.DrawXLSX.setOnClickListener(v -> {
            NumberSheet = Integer.parseInt(binding.editText1.getText().toString());
            Y = binding.editText2.getText().toString().charAt(0) - 65;
            binding.SettingsXlsx.setVisibility(View.GONE);
            try {
                radarEntries = loadDataFromExcel(fileUri);
                setupRadarChart(radarEntries);

            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            binding.SettingsXlsx.setVisibility(View.VISIBLE);
            fileUri = data.getData();
        }
    }
    private float[] parseInputData() {
        String inputText = binding.inputDataEditText.getText().toString();
        String[] values = inputText.split(",");
        float[] data = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            data[i] = Float.parseFloat(values[i].trim());
        }
        return data;
    }
    private RadarDataSet createRadarDataSet(float[] inputData) {
        RadarDataSet radarDataSet = new RadarDataSet(Arrays.asList(convertToEntries(inputData)), "Radar Graph");
        radarDataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        radarDataSet.setFillColor(ColorTemplate.MATERIAL_COLORS[0]);
        radarDataSet.setDrawFilled(true);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextSize(12f);
        return radarDataSet;
    }
    private void displayRadarGraph(RadarDataSet radarDataSet) {
        binding.radarGraphView.clear();
        RadarData radarData = new RadarData(radarDataSet);
        binding.radarGraphView.getXAxis().setTextSize(12f);
        binding.radarGraphView.getYAxis().setDrawLabels(false);
        binding.radarGraphView.getLegend().setEnabled(false);
        binding.radarGraphView.getDescription().setEnabled(false);

        binding.radarGraphView.setData(radarData);
        binding.radarGraphView.invalidate();
    }

    private RadarEntry[] convertToEntries(float[] data) {
        RadarEntry[] entries = new RadarEntry[data.length];
        for (int i = 0; i < data.length; i++) {
            entries[i] = new RadarEntry(data[i]);
        }
        return entries;
    }

    private ArrayList<RadarEntry> loadDataFromExcel(Uri fileUri) throws IOException, InvalidFormatException {
        ArrayList<RadarEntry> entries = new ArrayList<>();
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(NumberSheet);
        for (int row = 0; row < sheet.getLastRowNum(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow != null) {
                Cell cell = currentRow.getCell(Y);
                if (cell != null) {
                    float value = (float) cell.getNumericCellValue();
                    entries.add(new RadarEntry(value));
                }
            }
        }

        return entries;
    }
    private void buildRadarGraph() {
        float[] inputData = parseInputData();
        RadarDataSet radarDataSet = createRadarDataSet(inputData);
        displayRadarGraph(radarDataSet);
    }
    private void setupRadarChart(ArrayList<RadarEntry> entries) {
        RadarDataSet dataSet = new RadarDataSet(entries, "Column A");
        dataSet.setColor(Color.BLUE);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);

        RadarData data = new RadarData(dataSet);

        binding.radarGraphView.setData(data);
        binding.radarGraphView.getDescription().setEnabled(false);
        binding.radarGraphView.invalidate();
    }
}
