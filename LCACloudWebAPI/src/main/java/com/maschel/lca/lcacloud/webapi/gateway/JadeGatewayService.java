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

import com.maschel.lca.lcacloud.webapi.gateway.behaviour.request.ActuatorRequestBehaviour;
import com.maschel.lca.lcacloud.webapi.gateway.behaviour.request.SensorRequestBehaviour;
import com.maschel.lca.lcacloud.webapi.gateway.behaviour.response.ActuatorResponseBehaviour;
import com.maschel.lca.lcacloud.webapi.gateway.behaviour.response.SensorResponseBehaviour;
import com.maschel.lca.message.request.ActuatorRequestMessage;
import com.maschel.lca.message.request.SensorRequestMessage;
import com.maschel.lca.message.response.SensorValueMessage;
import jade.core.behaviours.SequentialBehaviour;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;

public class JadeGatewayService {

    public static final String SENSOR_ONTOLOGY = "sensor";
    public static final String ACTUATOR_ONTOLOGY = "actuator";
    public static final String CLOUD_AGENT_DEVICE_PREFIX = "Cloud";

    private static final long REQUEST_TIMEOUT = 5000;

    public JadeGatewayService() {
        JadeGateway.init(null, new Properties());
    }

    public SensorValueMessage sensorRequest(String deviceId, SensorRequestMessage message) {

        SensorRequestBehaviour requestBehaviour = new SensorRequestBehaviour(deviceId, message);
        SensorResponseBehaviour responseBehaviour = new SensorResponseBehaviour();
        SequentialBehaviour sequence = new SequentialBehaviour();
        sequence.addSubBehaviour(requestBehaviour);
        sequence.addSubBehaviour(responseBehaviour);
        try {
            JadeGateway.execute(sequence, REQUEST_TIMEOUT);
            return responseBehaviour.getResult();
        } catch (ControllerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean actuatorRequest(String deviceId, ActuatorRequestMessage message) {

        ActuatorRequestBehaviour requestBehaviour = new ActuatorRequestBehaviour(deviceId, message);
        ActuatorResponseBehaviour responseBehaviour = new ActuatorResponseBehaviour();
        SequentialBehaviour sequence = new SequentialBehaviour();
        sequence.addSubBehaviour(requestBehaviour);
        sequence.addSubBehaviour(responseBehaviour);
        try {
            JadeGateway.execute(sequence, REQUEST_TIMEOUT);
            return responseBehaviour.getDidSucceed();
        } catch (ControllerException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
