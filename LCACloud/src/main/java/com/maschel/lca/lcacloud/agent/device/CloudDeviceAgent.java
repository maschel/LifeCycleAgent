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

package com.maschel.lca.lcacloud.agent.device;

import com.google.gson.Gson;
import com.maschel.lca.lcacloud.agent.device.behaviour.DeviceMessageForwardBehaviour;
import com.maschel.lca.lcacloud.analytics.Analytics;
import com.maschel.lca.lcacloud.model.Device;
import com.maschel.lca.message.response.AnalyticsDataMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;

public class CloudDeviceAgent extends Agent {

    public static final String SENSOR_ONTOLOGY = "sensor";
    public static final String ACTUATOR_ONTOLOGY = "actuator";
    public static final String ANALYTIC_ONTOLOGY = "analytic";

    private Gson gson = new Gson();

    private Device agentDevice;
    private AID remoteDeviceAID;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            String agentDeviceId = (String) args[0];
            agentDevice = new Device(agentDeviceId);
            remoteDeviceAID = (AID) args[1];
        } else {
            System.out.println("ERROR: No model id specified.");
            this.doDelete();
            return;
        }

        addBehaviour(new MessagePerformer(this));
    }

    private class MessagePerformer extends CyclicBehaviour {

        public MessagePerformer(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();

            if(msg != null) {
                if (msg.getOntology() != null && msg.getOntology().equals(ANALYTIC_ONTOLOGY)) {
                    // Store Analytic data
                    try {
                        AnalyticsDataMessage analyticsDataMessage = gson.fromJson(msg.getContent(), AnalyticsDataMessage.class);
                        Analytics.getInstance().store(analyticsDataMessage);
                    } catch(Exception e) {
                        System.out.println("ERROR: Failed to store analytic data, reason: " + e.getMessage());
                    }

                } else {
                    // Forward Message from API to Device
                    myAgent.addBehaviour(new DeviceMessageForwardBehaviour(myAgent, remoteDeviceAID, msg));
                }
            } else {
                block();
            }
        }
    }

    protected void takeDown()
    {
        try {
            DFService.deregister(this);
        }
        catch (Exception e) {
            System.out.println("ERROR: Failed to deregister CloudDeviceAgent from DF. " + e.getMessage());
        }
    }
}
