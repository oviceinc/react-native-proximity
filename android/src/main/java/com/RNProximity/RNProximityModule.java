
package com.RNProximity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

public class RNProximityModule extends ReactContextBaseJavaModule implements SensorEventListener {

  private static final String TAG = "RNProximityModule";
  private static final String KEY_PROXIMITY = "proximity";
  private static final String KEY_DISTANCE = "distance";
  private static final String KEY_EVENT_ON_SENSOR_CHANGE = "EVENT_ON_SENSOR_CHANGE";
  private static final String EVENT_ON_SENSOR_CHANGE = "ProximityStateDidChange";
  private final ReactApplicationContext reactContext;

  private SensorManager mSensorManager;
  private Sensor mProximity;
  private PowerManager mPowerManager;
  private PowerManager.WakeLock mWakeLock;

  public RNProximityModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    mSensorManager = (SensorManager)reactContext.getSystemService(Context.SENSOR_SERVICE);
    mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    mPowerManager = (PowerManager)this.reactContext.getSystemService(ReactApplicationContext.POWER_SERVICE);
  }

  public void turnOnScreen(){
    // turn on screen
    if (mWakeLock != null && mWakeLock.isHeld()){
      mWakeLock.release();
    }
  }

  public void turnOffScreen(){
    // turn off screen
    if (mWakeLock == null){
      mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "contagt:tag");
    }
    if (!mWakeLock.isHeld()){
      mWakeLock.acquire();
    }
  }

  public void sendEvent(String eventName, @Nullable WritableMap params) {
    this.reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  @ReactMethod
  public void proximityEnabled(boolean enabled) {
    if (enabled){
      mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    } else {
      mSensorManager.unregisterListener(this);
    }
  }

  @Override
  public String getName() {
    return "RNProximity";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(KEY_EVENT_ON_SENSOR_CHANGE, EVENT_ON_SENSOR_CHANGE);
    return constants;
  }

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    WritableMap params = Arguments.createMap();

    double distance = sensorEvent.values[0];
    double maximumRange = mProximity.getMaximumRange();
    boolean isNearDevice = distance < maximumRange;

    if (isNearDevice) {
      turnOffScreen();
    } else {
      turnOnScreen();
    }

    params.putBoolean(KEY_PROXIMITY, isNearDevice);
    params.putDouble(KEY_DISTANCE, distance);

    sendEvent(EVENT_ON_SENSOR_CHANGE, params);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }
}
