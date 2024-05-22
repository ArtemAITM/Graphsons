package com.example.opm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.opm.databinding.ActivityCsvdiagramBinding;

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
                String fileContent = readCsvFile(fileUri);
                TextView textViewContent = findViewById(R.id.textViewContent);
                textViewContent.setText(fileContent);
            } catch (IOException e) {
                Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readCsvFile(Uri fileUri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            String[] encodings = {"UTF-8", "windows-1251", "KOI8-R"};
            for (String encoding : encodings) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        String key = parts[0];
                        String value = parts[1];
                        if (keys.contains(key))
                            stringBuilder.append(line).append("\n");
                    }
                    return stringBuilder.toString();
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }

        return stringBuilder.toString();
    }
}