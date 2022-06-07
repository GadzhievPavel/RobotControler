package com.example.robotcontroler;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.robotcontroler.view.joystick.RobotJoystick;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    enum ViewType{JOYSTICK,TEXTINFO,POINTCLOUD};
    ViewType viewType;
    private TextView joystickText, showText, pointCloudText;
    private EditText name, port;
    private Button create;
    private CheckBox checkBox;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout;
    private int typeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeView = 0;
        setContentView(R.layout.control_activity);
        initView();
        setAllViewListener();
    }

    private void initView(){
        joystickText = findViewById(R.id.joystick_text);
        showText = findViewById(R.id.show_text);
        pointCloudText = findViewById(R.id.pointCloud_text);
        name = findViewById(R.id.name_obj);
        port = findViewById(R.id.port);
        create = findViewById(R.id.create);
        checkBox = findViewById(R.id.cheak_edit);
        coordinatorLayout = findViewById(R.id.lay);
        relativeLayout = findViewById(R.id.rel_but);
    }

    private void setAllViewListener(){
        joystickText.setOnClickListener(this);
        showText.setOnClickListener(this);
        pointCloudText.setOnClickListener(this);
        create.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.joystick_text:
                setVisibleCreatorView();
                viewType = ViewType.JOYSTICK;
                break;
            case R.id.show_text:
                setVisibleCreatorView();
                viewType = ViewType.TEXTINFO;
                break;
            case R.id.pointCloud_text:
                setVisibleCreatorView();
                viewType = ViewType.POINTCLOUD;
                break;
            case R.id.create:
                if(!isCorrectInputData(name,port)) {
                    Toast.makeText(this, "Не хватает данных", Toast.LENGTH_SHORT).show();
                }else{
                    switch (viewType){
                        case JOYSTICK:
                            LinearLayout.LayoutParams params;
                            RobotJoystick robotJoystick = new RobotJoystick(ControlActivity.this,name.getText().toString(),
                                    "",Integer.parseInt(port.getText().toString()));
                            params = new LinearLayout.LayoutParams(250,250);
                            robotJoystick.setLayoutParams(params);
                            robotJoystick.setOnTouchListener(this);
                            coordinatorLayout.addView(robotJoystick);
                            setGoneCreatorView();
                            break;
                        case POINTCLOUD:
                            break;
                        case TEXTINFO:
                            break;
                    }
                }
                break;
        }
    }

    private void setVisibleCreatorView(){
        create.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        port.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    private void setGoneCreatorView(){
        port.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        create.setVisibility(View.GONE);
        port.getText().clear();
        name.getText().clear();
    }
    private boolean isCorrectInputData(EditText name, EditText port){
        return name.getText().length()>0 && port.getText().length()>0;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
