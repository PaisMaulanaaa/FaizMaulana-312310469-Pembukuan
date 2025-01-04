// PemasukanActivity.java
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

public class PemasukanActivity extends AppCompatActivity {
    private TextView tvTotalIncome;
    private EditText etAmount, etDescription;
    private Button btnSubmitIncome;
    private ImageButton btnBack;
    private LinearLayout layoutHistory;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "IncomeData";
    private static final String KEY_INCOME = "income";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemasukan);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitIncome = findViewById(R.id.btnSubmitIncome);
        btnBack = findViewById(R.id.btnBack);
        layoutHistory = findViewById(R.id.layoutHistory);

        // Load and display income history
        loadIncomeHistory();

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Submit income button click listener
        btnSubmitIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etAmount.getText().toString();
                String description = etDescription.getText().toString();

                if (!amountStr.isEmpty() && !description.isEmpty()) {
                    long amount = Long.parseLong(amountStr);
                    saveIncome(amount, description);
                    etAmount.setText("");
                    etDescription.setText("");
                    Toast.makeText(PemasukanActivity.this, "Pemasukan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PemasukanActivity.this, "Masukkan nominal dan keterangan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveIncome(long amount, String description) {
        try {
            String incomeJson = sharedPreferences.getString(KEY_INCOME, "[]");
            JSONArray incomeArray = new JSONArray(incomeJson);

            // Create new income entry
            JSONObject newIncome = new JSONObject();
            newIncome.put("amount", amount);
            newIncome.put("description", description);
            newIncome.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

            // Add to array
            incomeArray.put(newIncome);

            // Save updated array
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_INCOME, incomeArray.toString());
            editor.apply();

            // Reload history
            loadIncomeHistory();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIncomeHistory() {
        try {
            String incomeJson = sharedPreferences.getString(KEY_INCOME, "[]");
            JSONArray incomeArray = new JSONArray(incomeJson);

            // Clear existing history
            layoutHistory.removeAllViews();

            // Calculate total income
            long totalIncome = 0;

            // Add history items
            for (int i = incomeArray.length() - 1; i >= 0; i--) {
                JSONObject income = incomeArray.getJSONObject(i);
                long amount = income.getLong("amount");
                String date = income.getString("date");
                String description = income.getString("description");

                totalIncome += amount;

                // Inflate history item
                View historyItem = getLayoutInflater().inflate(R.layout.income_item, layoutHistory, false);
                TextView tvDate = historyItem.findViewById(R.id.transaction_date);
                TextView tvAmount = historyItem.findViewById(R.id.transaction_amount);
                TextView tvDescription = historyItem.findViewById(R.id.transaction_description);

                tvDate.setText(date);
                tvAmount.setText(String.format(Locale.getDefault(), "Rp %,d", amount));
                tvDescription.setText(description);

                layoutHistory.addView(historyItem);
            }

            // Update total income display
            tvTotalIncome.setText(String.format(Locale.getDefault(), "Rp %,d", totalIncome));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}