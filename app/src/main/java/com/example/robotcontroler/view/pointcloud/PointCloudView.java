package com.example.robotcontroler.view.pointcloud;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.robotcontroler.R;

public class PointCloudView extends RelativeLayout {
    private Switch xRotate, yRotate, zRotate;
    private PointCloudGLSurfaceView pointCloudGLSurfaceView;
    private Handler handler;
    public PointCloudView(Context context) {
        super(context);
    }

    public PointCloudView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        View view = View.inflate(context, R.layout.cloudpoint_view,this);
        pointCloudGLSurfaceView = view.findViewById(R.id.cloudPointSurface);
        TypedArray attributes = context.obtainStyledAttributes(attributeSet, R.styleable.PointCloudView);
        pointCloudGLSurfaceView.initUDPProtocol(attributes.getString(R.styleable.PointCloudView_ip),
                attributes.getInteger(R.styleable.PointCloudView_port,50001));
        xRotate = view.findViewById(R.id.xRotate);
        yRotate = view.findViewById(R.id.yRotate);
        zRotate = view.findViewById(R.id.zRotate);
        handler = new Handler();
        handler.postDelayed(updateGL,100);
        xRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pointCloudGLSurfaceView.setRotateX(isChecked);
                if(isChecked){
                    pointCloudGLSurfaceView.setRotateY(!isChecked);
                    yRotate.setChecked(!isChecked);
                    pointCloudGLSurfaceView.setRotateZ(!isChecked);
                    zRotate.setChecked(!isChecked);
                }else{
                    pointCloudGLSurfaceView.setRotateX(isChecked);
                }
            }
        });

        yRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pointCloudGLSurfaceView.setRotateY(isChecked);
                if(isChecked){
                    pointCloudGLSurfaceView.setRotateZ(!isChecked);
                    zRotate.setChecked(!isChecked);
                    pointCloudGLSurfaceView.setRotateX(!isChecked);
                    xRotate.setChecked(!isChecked);
                }else{
                    pointCloudGLSurfaceView.setRotateY(isChecked);
                }
            }
        });
        zRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pointCloudGLSurfaceView.setRotateZ(isChecked);
                if(isChecked){
                    pointCloudGLSurfaceView.setRotateX(!isChecked);
                    xRotate.setChecked(!isChecked);
                    pointCloudGLSurfaceView.setRotateY(isChecked);
                    yRotate.setChecked(!isChecked);
                }else{
                    pointCloudGLSurfaceView.setRotateZ(isChecked);
                }
            }
        });
    }

    private Runnable updateGL = new Runnable() {
        @Override
        public void run() {
            pointCloudGLSurfaceView.requestRender();
            handler.postDelayed(updateGL,1000);
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
