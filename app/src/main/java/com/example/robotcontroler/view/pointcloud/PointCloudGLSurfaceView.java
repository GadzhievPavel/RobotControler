package com.example.robotcontroler.view.pointcloud;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import com.example.robotcontroler.view.pointcloud.net.DatagramClientPointCloud;

public class PointCloudGLSurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX[][]= new float[4][2];
    private float previousY[][]= new float[4][2];
    double scaleLast = 0;
    private final com.example.robotcontroler.view.pointcloud.MyGLRenderer renderer;
    boolean inTouch = false;
    int downPI = 0;
    private int upPI = 0;
    private double globalScale = 1;
    private boolean isCameraRotate = false;
    private ScaleGestureDetector scaleGestureDetector;
    private int port;
    private String ip;
    private boolean isRotateX, isRotateY, isRotateZ;
    public PointCloudGLSurfaceView(Context context, String ip, int port) {
        super(context);
        this.ip = ip;
        this.port = port;
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        DatagramClientPointCloud datagramClientPointCloud = new DatagramClientPointCloud(ip,port);
        renderer = new MyGLRenderer(context, datagramClientPointCloud);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        scaleGestureDetector = new ScaleGestureDetector(context, new ClassListener());
    }

    public PointCloudGLSurfaceView(Context context, AttributeSet attr){
        super(context,attr);
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(context);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        scaleGestureDetector = new ScaleGestureDetector(context, new ClassListener());

    }

    public void initUDPProtocol(String ip, int port){
        this.port = port;
        this.ip = ip;
        DatagramClientPointCloud datagramClientPointCloud = new DatagramClientPointCloud(ip,port);
        renderer.setClient(datagramClientPointCloud);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        int pointerIndex = event.getActionIndex();
        int pointerCount = event.getPointerCount();
        float xStart = 0, yStart = 0, xEnd = 0, yEnd = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                inTouch = true;
            case MotionEvent.ACTION_POINTER_DOWN:
                downPI = pointerIndex;
                break;
            case MotionEvent.ACTION_UP:
                inTouch = false;
            case MotionEvent.ACTION_POINTER_UP:
                upPI = pointerIndex;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX(0) - previousX[0][0];
                float dy = event.getY(0) - previousY[0][1];
                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }
                if (x < getWidth() / 2) {
                    dy = dy * -1;
                }

                if (isRotateX) {
                    renderer.setAngleX(renderer.getAngleX() + ((dy + dx) * TOUCH_SCALE_FACTOR));
                }
                if (isRotateY) {
                    renderer.setAngleY(renderer.getAngleY() + ((dy + dx) * TOUCH_SCALE_FACTOR));
                }
                if (isRotateZ) {
                    renderer.setAngleZ(renderer.getAngleZ() + ((dx + dy) * TOUCH_SCALE_FACTOR));
                }
                break;
        }
        requestRender();
        for (int i = 0; i<pointerCount;i++){
            previousX[i][0] = event.getX(i);
            previousY[i][1] = event.getY(i);
        }

        return true;
    }

    public boolean isCameraRotate() {
        return isCameraRotate;
    }

    public void setCameraRotate(boolean cameraRotate) {
        isCameraRotate = cameraRotate;
        renderer.setCameraRotate(isCameraRotate);
    }

    public void setIp(String ip){
        this.ip=ip;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setRotateX(boolean rotateX) {
        isRotateX = rotateX;
    }

    public void setRotateY(boolean rotateY) {
        isRotateY = rotateY;
    }

    public void setRotateZ(boolean rotateZ) {
        isRotateZ = rotateZ;
    }

    private class ClassListener extends SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            Log.e("Scale", String.valueOf(setScale(scale)));
            renderer.setScale((float) globalScale);
            return true;
        }

        private double setScale(double scale){
            globalScale = (globalScale * scale);

            return globalScale;
        }
    }
}
