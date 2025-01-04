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

public class PengeluaranActivity extends AppCompatActivity {
    private TextView tvTotalExpense;
    private EditText etAmount, etDescription;
    private Button btnSubmitExpense;
    private ImageButton btnBack;
    private LinearLayout layoutHistory;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ExpenseData";
    private static final String KEY_EXPENSE = "expense";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengeluaran);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize views
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitExpense = findViewById(R.id.btnSubmitExpense);
        btnBack = findViewById(R.id.btnBack);
        layoutHistory = findViewById(R.id.layoutHistory);

        // Load and display expense history
        loadExpenseHistory();

        // Back button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Submit expense button click listener
        btnSubmitExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etAmount.getText().toString();
                String description = etDescription.getText().toString();

                if (!amountStr.isEmpty() && !description.isEmpty()) {
                    long amount = Long.parseLong(amountStr);
                    saveExpense(amount, description);
                    etAmount.setText("");
                    etDescription.setText("");
                    Toast.makeText(PengeluaranActivity.this, "Pengeluaran berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PengeluaranActivity.this, "Masukkan nominal dan keterangan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveExpense(long amount, String description) {
        try {
            String expenseJson = sharedPreferences.getString(KEY_EXPENSE, "[]");
            JSONArray expenseArray = new JSONArray(expenseJson);

            // Create new expense entry
            JSONObject newExpense = new JSONObject();
            newExpense.put("amount", amount);
            newExpense.put("description", description);
            newExpense.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

            // Add to array
            expenseArray.put(newExpense);

            // Save updated array
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_EXPENSE, expenseArray.toString());
            editor.apply();

            // Reload history
            loadExpenseHistory();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenseHistory() {
        try {
            String expenseJson = sharedPreferences.getString(KEY_EXPENSE, "[]");
            JSONArray expenseArray = new JSONArray(expenseJson);

            // Clear existing history
            layoutHistory.removeAllViews();

            // Calculate total expense
            long totalExpense = 0;

            // Add history items
            for (int i = expenseArray.length() - 1; i >= 0; i--) {
                JSONObject expense = expenseArray.getJSONObject(i);
                long amount = expense.getLong("amount");
                String date = expense.getString("date");
                String description = expense.getString("description");

                totalExpense += amount;

                // Inflate history item
                View historyItem = getLayoutInflater().inflate(R.layout.expense_item, layoutHistory, false);
                TextView tvDate = historyItem.findViewById(R.id.transaction_date);
                TextView tvAmount = historyItem.findViewById(R.id.transaction_amount);
                TextView tvDescription = historyItem.findViewById(R.id.transaction_description);

                tvDate.setText(date);
                tvAmount.setText(String.format(Locale.getDefault(), "Rp %,d", amount));
                tvDescription.setText(description);

                layoutHistory.addView(historyItem);
            }

            // Update total expense display
            tvTotalExpense.setText(String.format(Locale.getDefault(), "Rp %,d", totalExpense));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}