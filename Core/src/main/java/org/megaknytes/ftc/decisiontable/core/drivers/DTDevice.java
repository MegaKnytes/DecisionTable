package org.megaknytes.ftc.decisiontable.core.drivers;

import org.megaknytes.ftc.decisiontable.core.xml.parameters.ParameterRegistry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Represents a device driver in the decision table.
 * <p>
 * This interface defines the driver structure for devices that can be set up and have parameters registered
 * within the decision table. Implementing classes should provide specific behavior for
 * device setup and parameter registration.
 */
public interface DTDevice {

    /**
     * Sets up the initial configuration of the device
     *
     * @param opMode     The OpMode instance that is currently running.
     * @param deviceName The name of the device to be set up.
     */
    void setup(OpMode opMode, String deviceName);

    /**
     * Registers parameters for the device in the provided parameter registry.
     *
     * @param deviceName The name of the device for which parameters are being registered.
     * @param registry   The parameter registry where the parameters will be registered.
     */
    void registerParameters(String deviceName, ParameterRegistry registry);

}