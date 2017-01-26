/*
 *  LifeCycleAgent
 *
 *  MIT License
 *
 *  Copyright (c) 2017
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

package com.maschel.lca.lcacloud.analytics;

import com.maschel.lca.lcacloud.configuration.ConfigurationHelper;
import com.maschel.lca.message.dto.AnalyticsSensorDataDTO;
import com.maschel.lca.message.response.AnalyticsDataMessage;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class Analytics {

    private static Analytics instance = null;

    private final String ANALYTICS_DATABASE = "analytics";

    private MongoClient mongoClient;
    private MongoDatabase database;

    protected Analytics() {
        mongoClient = new MongoClient(ConfigurationHelper.getAnalyticsDBHost(),
                ConfigurationHelper.getAnalyticsDBPort());
        database = mongoClient.getDatabase(ANALYTICS_DATABASE);
    }

    public static Analytics getInstance() {
        if (instance == null) {
            instance = new Analytics();
        }
        return instance;
    }

    public void store(AnalyticsDataMessage analyticsData) {
        for (AnalyticsSensorDataDTO sensorData: analyticsData.values) {

            String collectionName = sensorData.name+"_"+sensorData.aggregate;

            Bson filter = Filters.eq("device-id", analyticsData.deviceId);

            if (!database.getCollection(collectionName).find(filter).iterator().hasNext()) {
                database.getCollection(collectionName).insertOne(
                        new Document()
                                .append("device-id", analyticsData.deviceId)
                                .append("values", new ArrayList<Document>())
                );
            }

            database.getCollection(collectionName).updateOne(filter,
                    Updates.addToSet("values", new Document(sensorData.date, sensorData.value))
            );
        }
    }

}
