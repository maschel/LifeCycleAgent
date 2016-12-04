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

package com.maschel.lca.lcacloud.webapi.gateway.behaviour.request;

import com.google.gson.Gson;
import com.maschel.lca.lcacloud.webapi.gateway.GatewayService;
import com.maschel.lca.lcacloud.webapi.gateway.message.request.SensorRequestMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SensorRequestBehaviour extends OneShotBehaviour {

    private String deviceId;
    private SensorRequestMessage requestMessage;

    private Gson gson = new Gson();

    public SensorRequestBehaviour(String deviceId, SensorRequestMessage requestMessage) {
        this.deviceId = deviceId;
        this.requestMessage = requestMessage;
    }

    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        String localName = GatewayService.CLOUD_AGENT_DEVICE_PREFIX + deviceId;
        msg.addReceiver(new AID(localName, AID.ISLOCALNAME));
        msg.setOntology(GatewayService.SENSOR_ONTOLOGY);
        msg.setContent(gson.toJson(requestMessage));
        myAgent.send(msg);
    }

}
