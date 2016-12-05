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
import com.maschel.lca.message.request.ActuatorRequestMessage;
import com.maschel.lca.lcadevice.device.Device;
import com.maschel.lca.lcadevice.device.actuator.Actuator;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ActuatorBehaviour extends OneShotBehaviour {

    private Gson gson = new Gson();

    private Device agentDevice;
    private ACLMessage message;

    public ActuatorBehaviour(Agent agent, Device agentDevice, ACLMessage msg) {
        super(agent);
        this.agentDevice = agentDevice;
        this.message = msg;
    }

    @Override
    public void action() {

        ActuatorRequestMessage actuateMessage;
        try {
            actuateMessage = gson.fromJson(
                    message.getContent(),
                    ActuatorRequestMessage.class
            );
        } catch (JsonSyntaxException ex) {
            sendFailureReply(message, "Invalid JSON syntax");
            return;
        }

        if (actuateMessage.actuator == null || actuateMessage.actuator.equals("")) {
            sendFailureReply(message, "No actuator name specified");
            return;
        }

        Actuator actuator = agentDevice.getActuatorByName(actuateMessage.actuator);
        if (actuator == null) {
            sendFailureReply(message, "Could not find actuator: " + actuateMessage.actuator);
            return;
        }

        try {
            actuator.actuate(actuator.getParsedArgumentInstance(actuateMessage.arguments));
        } catch (Exception e) {
            sendFailureReply(message, "Failed to actuate: " + e.getMessage());
            return;
        }

        sendSuccessReply(message);
    }

    private void sendFailureReply(ACLMessage msg, String reason) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent(reason);
        myAgent.send(reply);
    }

    private void sendSuccessReply(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.AGREE);
        myAgent.send(reply);
    }
}
