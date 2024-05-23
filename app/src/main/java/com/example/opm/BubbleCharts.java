package com.example.opm;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.opm.databinding.ActivityBubbleChartsBinding;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BubbleCharts extends AppCompatActivity {

    private BubbleChart bubbleChart;
    private ActivityBubbleChartsBinding binding;
    private int X0;
    private int Y0;
    private int X1;
    private int Y1;
    private int NumberSheet;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBubbleChartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bubbleChart = findViewById(R.id.bubbleChart);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, 1);
        binding.buttonSave.setOnClickListener(v -> {
            binding.SettingsXlsx.setVisibility(View.GONE);
            NumberSheet = Integer.parseInt(binding.editText1.getText().toString());
            X0 = binding.editText2.getText().toString().charAt(0) - 65;
            Y0 = Integer.parseInt(binding.editText2.getText().toString().substring(1));
            X1 = binding.editText3.getText().toString().charAt(0) - 65;
            Y1 =Integer.parseInt(binding.editText3.getText().toString().substring(1));
            List<BubbleEntry> entries;
            try {
                entries = loadDataFromExcel(fileUri);
            } catch (IOException | InvalidFormatException e) {
                throw new RuntimeException(e);
            }
            setupBubbleChart(entries);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            binding.SettingsXlsx.setVisibility(View.VISIBLE);
        }
    }

    private List<BubbleEntry> loadDataFromExcel(Uri fileUri) throws IOException, InvalidFormatException {
        List<BubbleEntry> entries = new ArrayList<>();
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(NumberSheet);
        if(X1 - X0 != 2){
            Toast.makeText(this, "Ошибка данных", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Ошибочный ввод данных");
        }
        for (int row = Y0; row < Y1; row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow != null) {
                Cell xCell = currentRow.getCell(X0);
                Cell yCell = currentRow.getCell(X0+1);
                Cell sizeCell = currentRow.getCell(X0+2);
                if (xCell != null && yCell != null && sizeCell != null) {
                    float x = (float) xCell.getNumericCellValue();
                    float y = (float) yCell.getNumericCellValue();
                    float size = (float) sizeCell.getNumericCellValue();
                    entries.add(new BubbleEntry(x, y, size));
                }
            }
        }

        return entries;
    }

    private void setupBubbleChart(List<BubbleEntry> entries) {
        BubbleDataSet dataSet = new BubbleDataSet(entries, "Bubble Data");
        dataSet.setColor(Color.BLUE);
        dataSet.setHighlightCircleWidth(1.5f);
        dataSet.setDrawValues(false);

        BubbleData data = new BubbleData(dataSet);
        bubbleChart.setData(data);
        bubbleChart.setVisibleXRange(0, 50);
        bubbleChart.setVisibleYRange(0, 50, YAxis.AxisDependency.LEFT);
        bubbleChart.getDescription().setEnabled(false);
        bubbleChart.invalidate();
    }
}
