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

package com.maschel.lca.lcacloud.webapi.gateway;

import com.maschel.lca.lcacloud.webapi.gateway.behaviour.request.SensorRequestBehaviour;
import com.maschel.lca.lcacloud.webapi.gateway.behaviour.response.SensorResponseBehaviour;
import com.maschel.lca.message.request.SensorRequestMessage;
import com.maschel.lca.message.response.SensorValueMessage;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;

public class JadeGatewayService {

    public static final String SENSOR_ONTOLOGY = "sensor";
    public static final String CLOUD_AGENT_DEVICE_PREFIX = "Cloud";

    public SensorValueMessage sensorRequest(String deviceId, SensorRequestMessage message) {

        connectGateway();

        SensorRequestBehaviour requestBehaviour = new SensorRequestBehaviour(deviceId, message);
        SensorResponseBehaviour responseBehaviour = new SensorResponseBehaviour();
        SequentialBehaviour sequence = new SequentialBehaviour();
        sequence.addSubBehaviour(requestBehaviour);
        sequence.addSubBehaviour(responseBehaviour);
        try {
            JadeGateway.execute(sequence);
            return responseBehaviour.getResult();
        } catch (ControllerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void connectGateway() {
        if (!JadeGateway.isGatewayActive()) {
            JadeGateway.init(null, new Properties());
        }
    }
}