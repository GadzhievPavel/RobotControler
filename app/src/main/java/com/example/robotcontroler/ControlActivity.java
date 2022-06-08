package com.example.robotcontroler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentManager;

import com.example.robotcontroler.view.ControlView;
import com.example.robotcontroler.view.joystick.RobotJoystick;
import com.google.android.material.behavior.SwipeDismissBehavior;

import java.util.ArrayList;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    enum ViewType{JOYSTICK,TEXTINFO,POINTCLOUD};
    ViewType viewType;
    private TextView joystickText, showText, pointCloudText;
    private EditText name, port;
    private Button create;
    private CheckBox checkBox;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout;
    private int typeView;
    private String ip;
    private ArrayList<ControlView> listViews;
    boolean enableDrag = false;
    private int _xDelta, _yDelta;
    private Context context;
    private  FragmentManager fragmentManager;
    private LayoutInflater inflater;
    private ViewGroup parent;
    ArrayList<RobotJoystick> robotJoysticks;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeView = 0;
        setContentView(R.layout.control_activity);
        initView();
        listViews = new ArrayList<>();
        Bundle arguments = getIntent().getExtras();
        ip = arguments.get("ip").toString();
        setAllViewListener();
        context = this;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent = (ViewGroup)findViewById(R.id.lay);
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

    @SuppressLint("ClickableViewAccessibility")
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
//                            LinearLayout.LayoutParams params;
//                            Log.e("address",ip+" "+Integer.parseInt(port.getText().toString()));
//                            RobotJoystick robotJoystick = new RobotJoystick(ControlActivity.this,name.getText().toString(),
//                                    ip,Integer.parseInt(port.getText().toString()));
//                            params = new LinearLayout.LayoutParams(250,250);
//                            robotJoystick.setLayoutParams(params);
//                            ControlView<View> controlView = new ControlView<>(robotJoystick);
//                            //controlView.getView().setOnTouchListener(this);
//                            coordinatorLayout.addView(robotJoystick);
//                            listViews.add(controlView);
//                            setGoneCreatorView();
                            RobotJoystick robotJoystick = new RobotJoystick(ControlActivity.this,name.getText().toString(),
                                    ip,Integer.parseInt(port.getText().toString()));
                            LinearLayout.LayoutParams params;
                            params = new LinearLayout.LayoutParams(250,250);
                            robotJoystick.setLayoutParams(params);
                            parent.addView(robotJoystick);
                            setAllTouchListener(parent);
                            setGoneCreatorView();
                            break;
                        case POINTCLOUD:
                            break;
                        case TEXTINFO:
                            TextView textView = new TextView(context);
                            params = new LinearLayout.LayoutParams(250,250);
                            textView.setLayoutParams(params);
                            textView.setText("text info:");
                            parent.addView(textView);
                            setAllTouchListener(parent);
                            setGoneCreatorView();
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
        if(isChecked){
            enableTouchListenerView(false);
        }else{
            enableTouchListenerView(true);
        }
    }

    private void enableTouchListenerView(boolean f){
        this.enableDrag = !f;
        for (int i = 0; i<parent.getChildCount();i++) {
            if(parent.getChildAt(i) instanceof RobotJoystick){
                Log.e("robot joystic", "no enable");
                RobotJoystick robotJoystick = (RobotJoystick) parent.getChildAt(i);
                robotJoystick.setEnableOnTouch(f);
            }
        }
    }

    private void setAllTouchListener(ViewGroup viewGroup){
        for(int i =0; i< viewGroup.getChildCount();i++){
            Log.e("touch", String.valueOf(viewGroup.getChildAt(i)));
            viewGroup.getChildAt(i).setOnTouchListener(onTouchListener);
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.e("view touch", String.valueOf(v.getId()));
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            Log.e("touch", String.valueOf(enableDrag));
            if (enableDrag) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        CoordinatorLayout.LayoutParams lParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        Log.e("actionDown", lParams.leftMargin + " " + lParams.topMargin);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("actionUp", "actionUp");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.e("actionPointrDown", "actionPointrDown");
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.e("actionPointrUp", "actionPointrUp");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                        layoutParams.leftMargin = X - _xDelta;
                        layoutParams.topMargin = Y - _yDelta;
                        layoutParams.rightMargin = -250;
                        layoutParams.bottomMargin = -250;
                        v.setLayoutParams(layoutParams);
                        Log.e("Move", "Move");
                        break;
                }
                return true;

            } else {
                return false;
            }
        }
    };
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        Log.e("Touch","touch");
//        for (int i = 0; i<listViews.size();i++){
//            if(listViews.get(i).getView().equals(v)){
//                Log.e("Touch","touch");
//            }
//        }
//        return false;
//    }

    //    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        final int X = (int) event.getRawX();
//        final int Y = (int) event.getRawY();
//        Log.e("touch", String.valueOf(enableDrag));
//        if(enableDrag) {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//                    CoordinatorLayout.LayoutParams lParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
//                    _xDelta = X - lParams.leftMargin;
//                    _yDelta = Y - lParams.topMargin;
//                    Log.e("actionDown", lParams.leftMargin+" "+lParams.topMargin);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.e("actionUp","actionUp");
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    Log.e("actionPointrDown","actionPointrDown");
//                    break;
//                case MotionEvent.ACTION_POINTER_UP:
//                    Log.e("actionPointrUp","actionPointrUp");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
//                    layoutParams.leftMargin = X - _xDelta;
//                    layoutParams.topMargin = Y - _yDelta;
//                    layoutParams.rightMargin = -250;
//                    layoutParams.bottomMargin = -250;
//                    v.setLayoutParams(layoutParams);
//                    Log.e("Move","Move");
//                    break;
////                default:
////                    return false;
//            }
//            return true;
//
//        }else{
//            return false;
//        }
//    }
}
