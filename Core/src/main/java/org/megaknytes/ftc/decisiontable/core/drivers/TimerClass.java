package org.megaknytes.ftc.decisiontable.core.drivers;

public class TimerClass {
    int numTimers;
    long[] Timers;

    public TimerClass(int numTimers) {
        int i;

        this.numTimers = numTimers;
        Timers = new long[numTimers];

        for (i = 0; i < numTimers; i++) {
            Timers[i] = 0;
        }
    }

    public double get(int channel) {
        if ((channel >= 0) && (channel < numTimers)) {
            if (System.currentTimeMillis() < (long) Timers[channel]) {
                return (1.0);
            } else {
                return (0.0);
            }
        }
        return (-1.0);
    }

    public void set(int channel, double value) {
        if ((channel >= 0) && (channel < numTimers)) {
            Timers[channel] = System.currentTimeMillis() + (long) (value);
        }
    }

    public void init(String IOName, int channel, double initVal) {
    }
}