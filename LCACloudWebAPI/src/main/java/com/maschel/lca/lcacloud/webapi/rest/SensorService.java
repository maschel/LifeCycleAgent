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

package com.maschel.lca.lcacloud.webapi.rest;

import com.maschel.lca.lcacloud.webapi.gateway.JadeGatewayService;
import com.maschel.lca.message.request.SensorRequestMessage;
import com.maschel.lca.message.response.SensorValueMessage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensor")
public class SensorService {

    JadeGatewayService jadeGatewayService = new JadeGatewayService();

    @GET
    @Path("/value")
    @Produces(MediaType.APPLICATION_JSON)
    public void getSensorValue(
            @Suspended AsyncResponse asyncResponse,
            @QueryParam("deviceId") String deviceId,
            @QueryParam("sensorName") String sensorName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response;
                SensorValueMessage sensorValueMessage = getSensorResult();
                if (sensorValueMessage != null) {
                    response = Response.status(Response.Status.OK).entity(sensorValueMessage).build();
                } else {
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
                asyncResponse.resume(response);
            }

            private SensorValueMessage getSensorResult() {
                SensorRequestMessage sensorRequestMessage = new SensorRequestMessage(sensorName);
                return jadeGatewayService.sensorRequest(deviceId, sensorRequestMessage);
            }
        }).start();
    }

}