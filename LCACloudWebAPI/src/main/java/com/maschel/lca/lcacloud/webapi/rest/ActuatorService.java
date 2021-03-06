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
import com.maschel.lca.message.request.ActuatorRequestMessage;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/actuator")
public class ActuatorService {

    JadeGatewayService jadeGatewayService = new JadeGatewayService();

    @POST
    @Path("/actuate")
    @Consumes(MediaType.APPLICATION_JSON)
    public void actuate(
            @Suspended AsyncResponse asyncResponse,
            @QueryParam("deviceId") String deviceId,
            ActuatorRequestMessage message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean success = getActuateResult();
                Response response;
                if (success == null) {
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                } else if (!success) {
                    response = Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
                } else {
                    response = Response.status(Response.Status.OK).build();
                }
                asyncResponse.resume(response);
            }

            private Boolean getActuateResult() {
                return jadeGatewayService.actuatorRequest(deviceId, message);
            }
        }).start();
    }
}
