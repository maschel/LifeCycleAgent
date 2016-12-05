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

package com.maschel.lca.lcadevice.agent.behaviour;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maschel.lca.message.request.SensorRequestMessage;
import com.maschel.lca.message.response.SensorValueMessage;
import com.maschel.lca.lcadevice.device.Device;
import com.maschel.lca.lcadevice.device.sensor.Sensor;
import com.maschel.lca.lcadevice.agent.message.mapper.SensorMapper;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SensorBehaviour extends OneShotBehaviour {

    private SensorMapper sensorMapper = new SensorMapper();
    private Gson gson = new Gson();

    private Device agentDevice;
    private ACLMessage message;

    public SensorBehaviour(Agent agent, Device agentDevice, ACLMessage msg) {
        super(agent);
        this.agentDevice = agentDevice;
        this.message = msg;
    }

    @Override
    public void action() {

        SensorRequestMessage sensorRequestMessage;
        try {
            sensorRequestMessage = gson.fromJson(
                    message.getContent(),
                    SensorRequestMessage.class
            );
        } catch (JsonSyntaxException ex) {
            sendFailureReply(message, "Invalid JSON syntax");
            return;
        }

        if (sensorRequestMessage.sensor == null || sensorRequestMessage.sensor.equals("")) {
            sendFailureReply(message, "Invalid sensor");
            return;
        }

        Sensor sensor = agentDevice.getSensorByName(sensorRequestMessage.sensor);
        if (sensor == null) {
            sendFailureReply(message, "Sensor not found");
            return;
        }

        ACLMessage reply = message.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        SensorValueMessage message = new SensorValueMessage(
                agentDevice.getId(),
                sensorMapper.ObjectToDto(sensor)
        );
        reply.setContent(gson.toJson(message));

        myAgent.send(reply);
    }

    private void sendFailureReply(ACLMessage msg, String reason) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent(reason);
        myAgent.send(reply);
    }
}
