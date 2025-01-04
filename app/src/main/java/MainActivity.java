package com.example.pembukuan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private CardView cardMenabung, cardPemasukan, cardPengeluaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardMenabung = findViewById(R.id.cardMenabung);
        cardPemasukan = findViewById(R.id.cardPemasukan);
        cardPengeluaran = findViewById(R.id.cardPengeluaran);

        cardMenabung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenabungActivity.class);
                startActivity(intent);
            }
        });

        cardPemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PemasukanActivity.class);
                startActivity(intent);
            }
        });

        cardPengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PengeluaranActivity.class);
                startActivity(intent);
            }
        });
    }
}
