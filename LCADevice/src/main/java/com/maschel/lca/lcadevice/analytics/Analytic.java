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

package com.maschel.lca.lcadevice.analytics;

import com.maschel.lca.lcadevice.device.sensor.Sensor;
import com.maschel.lca.lcadevice.device.sensor.SensorObserver;
import com.maschel.lca.lcadevice.analytics.storage.AnalyticsStorage;

public class Analytic implements SensorObserver {

    private Sensor sensor;
    private Aggregates aggregate;
    private TimeRange timeRange;

    private AnalyticsStorage storage;

    public Analytic(Sensor sensor, Aggregates aggregate, TimeRange timeRange) {
        this.sensor = sensor;
        sensor.registerObserver(this);

        this.aggregate = aggregate;
        this.timeRange = timeRange;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Aggregates getAggregate() {
        return aggregate;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setStorage(AnalyticsStorage storage) {
        this.storage = storage;
    }

    public String getCurrentDescription() {
        return this.getSensor().getName() + "_" +
                this.getAggregate().getDescription() + "_" +
                this.getTimeRange().getCurrentTimeString();
    }

    public String toString() {
        return this.getSensor().getName() + "_" +
                this.getAggregate().getDescription() + "_" +
                this.getTimeRange().getDescription();
    }

    @Override
    public void sensorUpdateNotification() {
        if (storage != null) {
            storage.store(this);
        }
    }
}
