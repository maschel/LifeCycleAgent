/*
 *  LifeCycleAgent
 *
 *  MIT License
 *
 *  Copyright (c) 2016
 *
 *  Geoffrey Mastenbroek, geoffrey.mastenbroek@student.hu.nl
 *  Feiko Wielsma, feiko.wielsma@student.hu.nl
 *  Robbin van den Berg, robbin.vandenberg@student.hu.nl
 *  Arnoud den Haring, arnoud.denharing@student.hu.nl
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.maschel.lca.lcadevice.device;

import com.maschel.lca.lcadevice.analytics.AnalyticService;
import com.maschel.lca.lcadevice.device.actuator.Actuator;
import com.maschel.lca.lcadevice.device.sensor.Sensor;

import java.util.List;

/**
 * Abstract Device class
 * This class should be extended by the device that needs to be connected the JADE agent platform.
 * When the class is extended it can be loaded into the platform:
 * java jade.Boot -gui -agents AgentName:DeviceAgent(org.package.TheDeviceImplementation)
 * The device class has the following lifecycle:
 * <ul>
 *     <li>Load the device class</li>
 *     <li>Setup</li>
 *     <li>Connect</li>
 *     <li>Update(loop)</li>
 *     <li>SendAnalyticData(loop)</li>
 *     <li>(Process requests)</li>
 *     <li>Disconnect</li>
 * </ul>
 */
public abstract class Device {

    private final String deviceId;
    private long sensorUpdateInterval = 0;
    private long minAnalyticSyncInterval = 0;

    private long lastAnalyticSync = System.currentTimeMillis();

    private Component rootComponent;

    private AnalyticService analyticService;

    /**
     * Default Device constructor
     * This should be called using super(id) in any device implementation.
     * @param deviceId The device id
     */
    public Device(String deviceId) {
        this.deviceId = deviceId;
        this.rootComponent = new Component(deviceId);
        this.analyticService = new AnalyticService(deviceId);
    }

    /**
     * Default Device constructor with analyticSync loop
     * This should be called using super(id, minAnalyticSyncInterval) in any device implementation.
     * @param deviceId The device id
     * @param minAnalyticSyncInterval The minimum interval at which the agent should try to sync
     *                                analytic data (0=disabled).
     */
    public Device(String deviceId, long minAnalyticSyncInterval) {
        this(deviceId);
        this.minAnalyticSyncInterval = minAnalyticSyncInterval;
    }

    /**
     * Default Device constructor with analyticSync and sensorUpdate loop
     * This should be called using super(id, minAnalyticSyncInterval, sensorUpdateInterval)in any
     * device implementation.
     * @param deviceId The device id
     * @param minAnalyticSyncInterval The minimum interval at which the agent should try to sync
     *                                analytic data (0=disabled).
     * @param sensorUpdateInterval The interval at which sensors should be updated (0=disabled).
     */
    public Device(String deviceId, long minAnalyticSyncInterval, long sensorUpdateInterval) {
        this(deviceId, minAnalyticSyncInterval);
        this.sensorUpdateInterval = sensorUpdateInterval;
    }

    /**
     * Add a device sensor.
     * @param sensor The sensor to add.
     */
    public final void addDeviceSensor(Sensor sensor) {
        rootComponent.add(sensor);
    }

    /**
     * Add a device actuator.
     * @param actuator The actuator to add.
     */
    public final void addDeviceActuator(Actuator actuator) { rootComponent.add(actuator); }

    /**
     * Add a component to the device.
     * @param component The component to add.
     */
    public final void addComponent(Component component) {
        rootComponent.add(component);
    }

    /**
     * Get all the (sub)components of the device.
     * @return List of the (sub)components.
     */
    public final List<Component> getComponents() {
        return rootComponent.getDescendantComponents();
    }

    /**
     * Searches the device and component tree for a sensor with the given name.
     * @param name Sensor name.
     * @return {@link Sensor} instance with given sensor name or null if not found.
     */
    public final Sensor getSensorByName(String name) { return rootComponent.getSensorByName(name); }

    /**
     * Get all the (sub)sensors of the device
     * @return List of all the (sub)sensors.
     */
    public final List<Sensor> getSensors() {
        return rootComponent.getDescendantSensors();
    }

    /**
     * Searches the device and component tree for a actuator with the given name.
     * @param name Actuator name.
     * @return {@link Actuator} instance with given actuator name or null if not found.
     */
    public final Actuator getActuatorByName(String name) { return rootComponent.getActuatorByName(name); }

    /**
     * Get all the (sub)actuators of the device.
     * @return List of the (sub)actuators.
     */
    public final List<Actuator> getActuators() {
        return rootComponent.getDescendantActuators();
    }

    /**
     * Get the Device id.
     * @return The Device id.
     */
    public final String getId() {
        return deviceId;
    }

    /**
     * Set the last analytic sync time.
     * @param lastAnalyticSync Analytic sync time in millis.
     */
    public final void setLastAnalyticSync(long lastAnalyticSync) {
        this.lastAnalyticSync = lastAnalyticSync;
    }

    /**
     * Get the sensor update interval.
     * @return The sensor update interval.
     */
    public final long getSensorUpdateInterval() {
        return sensorUpdateInterval;
    }

    /**
     * Get the analyticService of the current Device.
     * @return The analytic service.
     */
    public final AnalyticService getAnalyticService() {
        return analyticService;
    }

    /**
     * This updates the (sub)sensors of the device and all its components.
     * Note: this should not be called manually.
     */
    public final void updateSensors() {
        rootComponent.updateSensors();
    }

    /**
     * Check if the device is ready for analytic data synchronisation and the minimal sync interval has passed.
     * @return True if a sync should be performed.
     */
    public final Boolean shouldPerformAnalyticDataSync() {
        return (minAnalyticSyncInterval != 0 && deviceReadyForAnalyticDataSync() &&
                ((System.currentTimeMillis() - lastAnalyticSync) > minAnalyticSyncInterval));
    }

    /**
     * This method will be called when de agent platform has started.
     * Use this method to setup/instantiate the device.
     */
    public abstract void setup();

    /**
     * This method will be called when setup is done.
     * Use this method to open the connect to the device.
     */
    public abstract void connect();

    /**
     * This method will be called on every update (sensorUpdateInterval), before updateSensors.
     * Use this method to update the sensor values in the device library (if applicable).
     */
    public abstract void update();

    /**
     * This method will be called on every update (sensorUpdateInterval), if the return value is
     * true and the minimal analytic data sync interval has passed, the device agent will try to send
     * the available analytic data to the cloud.
     * @return Should return True if the device is ready for sync
     */
    public abstract Boolean deviceReadyForAnalyticDataSync();

    /**
     * This method will be called when the agent(platform) will be stopped.
     * Use this method to clean up any open connections to the device.
     */
    public abstract void disconnect();
}
