package com.eightball.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final float[] rotationsAngles = new float[]{26f, 20f, 10f, 15f, 16f, 56f};
    private Random random;
    private int animationRun = 0;
    private String[] predictions;
    private SensorManager sensorManager;
    private float accel;
    private float accelCurrent;
    private float accelLast;
    private ImageView triangle, center;
    private TextView result;
    private RelativeLayout mainBall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random = new Random();

        mainBall = findViewById(R.id.ballMain);
        mainBall.setPivotX(mainBall.getWidth() / 2f);
        mainBall.setPivotY(mainBall.getHeight() / 2f);

        predictions = getResources().getStringArray(R.array.predictions);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(mSensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;

        center = findViewById(R.id.imageViewCenter);
        triangle = findViewById(R.id.triangle);
        result = findViewById(R.id.result);

    }

    private void showNextPredictions() {
        if(animationRun == 0) {
            center.setAlpha(0f);
            result.setText(predictions[random.nextInt(predictions.length)]);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
            alphaAnimation.setDuration(2000);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animationRun++;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animationRun--;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            RotateAnimation rotateAnimation = new RotateAnimation(
                    rotationsAngles[random.nextInt(rotationsAngles.length)] + 20f,
                    0f, RotateAnimation.RELATIVE_TO_PARENT, RotateAnimation.RELATIVE_TO_PARENT);
            int fromX = random.nextInt(mainBall.getWidth()/2);
            int fromY = random.nextInt(mainBall.getHeight()/2);
            TranslateAnimation translateAnimation = new TranslateAnimation(random.nextBoolean()? -fromX: fromX,
                    0, random.nextBoolean() ? -fromY : fromY, 0);
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    mainBall.getScaleX() * 0.8f, mainBall.getScaleX(),
                    mainBall.getScaleY() * 0.8f, mainBall.getScaleY());
            translateAnimation.setDuration(2000);
            rotateAnimation.setDuration(2000);
            scaleAnimation.setDuration(2000);
            rotateAnimation.setFillAfter(true);
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(scaleAnimation);
            mainBall.startAnimation(animationSet);
            alphaAnimation.setStartOffset(500);
            triangle.startAnimation(alphaAnimation);
            result.startAnimation(alphaAnimation);
        }
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = accelCurrent - accelLast;
            accel = accel * 0.9f + delta;
            if (accel > 12) {
                showNextPredictions();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}