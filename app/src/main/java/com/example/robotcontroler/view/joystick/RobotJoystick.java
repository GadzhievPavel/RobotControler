package com.example.robotcontroler.view.joystick;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.robotcontroler.R;

public class RobotJoystick extends RelativeLayout implements JoystickSimple.OnMoveListener {

    private JoystickSimple joystickSimple;
    private TextView name;
    public RobotJoystick(Context context,String name, String ip, int port) {
        super(context);
        View view = View.inflate(context, R.layout.view_robot_joystick,this);
        joystickSimple = view.findViewById(R.id.joystick_base);
        this.name = view.findViewById(R.id.name_joystick_base);
        this.name.setText(name);
        joystickSimple.setOnMoveListener(this);
    }

    public RobotJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_robot_joystick,this);
        joystickSimple = view.findViewById(R.id.joystick_base);
        this.name = view.findViewById(R.id.name_joystick_base);
    }

    @Override
    public void onMove(int angle, int strength) {
        Log.e("move",angle+" "+strength);
    }
}
