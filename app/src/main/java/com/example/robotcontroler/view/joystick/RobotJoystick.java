package com.example.robotcontroler.view.joystick;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.robotcontroler.R;
import com.example.robotcontroler.view.joystick.net.JoysticDatargamClient;

public class RobotJoystick extends RelativeLayout implements JoystickSimple.OnMoveListener {

    private JoystickSimple joystickSimple;
    private TextView name;
    private JoysticDatargamClient client;
    private View view;
    public RobotJoystick(Context context,String name, String ip, int port) {
        super(context);
        view = View.inflate(context, R.layout.view_robot_joystick,this);
        joystickSimple = view.findViewById(R.id.joystick_base);
        this.name = view.findViewById(R.id.name_joystick_base);
        this.name.setText(name);
        joystickSimple.setOnMoveListener(this);
        //Log.e("ip",ip);
        client = new JoysticDatargamClient(ip,port);
        client.startThread();
        client.pauseThread();
    }

    public RobotJoystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_robot_joystick,this);
        joystickSimple = view.findViewById(R.id.joystick_base);
        this.name = view.findViewById(R.id.name_joystick_base);
    }

    @Override
    public void onMove(int angle, int strength) {
        client.resumeThread();
        //Log.e("move",angle+" "+strength);
        client.setTwist(angle,strength);
        client.pauseThread();
    }

    public void setEnableOnTouch(boolean f){
        joystickSimple.setEnabled(f);
        //view.setEnabled(f);
    }


}
