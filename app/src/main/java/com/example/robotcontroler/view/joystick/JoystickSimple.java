package com.example.robotcontroler.view.joystick;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.robotcontroler.R;
import com.example.robotcontroler.view.joystick.net.JoysticDatargamClient;

public class JoystickSimple extends View implements Runnable {
    /*
    INTERFACES
    */
    public interface OnMoveListener {
        void onMove(int angle, int strength);
    }
    public interface OnMultipleLongPressListener {

        void onMultipleLongPress();
    }
    /*
    CONSTANTS
    */
    private static final int DEFAULT_LOOP_INTERVAL = 50; // in milliseconds
    private static final int MOVE_TOLERANCE = 10;
    private static final int DEFAULT_COLOR_BUTTON = Color.BLACK;
    private static final int DEFAULT_COLOR_BORDER = Color.TRANSPARENT;
    private static final int DEFAULT_ALPHA_BORDER = 255;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_SIZE = 200;
    private static final int DEFAULT_WIDTH_BORDER = 3;
    private static final boolean DEFAULT_FIXED_CENTER = true;
    private static final boolean DEFAULT_AUTO_RECENTER_BUTTON = true;
    private static final boolean DEFAULT_BUTTON_STICK_TO_BORDER = false;

    // DRAWING
    private Paint mPaintCircleButton;
    private Paint mPaintCircleBorder;
    private Paint mPaintBackground;
    private Paint mPaintText;

    private Paint mPaintBitmapButton;
    private Bitmap mButtonBitmap;

    private float mButtonSizeRatio;

    private float mBackgroundSizeRatio;


    // COORDINATE
    private int mPosX = 0;
    private int mPosY = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;

    private int mFixedCenterX = 0;
    private int mFixedCenterY = 0;

    private boolean mFixedCenter;

    private boolean mAutoReCenterButton;

    private boolean mButtonStickToBorder;

    private boolean mEnabled;

    // SIZE
    private int mButtonRadius;
    private int mBorderRadius;
    private int mBorderAlpha;
    private float mBackgroundRadius;
    private OnMoveListener mCallback;
    private long mLoopInterval = DEFAULT_LOOP_INTERVAL;
    private Thread mThread = new Thread(this);
    private OnMultipleLongPressListener mOnMultipleLongPressListener;
    private final Handler mHandlerMultipleLongPress = new Handler();
    private Runnable mRunnableMultipleLongPress;
    private int mMoveTolerance;
    public static int BUTTON_DIRECTION_BOTH = 0;
    private int mButtonDirection = 0;

    JoysticDatargamClient client;
    /*
    CONSTRUCTORS
     */

    public JoystickSimple(Context context,String ip, int port) {
        this(context, null);
        client = new JoysticDatargamClient(ip, port);
        client.startThread();
        client.pauseThread();
    }

    public JoystickSimple(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public JoystickSimple(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.JoystickSimple,
                0, 0
        );

        int buttonColor;
        int borderColor;
        int backgroundColor;
        int borderWidth;
        Drawable buttonDrawable;
        try {
            buttonColor = styledAttributes.getColor(R.styleable.JoystickSimple_JV_buttonColor, DEFAULT_COLOR_BUTTON);
            borderColor = styledAttributes.getColor(R.styleable.JoystickSimple_JV_borderColor, DEFAULT_COLOR_BORDER);
            mBorderAlpha = styledAttributes.getInt(R.styleable.JoystickSimple_JV_borderAlpha, DEFAULT_ALPHA_BORDER);
            backgroundColor = styledAttributes.getColor(R.styleable.JoystickSimple_JV_backgroundColor, DEFAULT_BACKGROUND_COLOR);
            borderWidth = styledAttributes.getDimensionPixelSize(R.styleable.JoystickSimple_JV_borderWidth, DEFAULT_WIDTH_BORDER);
            mFixedCenter = styledAttributes.getBoolean(R.styleable.JoystickSimple_JV_fixedCenter, DEFAULT_FIXED_CENTER);
            mAutoReCenterButton = styledAttributes.getBoolean(R.styleable.JoystickSimple_JV_autoReCenterButton, DEFAULT_AUTO_RECENTER_BUTTON);
            mButtonStickToBorder = styledAttributes.getBoolean(R.styleable.JoystickSimple_JV_buttonStickToBorder, DEFAULT_BUTTON_STICK_TO_BORDER);
            buttonDrawable = styledAttributes.getDrawable(R.styleable.JoystickSimple_JV_buttonImage);
            mEnabled = styledAttributes.getBoolean(R.styleable.JoystickSimple_JV_enabled, true);
            mButtonSizeRatio = styledAttributes.getFraction(R.styleable.JoystickSimple_JV_buttonSizeRatio, 1, 1, 0.25f);
            mBackgroundSizeRatio = styledAttributes.getFraction(R.styleable.JoystickSimple_JV_backgroundSizeRatio, 1, 1, 0.75f);
            mButtonDirection = styledAttributes.getInteger(R.styleable.JoystickSimple_JV_buttonDirection, BUTTON_DIRECTION_BOTH);
        } finally {
            styledAttributes.recycle();
        }

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.argb(255,0,0,0));
        mPaintText.setStyle(Paint.Style.FILL);

