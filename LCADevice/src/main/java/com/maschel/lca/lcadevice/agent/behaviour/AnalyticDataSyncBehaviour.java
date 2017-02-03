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
import com.maschel.lca.lcadevice.agent.message.mapper.AnalyticsSensorDataMapper;
import com.maschel.lca.lcadevice.analytics.storage.AnalyticsSensorData;
import com.maschel.lca.lcadevice.config.ConfigurationHelper;
import com.maschel.lca.lcadevice.device.Device;
import com.maschel.lca.message.response.AnalyticsDataMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class AnalyticDataSyncBehaviour extends OneShotBehaviour {

    private static String ANALYTIC_ONTOLOGY = "analytic";
    private final String DEVICE_ID_PARAMETER = "deviceId";

    private AnalyticsSensorDataMapper analyticsSensorDataMapper = new AnalyticsSensorDataMapper();
    private Gson gson = new Gson();

    private Device agentDevice;

    public AnalyticDataSyncBehaviour(Agent agent, Device agentDevice) {
        super(agent);
        this.agentDevice = agentDevice;
    }

    @Override
    public void action() {
        AID commAID = new AID(ConfigurationHelper.getCloudPlatformCommAgentAID(), AID.ISGUID);
        commAID.addAddresses(ConfigurationHelper.getCloudPlatformAddress());

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(commAID);
        msg.addUserDefinedParameter(DEVICE_ID_PARAMETER, agentDevice.getId());
        msg.setOntology(ANALYTIC_ONTOLOGY);

        List<AnalyticsSensorData> sensorDataList = agentDevice.getAnalyticService().getAnalyticsSensorData(false);

        AnalyticsDataMessage content = new AnalyticsDataMessage(
                agentDevice.getId(),
                analyticsSensorDataMapper.ObjectsToDtos(sensorDataList)
        );
        msg.setContent(gson.toJson(content));

        myAgent.send(msg);
    }
}
