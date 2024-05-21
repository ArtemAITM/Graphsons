package com.example.opm;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opm.databinding.ActivityXlsxdiagramBinding;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XLSXDiagram extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static int NumberSheet;
    private static int X0;
    private static int Y0;
    private static int X1;
    private static int Y1;
    private ActivityXlsxdiagramBinding binding;
    public static final ArrayList<BarEntry> BAR_ENTRIES = new ArrayList<>();
    public static final ArrayList<PieEntry> PIE_ENTRIES = new ArrayList<>();
    public static final ArrayList<String> keys = new ArrayList<>();
    public static final ArrayList<String> values = new ArrayList<>();
    private Uri fileUri;
    private boolean LoadedData = false;
    @SuppressLint("CommitTransaction")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXlsxdiagramBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       // createMenu();
        binding.AddFILE.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);

        });
        binding.settinsxlsx.setOnClickListener(v -> {
            binding.pieChart.setVisibility(View.GONE);
            binding.barChart.setVisibility(View.GONE);
            binding.SettingsXlsx.setVisibility(View.VISIBLE);
        });
        binding.buttonSave.setOnClickListener(v -> {
            String sheet = binding.editText1.getText().toString();
            String coord0 = binding.editText2.getText().toString();
            String coord1 = binding.editText3.getText().toString();
            if (coord1.length() == 2 && coord0.length() == 2) {
                NumberSheet = Integer.parseInt(sheet);
                X0 = coord0.charAt(0) - 65;
                X1 = coord1.charAt(0) - 65;
                Y0 = Integer.parseInt(coord0.substring(1));
                Y1 = Integer.parseInt(coord1.substring(1));
            }
            else {
                Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show();
            }
            System.out.println(X0);
            binding.SettingsXlsx.setVisibility(View.GONE);
            binding.barChart.setVisibility(View.VISIBLE);
            binding.pieChart.setVisibility(View.VISIBLE);
            if(LoadedData){
                readExcelFile(fileUri);
            }else {
                Toast.makeText(this, "Данных нет", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            LoadedData = true;
       }
    }
    private void readExcelFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            assert inputStream != null;
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(NumberSheet);
            if (X1 - X0 != 1) {
                throw new IllegalArgumentException("Файл должен содержать только два столбца.");
            }else {
                for(int rowNum = Y0; rowNum <= Y1; ++rowNum){
                    Row row = sheet.getRow(rowNum);
                    if (row != null){
                        Cell cellKey = row.getCell(0);
                        String key = getCellValue(cellKey);
                        Cell cellValue = row.getCell(1);
                        String value = getCellValue(cellValue);
                        keys.add(key);
                        values.add(value);
                    }
                }
                for (int i = 0; i < keys.size(); i++) {
                    System.out.println(keys.get(i) + " = " + values.get(i));
                }

                DrawingChartsXLSX draw = new DrawingChartsXLSX(binding);
                binding.pieChart.setData(null);
                draw.DRAW();
            }
            inputStream.close();
        } catch (IOException e) {
            System.out.println("нЕ норм");
            e.printStackTrace();
        }
    }


    public String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}