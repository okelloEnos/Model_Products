package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Retry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        TextView tvRetry = findViewById(R.id.tvRetry);
        Button retryBtn = findViewById(R.id.retry_button);

        if (getIntent().hasExtra("internet")){
            String connection = getIntent().getStringExtra("internet");

            if (connection.equals("first")){
                tvRetry.setText(R.string.No_internet);
            }
            else if (connection.equals("second")){
                tvRetry.setText(R.string.No_network);
            }
        }
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Retry.this, "Implementation Coming Soon...", Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(Retry.this, welcome_screen.class);
                startActivity(startIntent);
            }
        });
    }
}
