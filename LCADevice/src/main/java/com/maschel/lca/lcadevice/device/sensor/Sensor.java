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

package com.maschel.lca.lcadevice.device.sensor;

import com.maschel.lca.lcadevice.device.Device;
import com.maschel.lca.lcadevice.device.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensor class
 * This class should be used for adding sensors to the {@link Device} or {@link Component}.
 * @param <T> The type of the sensor value.
 */
public abstract class Sensor<T> implements ObservableSensor {

    private String name;
    private T value;

    private long minUpdateInterval = 0;
    private long lastSensorRead = 0;

    private List<SensorObserver> observers = new ArrayList<>();

    /**
     * Default Sensor constructor.
     * @param name Name of the sensor.
     */
    public Sensor(String name) {
        this.name = name;
    }

    /**
     * Sensor constructor that sets a minimum sensor update interval.
     * @param name Name of the sensor.
     * @param minUpdateIntervalMillis The minimal sensor update interval.
     */
    public Sensor(String name, long minUpdateIntervalMillis) {
        this.name = name;
        this.minUpdateInterval = minUpdateIntervalMillis;
    }

    /**
     * Read a new sensor value from the device (respecting the interval).
     */
    final public void update() {
        if (lastSensorRead == 0 || (System.currentTimeMillis() > (lastSensorRead + minUpdateInterval))) {
            lastSensorRead = System.currentTimeMillis();
            this.value = readSensor();
            notifyObservers();
        }
    }

    @Override
    final public void registerObserver(SensorObserver obj) {
        observers.add(obj);
    }

    @Override
    final public void unregisterObserver(SensorObserver obj) {
        observers.remove(obj);
    }

    @Override
    final public void notifyObservers() {
        for (SensorObserver observer: observers) {
            observer.sensorUpdateNotification();
        }
    }

    /**
     * Get the name of the sensor
     * @return sensor name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the current value of the sensor (instance)
     * @return sensor value
     */
    public T getValue() {
        this.update();
        return this.value;
    }

    /**
     * Get the type of the sensor value
     * @return Class of sensor value
     */
    public String getType() {
        return getValue().getClass().getCanonicalName();
    }

    /**
     * Read the sensor value from the device.
     * This method should implement the functionality to read the device sensor.
     * @return Sensor value in the type specified by the {@link Sensor} class.
     */
    public abstract T readSensor();
}
