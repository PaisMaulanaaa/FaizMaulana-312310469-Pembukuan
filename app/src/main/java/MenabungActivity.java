package com.example.pembukuan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenabungActivity extends AppCompatActivity {
    private TextView tvTotalSavings;
    private EditText etAmount, etMotivation;
    private Button btnSubmitSaving, btnSubmitMotivation;
    private ImageButton btnBack;
    private LinearLayout layoutHistory;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "SavingsData";
    private static final String KEY_SAVINGS = "savings";
    private static final String KEY_MOTIVATION = "motivation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menabung);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        tvTotalSavings = findViewById(R.id.tvTotalSavings);
        etAmount = findViewById(R.id.etAmount);
        etMotivation = findViewById(R.id.etMotivation);
        btnSubmitSaving = findViewById(R.id.btnSubmitSaving);
        btnSubmitMotivation = findViewById(R.id.btnSubmitMotivation);
        btnBack = findViewById(R.id.btnBack);
        layoutHistory = findViewById(R.id.layoutHistory);

        // Load saved motivation
        String savedMotivation = sharedPreferences.getString(KEY_MOTIVATION, "");
        etMotivation.setText(savedMotivation);

        // Load and display savings history
        loadSavingsHistory();

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Submit saving button click listener
        btnSubmitSaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etAmount.getText().toString();
                if (!amountStr.isEmpty()) {
                    long amount = Long.parseLong(amountStr);
                    saveSaving(amount);
                    etAmount.setText("");
                    Toast.makeText(MenabungActivity.this, "Tabungan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MenabungActivity.this, "Masukkan nominal tabungan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Submit motivation button click listener
        btnSubmitMotivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motivation = etMotivation.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_MOTIVATION, motivation);
                editor.apply();
                Toast.makeText(MenabungActivity.this, "Motivasi berhasil disimpan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSaving(long amount) {
        try {
            String savingsJson = sharedPreferences.getString(KEY_SAVINGS, "[]");
            JSONArray savingsArray = new JSONArray(savingsJson);

            // Create new saving entry
            JSONObject newSaving = new JSONObject();
            newSaving.put("amount", amount);
            newSaving.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

            // Add to array
            savingsArray.put(newSaving);

            // Save updated array
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_SAVINGS, savingsArray.toString());
            editor.apply();

            // Reload history
            loadSavingsHistory();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadSavingsHistory() {
        try {
            String savingsJson = sharedPreferences.getString(KEY_SAVINGS, "[]");
            JSONArray savingsArray = new JSONArray(savingsJson);

            // Clear existing history
            layoutHistory.removeAllViews();

            // Calculate total savings
            long totalSavings = 0;

            // Add history items
            for (int i = savingsArray.length() - 1; i >= 0; i--) {
                JSONObject saving = savingsArray.getJSONObject(i);
                long amount = saving.getLong("amount");
                String date = saving.getString("date");

                totalSavings += amount;

                // Inflate history item
                View historyItem = getLayoutInflater().inflate(R.layout.transaction_item, layoutHistory, false);
                TextView tvDate = historyItem.findViewById(R.id.transaction_date);
                TextView tvAmount = historyItem.findViewById(R.id.transaction_amount);

                tvDate.setText(date);
                tvAmount.setText(String.format(Locale.getDefault(), "Rp %,d", amount));

                layoutHistory.addView(historyItem);
            }

            // Update total savings display
            tvTotalSavings.setText(String.format(Locale.getDefault(), "Rp %,d", totalSavings));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}