        mPaintCircleButton = new Paint();
        mPaintCircleButton.setAntiAlias(true);
        mPaintCircleButton.setColor(buttonColor);
        mPaintCircleButton.setStyle(Paint.Style.FILL);

        if (buttonDrawable != null) {
            if (buttonDrawable instanceof BitmapDrawable) {
                mButtonBitmap = ((BitmapDrawable) buttonDrawable).getBitmap();
                mPaintBitmapButton = new Paint();
            }
        }

        mPaintCircleBorder = new Paint();
        mPaintCircleBorder.setAntiAlias(true);
        mPaintCircleBorder.setColor(borderColor);
        mPaintCircleBorder.setStyle(Paint.Style.STROKE);
        mPaintCircleBorder.setStrokeWidth(borderWidth);

        if (borderColor != Color.TRANSPARENT) {
            mPaintCircleBorder.setAlpha(mBorderAlpha);
        }

        mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(backgroundColor);
        mPaintBackground.setStyle(Paint.Style.FILL);
        mRunnableMultipleLongPress = new Runnable() {
            @Override
            public void run() {
                if (mOnMultipleLongPressListener != null)
                    mOnMultipleLongPressListener.onMultipleLongPress();
            }
        };
    }

    private void initPosition() {
        mFixedCenterX = mCenterX = mPosX = getWidth() / 2;
        mFixedCenterY = mCenterY = mPosY = getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBackgroundRadius, mPaintBackground);
        canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBorderRadius, mPaintCircleBorder);
        if (mButtonBitmap != null) {
            canvas.drawBitmap(
                    mButtonBitmap,
                    mPosX + mFixedCenterX - mCenterX - mButtonRadius,
                    mPosY + mFixedCenterY - mCenterY - mButtonRadius,
                    mPaintBitmapButton
            );
        }
        else {
            canvas.drawCircle(
                    mPosX + mFixedCenterX - mCenterX,
                    mPosY + mFixedCenterY - mCenterY,
                    mButtonRadius,
                    mPaintCircleButton
            );
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        initPosition();
        int d = Math.min(w, h);
        mButtonRadius = (int) (d / 2 * mButtonSizeRatio);
        mBorderRadius = (int) (d / 2 * mBackgroundSizeRatio);
        mBackgroundRadius = mBorderRadius - (mPaintCircleBorder.getStrokeWidth() / 2);

        if (mButtonBitmap != null)
            mButtonBitmap = Bitmap.createScaledBitmap(mButtonBitmap, mButtonRadius * 2, mButtonRadius * 2, true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            return DEFAULT_SIZE;
        } else {
            return MeasureSpec.getSize(measureSpec);
        }
    }
    /*
    USER EVENT
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnabled) {
            return true;
        }
        mPosY = mButtonDirection < 0 ? mCenterY : (int) event.getY(); // direction negative is horizontal axe
        mPosX = mButtonDirection > 0 ? mCenterX : (int) event.getX(); // direction positive is vertical axe

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mThread.interrupt();
            if (mAutoReCenterButton) {
                resetButtonPosition();
                if (mCallback != null)
                    mCallback.onMove(getAngle(), getStrength());
                client.resumeThread();
                client.setTwist(getAngle(), getStrength());
                client.pauseThread();
            }
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
            }

            mThread = new Thread(this);
            mThread.start();

            if (mCallback != null)
                mCallback.onMove(getAngle(), getStrength());
            client.resumeThread();
            client.setTwist(getAngle(), getStrength());
            client.pauseThread();
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mFixedCenter) {
                    mCenterX = mPosX;
                    mCenterY = mPosY;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                if (event.getPointerCount() == 2) {
                    mHandlerMultipleLongPress.postDelayed(mRunnableMultipleLongPress, ViewConfiguration.getLongPressTimeout()*2);
                    mMoveTolerance = MOVE_TOLERANCE;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
                mMoveTolerance--;
                if (mMoveTolerance == 0) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                if (event.getPointerCount() == 2) {
                    mHandlerMultipleLongPress.removeCallbacks(mRunnableMultipleLongPress);
                }
                break;
            }
        }

        double abs = Math.sqrt((mPosX - mCenterX) * (mPosX - mCenterX)
                + (mPosY - mCenterY) * (mPosY - mCenterY));
        if (abs > mBorderRadius || (mButtonStickToBorder && abs != 0)) {
            mPosX = (int) ((mPosX - mCenterX) * mBorderRadius / abs + mCenterX);
            mPosY = (int) ((mPosY - mCenterY) * mBorderRadius / abs + mCenterY);
        }

        if (!mAutoReCenterButton) {
            if (mCallback != null)
                mCallback.onMove(getAngle(), getStrength());
        }

        client.resumeThread();
        client.setTwist(getAngle(), getStrength());
        client.pauseThread();
        invalidate();


        return true;
    }

    /*
    GETTERS
     */

    private int getAngle() {
        int angle = (int) Math.toDegrees(Math.atan2(mCenterY - mPosY, mPosX - mCenterX));
        return angle < 0 ? angle + 360 : angle;
    }

    private int getStrength() {
        return (int) (100 * Math.sqrt((mPosX - mCenterX)
                * (mPosX - mCenterX) + (mPosY - mCenterY)
                * (mPosY - mCenterY)) / mBorderRadius);
    }

    public void resetButtonPosition() {
        mPosX = mCenterX;
        mPosY = mCenterY;
    }

    public int getButtonDirection() {
        return mButtonDirection;
    }
    public boolean isEnabled() {
        return mEnabled;
    }
    public float getButtonSizeRatio() {
        return mButtonSizeRatio;
    }
    public float getmBackgroundSizeRatio() {
        return mBackgroundSizeRatio;
    }
    public boolean isAutoReCenterButton() {
        return mAutoReCenterButton;
    }
    public boolean isButtonStickToBorder() {
        return mButtonStickToBorder;
    }
    public int getNormalizedX() {
        if (getWidth() == 0) {
            return 50;
        }
        return Math.round((mPosX-mButtonRadius)*100.0f/(getWidth()-mButtonRadius*2));
    }
    public int getNormalizedY() {
        if (getHeight() == 0) {
            return 50;
        }
        return Math.round((mPosY-mButtonRadius)*100.0f/(getHeight()-mButtonRadius*2));
    }
    public int getBorderAlpha() {
        return mBorderAlpha;
    }
    /*
    SETTERS
     */
    public void setButtonDrawable(Drawable d) {
        if (d != null) {
            if (d instanceof BitmapDrawable) {
                mButtonBitmap = ((BitmapDrawable) d).getBitmap();

                if (mButtonRadius != 0) {
                    mButtonBitmap = Bitmap.createScaledBitmap(
                            mButtonBitmap,
                            mButtonRadius * 2,
                            mButtonRadius * 2,
                            true);
                }

                if (mPaintBitmapButton != null)
                    mPaintBitmapButton = new Paint();
            }
        }
    }
    public void setButtonColor(int color) {
        mPaintCircleButton.setColor(color);
        invalidate();
    }
    public void setBorderColor(int color) {
        mPaintCircleBorder.setColor(color);
        if (color != Color.TRANSPARENT) {
            mPaintCircleBorder.setAlpha(mBorderAlpha);
        }
        invalidate();
    }
    public void setBorderAlpha(int alpha) {
        mBorderAlpha = alpha;
        mPaintCircleBorder.setAlpha(alpha);
        invalidate();
    }
    @Override
    public void setBackgroundColor(int color) {
        mPaintBackground.setColor(color);
        invalidate();
    }
    public void setBorderWidth(int width) {
        mPaintCircleBorder.setStrokeWidth(width);
        mBackgroundRadius = mBorderRadius - (width / 2.0f);
        invalidate();
    }
    public void setOnMoveListener(OnMoveListener l) {
        setOnMoveListener(l, DEFAULT_LOOP_INTERVAL);
    }

    public void setOnMoveListener(OnMoveListener l, int loopInterval) {
        mCallback = l;
        mLoopInterval = loopInterval;
    }
    public void setOnMultiLongPressListener(OnMultipleLongPressListener l) {
        mOnMultipleLongPressListener = l;
    }
    public void setFixedCenter(boolean fixedCenter) {
        // if we set to "fixed" we make sure to re-init position related to the width of the joystick
        if (fixedCenter) {
            initPosition();
        }
        mFixedCenter = fixedCenter;
        invalidate();
    }
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }
    public void setButtonSizeRatio(float newRatio) {
        if (newRatio > 0.0f & newRatio <= 1.0f) {
            mButtonSizeRatio = newRatio;
        }
    }
    public void setBackgroundSizeRatio(float newRatio) {
        if (newRatio > 0.0f & newRatio <= 1.0f) {
            mBackgroundSizeRatio = newRatio;
        }
    }
    public void setAutoReCenterButton(boolean b) {
        mAutoReCenterButton = b;
    }
    public void setButtonStickToBorder(boolean b) {
        mButtonStickToBorder = b;
    }
    public void setButtonDirection(int direction) {
        mButtonDirection = direction;
    }

    /*
    IMPLEMENTS
     */
    @Override // Runnable
    public void run() {
        while (!Thread.interrupted()) {
            post(new Runnable() {
                public void run() {
                    if (mCallback != null)
                        mCallback.onMove(getAngle(), getStrength());
                }
            });

            try {
                Thread.sleep(mLoopInterval);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}