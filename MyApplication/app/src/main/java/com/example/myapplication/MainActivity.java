package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btnIhudHazala = null;
        Button btnMada = null;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnIhudHazala = this.findViewById(R.id.btnIhudHazala);
        btnIhudHazala.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Call  1221 איחוד הצלה ?", Toast.LENGTH_SHORT).show();
                String phoneNumber = "1221"; // Replace with your desired phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
            }
        });
        btnMada = this.findViewById(R.id.btnMada);
        btnMada.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Call Mada 101 ?", Toast.LENGTH_SHORT).show();
                String phoneNumber = "101"; // Replace with your desired phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
            }
        });
    }
}