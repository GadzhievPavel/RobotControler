package com.example.robotcontroler;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText ipRobotEditText;
    private Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipRobotEditText = findViewById(R.id.ipRobot);
        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipRobotEditText.getText().toString();
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                intent.putExtra("ip",ip);
                startActivity(intent);
            }
        });
    }


}