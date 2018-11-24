package com.example.forrestsu.sensortest.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.forrestsu.sensortest.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer; //加速传感器，静止时受重力影响，某一方向的值为地球重力加速度，约9.8m/s^2
    private Sensor mAmbientTemperature; //环境温度传感器
    private Sensor mGyroscope; //陀螺仪
    private Sensor mLight; //光线传感器
    private Sensor mRotationVector; //旋转矢量

    private TextView allTV, accelerometerTV, ambientTemperatureTV, gyroscopeTV, lightTV, rotationVectorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //获取传感器管理
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //获取当前设备所有传感器
        List<Integer> allSensors = getAllSensors();
        allTV.setText(("当前设备共有" + allSensors.size() + "个传感器"));
        //加速传感器
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //环境温度传感器
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        //陀螺仪
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //光线传感器
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //旋转矢量
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    //初始化控件
    public void initView() {
        allTV = (TextView) findViewById(R.id.tv_all);
        accelerometerTV = (TextView) findViewById(R.id.tv_value_accelerometer);
        accelerometerTV.setText("当前设备没有加速传感器");
        ambientTemperatureTV = (TextView) findViewById(R.id.tv_value_ambient_temperature);
        ambientTemperatureTV.setText("当前设备没有环境温度传感器");
        gyroscopeTV = (TextView) findViewById(R.id.tv_value_gyroscope);
        gyroscopeTV.setText("当前设备没有陀螺仪");
        lightTV = (TextView) findViewById(R.id.tv_light);
        lightTV.setText("当前设备没有光线传感器");
        rotationVectorTV = (TextView) findViewById(R.id.tv_rotation_vector);
        rotationVectorTV.setText("当前设备没有旋转矢量传感器");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //为传感器注册监听
        Log.i(TAG, "onResume: 注册传感器监听");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: 取消注册传感器监听");
        mSensorManager.unregisterListener(this);
    }

    //当传感器的精度变化时回调该方法
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    //当传感器的值变化时回调该方法
    @Override
    public void onSensorChanged(SensorEvent event) {
        float values[] = event.values;
        StringBuilder sb;
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sb = new StringBuilder();
                sb.append("加速传感器（m/s^2）");
                sb.append("\nX轴方向：");
                sb.append(values[0]); //Acceleration minus Gx on the x-axis
                sb.append("\nY轴方向：");
                sb.append(values[1]); //Acceleration minus Gy on the y-axis
                sb.append("\nZ轴方向：");
                sb.append(values[2]); //Acceleration minus Gz on the z-axis
                accelerometerTV.setText(sb.toString());
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sb = new StringBuilder();
                sb.append("环境温度传感器");
                sb.append("\n温度：");
                sb.append(values[0]);
                ambientTemperatureTV.setText(sb.toString());
                break;
            case Sensor.TYPE_GYROSCOPE:
                sb = new StringBuilder();
                sb.append("陀螺仪（rad/s）");
                sb.append("\n围绕X轴的角速度：");
                sb.append(values[0]); //Angular speed around the x-axis
                sb.append("\n围绕Y轴的角速度：");
                sb.append(values[1]); //Angular speed around the y-axis
                sb.append("\n围绕Z轴的角速度：");
                sb.append(values[2]); //Angular speed around the z-axis
                gyroscopeTV.setText(sb.toString());
                break;
            case Sensor.TYPE_LIGHT:
                sb = new StringBuilder();
                sb.append("光线传感器（lx）");
                sb.append("\n当前光线亮度：");
                sb.append(values[0]);
                lightTV.setText(sb.toString());
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sb = new StringBuilder();
                sb.append("旋转矢量");
                sb.append("\n围绕X轴：");
                sb.append(values[0]); //x*sin(θ/2)
                sb.append("\n围绕Y轴：");
                sb.append(values[1]); //y*sin(θ/2)
                sb.append("\n围绕Z轴：");
                sb.append(values[2]); //z*sin(θ/2)
                sb.append("\ncos(θ/2)：");
                sb.append(values[3]); //cos(θ/2)
                sb.append("\nestimated heading Accuracy (in radians)：");
                sb.append(values[4]); //estimated heading Accuracy (in radians) (-1 if unavailable)
                rotationVectorTV.setText(sb.toString());
                break;
            default:
                break;
        }

    }

    /**
     * 获取当前设备所有传感器的类型
     * @return 包含当前设备所有传感器类型的List
     */
    public List<Integer> getAllSensors() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        List<Integer> list = new ArrayList<Integer>();
        for (Sensor sensor : sensors) {
                list.add(sensor.getType());
        }
        return list;
    }

    /**
     * 获取当前设备所有传感器的具体情况
     */
    public void getAllSensorsSituation() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sb = new StringBuilder();
        sb.append("该机共有" + sensors.size() + "个传感器");
        int i = 1;
        String s;
        for (Sensor sensor : sensors) {
            sb.append("\n" + i);
            sb.append("\n名称：" + sensor.getName());
            sb.append("\n供应商：" + sensor.getVendor());
            sb.append("\n版本：" + sensor.getVersion());
            if (Build.VERSION.SDK_INT < 20) {
                sb.append("\n类型：" + sensor.getType());
            } else {
                s = sensor.getStringType();
                sb.append("\n类型：" + (s.substring(s.lastIndexOf(".") + 1)).toUpperCase());
            }
            sb.append("\n######");
            i += 1;
        }
        Log.i(TAG, "getAllSensorsSituation: " + sb.toString());
    }

    /**
     * 判断当前设备是否有这个传感器
     * @param list 当前设备所有的传感器类型
     * @param i 要检查的传感器类型
     * @return
     */
    public boolean isInclude(List<Integer> list, int i) {
        for (int s : list) {
            if (s == i) {
                return true;
            }
        }
        return false;
    }
}
