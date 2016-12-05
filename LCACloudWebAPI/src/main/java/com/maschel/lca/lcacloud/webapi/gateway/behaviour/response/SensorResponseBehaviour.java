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

package com.maschel.lca.lcacloud.webapi.gateway.behaviour.response;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maschel.lca.lcacloud.webapi.gateway.GatewayService;
import com.maschel.lca.message.response.SensorValueMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SensorResponseBehaviour extends OneShotBehaviour {

    private SensorValueMessage result;

    Gson gson = new Gson();

    public SensorResponseBehaviour() {
    }

    @Override
    public void action() {
        MessageTemplate performativeTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        MessageTemplate ontologyTemplate = MessageTemplate.MatchOntology(GatewayService.SENSOR_ONTOLOGY);
        MessageTemplate andTemplate = MessageTemplate.and(performativeTemplate, ontologyTemplate);

        ACLMessage msg = myAgent.blockingReceive(andTemplate);

        if (msg != null) {
            try {
                result = gson.fromJson(msg.getContent(), SensorValueMessage.class);
            } catch (JsonSyntaxException ex) {
                System.out.println("ERROR: Unexpected JSON Object");
            }
        }
    }

    public SensorValueMessage getResult() {
        return result;
    }
}
