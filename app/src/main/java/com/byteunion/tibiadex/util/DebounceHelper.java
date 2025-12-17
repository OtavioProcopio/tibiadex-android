package com.byteunion.tibiadex.util;

import android.os.Handler;
import android.os.Looper;

public class DebounceHelper {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public void debounce(Runnable action, long delayMillis) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        runnable = () -> action.run();
        handler.postDelayed(runnable, delayMillis);
    }

    public void cancel() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
