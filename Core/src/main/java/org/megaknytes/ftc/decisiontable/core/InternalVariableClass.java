package org.megaknytes.ftc.decisiontable.core;


class InternalVariableClass {
    int numInternalVariables;
    double internalVariables[];

    public InternalVariableClass(int numInternalVariables) {
        int i;

        this.numInternalVariables = numInternalVariables;
        internalVariables = new double[numInternalVariables];

        for (i = 0; i < numInternalVariables; i++) {
            internalVariables[i] = 0.0;
        }
    }

    public double get(int channel) {
        if ((channel >= 0) && (channel < numInternalVariables)) {
            return (internalVariables[channel]);
        }
        return (-1.0);
    }

    public void set(int channel, double value) {
        if ((channel >= 0) && (channel < numInternalVariables)) {
            internalVariables[channel] = value;
        }
    }

    public void init(String IOName, int channel, double initVal) {
        internalVariables[channel] = initVal;
        return;
    }
}