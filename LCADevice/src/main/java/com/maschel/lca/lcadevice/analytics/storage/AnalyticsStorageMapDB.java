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

package com.maschel.lca.lcadevice.analytics.storage;

import com.maschel.lca.lcadevice.analytics.Analytic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerArray;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnalyticsStorageMapDB implements AnalyticsStorage {

    private static DB db = null;
    private static String database_file = "analytics.db";
    private static int connections = 0;

    private static final int COMMIT_INTERVAL = 1000;
    private static long lastCommitMillis = 0;

    private String analytics_map_name;
    private HTreeMap<Object[], Object> analyticsMap = null;

    public AnalyticsStorageMapDB(String deviceId) {
        analytics_map_name = "analytics" + deviceId;

        connections++;
    }

    private void openDatabase() {
        if (db == null) {
            db = DBMaker.fileDB(database_file)
                    .fileMmapEnableIfSupported()
                    .transactionEnable()
                    .closeOnJvmShutdown()
                    .make();
        }
        if (analyticsMap == null) {
            analyticsMap = db.hashMap(analytics_map_name)
                    .keySerializer(new SerializerArray(Serializer.STRING)).valueSerializer(Serializer.JAVA)
                    .createOrOpen();
        }
    }

    @Override
    public void store(Analytic analytic) {

        openDatabase();

        Object[] currentKey = new String[]{
                analytic.getSensor().getName(),
                analytic.getAggregate().getDescription(),
                analytic.getTimeRange().getCurrentTimeString()
        };

        if(!analyticsMap.containsKey(currentKey)) {
            analyticsMap.put(currentKey, analytic.getAggregate().getDefaultValue());
        }

        Object currentValue = analyticsMap.get(currentKey);
        analyticsMap.put(currentKey, analytic.getAggregate().calculate(currentValue, analytic.getSensor().getValue()));

        commit();
    }

    @Override
    public List<AnalyticsSensorData> getCurrentData(Boolean purgeData) {

        openDatabase();

        Set<Map.Entry<Object[], Object>> currentSet = analyticsMap.getEntries();
        if (purgeData) {
            analyticsMap.clear();
            db.commit();
        }

        return currentSet.stream().map(entry -> new AnalyticsSensorData(
                (String) entry.getKey()[0],
                (String) entry.getKey()[1],
                (String) entry.getKey()[2],
                entry.getValue()
        )).collect(Collectors.toList());
    }

    private static void commit() {
        if (lastCommitMillis == 0L || ((System.currentTimeMillis() - lastCommitMillis) > COMMIT_INTERVAL)) {
            lastCommitMillis = System.currentTimeMillis();
            db.commit();
        }
    }

    @Override
    public void close() {
        connections--;
        analyticsMap = null;

        if(connections == 0 && db != null && !db.isClosed()) {
            db.close();
            db = null;
        }
    }
}
