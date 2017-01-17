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

package com.maschel.lca.lcadevice.agent;

import com.maschel.lca.lcadevice.agent.behaviour.ActuatorBehaviour;
import com.maschel.lca.lcadevice.agent.behaviour.AnalyticDataSyncBehaviour;
import com.maschel.lca.lcadevice.agent.behaviour.CommBehaviour;
import com.maschel.lca.lcadevice.agent.behaviour.SensorBehaviour;
import com.maschel.lca.lcadevice.device.Device;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * ControlAgent class
 * The ControlAgent is used to get sensor value requests and actuator commands from the agent platform and forward
 * them to the device implementation. This Class is also responsible for the device lifecycle:
 * <ul>
 *     <li>Load the device class</li>
 *     <li>Setup</li>
 *     <li>Connect</li>
 *     <li>Update(loop)</li>
 *     <li>SendAnalyticData(loop)</li>
 *     <li>(Process requests)</li>
 *     <li>Disconnect</li>
 * </ul>
 */
public class DeviceAgent extends Agent {

    private Device agentDevice;

    /**
     * Setup the agent platform and setup the device.
     */
    protected void setup() {

        // Get agent arguments
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String agentDeviceClassName = (String)args[0];
            // Load Device class
            try {
                agentDevice = loadDeviceClass(agentDeviceClassName);
                agentDevice.setup();
                agentDevice.connect();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                this.doDelete();
                return;
            }
        } else {
            System.out.println("ERROR: No device class provided to DeviceAgent");
            this.doDelete();
            return;
        }

        // Connect to the Cloud platform
        addBehaviour(new CommBehaviour(agentDevice.getId()));

        if (agentDevice.getSensorUpdateInterval() != 0) {
            addBehaviour(new TickerBehaviour(this, agentDevice.getSensorUpdateInterval()) {
                @Override
                protected void onTick() {
                    agentDevice.update();
                    agentDevice.updateSensors();

                    if (agentDevice.shouldPerformAnalyticDataSync()) {
                        addBehaviour(new AnalyticDataSyncBehaviour(myAgent, agentDevice));
                        agentDevice.setLastAnalyticSync(System.currentTimeMillis());
                    }
                }
            });
        }

        addBehaviour(new MessagePerformer(this));

    }

    /**
     * Default MessagePerformer. Is called on a new message and passes it to the correct behaviour based on the
     * ontology specified in the message.
     */
    private class MessagePerformer extends CyclicBehaviour {
        private static final String SENSOR_ONTOLOGY = "sensor";
        private static final String ACTUATOR_ONTOLOGY = "actuator";

        public MessagePerformer(Agent a) {
            super(a);
        }

        @Override
        public void action() {
            MessageTemplate mtPerformative = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mtPerformative);
            if (msg != null) {
                switch (msg.getOntology()) {
                    case SENSOR_ONTOLOGY:
                         // SensorBehaviour, is called on a sensor request message
                        myAgent.addBehaviour(new SensorBehaviour(myAgent, agentDevice, msg));
                        break;
                    case ACTUATOR_ONTOLOGY:
                        // ActuatorBehaviour, is called on a actuator command message
                        myAgent.addBehaviour(new ActuatorBehaviour(myAgent, agentDevice, msg));
                        break;
                }
            } else {
                block();
            }
        }
    }

    protected void takeDown() {
        if (agentDevice != null) {
            agentDevice.disconnect();
            agentDevice.getAnalyticService().closeStorage();
        }
    }

    private Device loadDeviceClass(String agentDeviceClassName) throws Exception {
        try {
            return this.getClass()
                    .getClassLoader()
                    .loadClass(agentDeviceClassName)
                    .asSubclass(Device.class)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new Exception("Failed to load DeviceAgent with class: " + agentDeviceClassName);
        }
    }
}
