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

package com.maschel.lca.lcadevice.example;

import com.maschel.lca.lcadevice.analytics.AggregateOperator;
import com.maschel.lca.lcadevice.analytics.Analytic;
import com.maschel.lca.lcadevice.analytics.TimeRange;
import com.maschel.lca.lcadevice.device.Device;
import com.maschel.lca.lcadevice.device.actuator.Actuator;
import com.maschel.lca.lcadevice.device.sensor.Sensor;

import java.util.Random;

public class ExampleDevice extends Device {

    private static int deviceCount = 0;

    private static final long SENSOR_UPDATE_INTERVAL = 10000;

    public ExampleDevice() {
        super("TestDevice-"+deviceCount, 0, SENSOR_UPDATE_INTERVAL);
        deviceCount++;
    }

    @Override
    public void setup() {

        Sensor testSensor = new Sensor("TestSensor", SENSOR_UPDATE_INTERVAL) {
            @Override
            public Double readSensor() {
                double start = 0;
                double end = 100;
                double random = new Random().nextDouble();
                System.out.println("TestSensor: " + random);
                return start + (random * (end - start));
            }
        };

        this.addDeviceSensor(testSensor);

        this.addDeviceActuator(new Actuator<Double>("TestActuator") {
            @Override
            public void actuate(Double arg) {
                System.out.println("TestActuator actuate: " + arg.toString());
            }
        });

        this.addDeviceActuator(new Actuator<ExampleArgument>("TestArgActuator") {
            @Override
            public void actuate(ExampleArgument arg) {
                System.out.println("Speed: " + arg.getSpeed() + ", angle: " + arg.getAngle());
            }
        });

        this.getAnalyticService().registerAnalytic(new Analytic(testSensor, AggregateOperator.TOTAL(0.0), TimeRange.MINUTE));

    }

    @Override
    public void connect() {

    }

    @Override
    public void update() {

    }

    @Override
    public Boolean deviceReadyForAnalyticDataSync() {
        return true;
    }

    @Override
    public void disconnect() {
        deviceCount--;
    }
}
