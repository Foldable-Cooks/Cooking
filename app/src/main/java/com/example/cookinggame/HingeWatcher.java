package com.example.cookinggame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Watches the phone's hinge angle sensor and reports a fold followed by a reopen.
 *
 * Works even when the app is briefly hidden while folded (e.g. the cover screen's
 * "swipe up to continue" prompt), and also counts bending the phone part way,
 * which is the natural "snap" motion. Roughly: 180 = flat open, 0 = closed.
 */
public class HingeWatcher implements SensorEventListener {

    public interface Listener {
        void onFolded();
        void onReopened();
    }

    /** Below this angle counts as folded. */
    private static final float FOLDED_BELOW_DEGREES = 140f;
    /** Above this angle (after folding) counts as open again. */
    private static final float OPEN_ABOVE_DEGREES = 160f;

    private final SensorManager sensorManager;
    private final Sensor hinge;
    private final Listener listener;
    private boolean folded = false;
    private boolean running = false;

    public HingeWatcher(Context context, Listener listener) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.hinge = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE) : null;
        this.listener = listener;
    }

    /** False on phones without a hinge sensor. */
    public boolean isAvailable() {
        return hinge != null;
    }

    public void start() {
        if (hinge == null || running) return;
        folded = false;
        running = true;
        sensorManager.registerListener(this, hinge, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        if (!running) return;
        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float angle = event.values[0];
        if (!folded && angle < FOLDED_BELOW_DEGREES) {
            folded = true;
            listener.onFolded();
        } else if (folded && angle > OPEN_ABOVE_DEGREES) {
            folded = false;
            listener.onReopened();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
