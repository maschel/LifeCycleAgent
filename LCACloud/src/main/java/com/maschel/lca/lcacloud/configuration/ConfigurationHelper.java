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

package com.maschel.lca.lcacloud.configuration;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationHelper {

    private static String PROPERTIES_FILE = "cloudconfig.properties";
    private static Properties properties = new Properties();

    static {
        InputStream inputStream = ConfigurationHelper.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Cloud property file '" + PROPERTIES_FILE + "' not found in the classpath.");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getAnalyticsDBHost() {
        return properties.getProperty("analytics_db_host", "localhost");
    }

    public static Integer getAnalyticsDBPort() {
        return Integer.parseInt(properties.getProperty("analytics_db_port", "27017"));
    }
}
