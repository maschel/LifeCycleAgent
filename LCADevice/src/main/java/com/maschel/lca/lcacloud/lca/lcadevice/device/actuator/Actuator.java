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

package com.maschel.lca.lcacloud.lca.lcadevice.device.actuator;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Actuator class
 * This class should be used to add Actuators to a device or component.
 * @param <T> The type of the actuator arguments (java type or {@link Argument}).
 */
public abstract class Actuator<T> {

    private String name;
    private Class<T> argumentClass;

    /**
     * Default actuator constructor.
     * This constructor automatically sets the argumentClass attribute to the type of T
     * @param name The name of the Actuator
     */
    @SuppressWarnings("unchecked")
    public Actuator(String name) {
        this.name = name;
        this.argumentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Active a device actuator.
     * This method should implement the functionality to activate a device actuator.
     * @param args The argument needed for the actuator (can be a custom {@link Argument} implementation or a java type).
     */
    public abstract void actuate(T args);

    /**
     * Get the name of the actuator.
     * @return The actuator name.
     */
    public String getName() {
        return name;
    }

    /**
     * Parse the List of Object arguments (received from the remote agent) to the types expected by the Actuator.
     * This method can be used to call the actuate method like this:
     * testActuator.actuate(testActuator.getParsedArgumentInstance(args))
     * @param args List of Object arguments
     * @return Parsed argument (java type or {@link Argument} implementation).
     * @throws Exception When unable to cast arguments to expected types.
     */
    public T getParsedArgumentInstance(List<Object> args) throws Exception {
        try {

            // Argument void type
            if (argumentClass == Void.class) {
                return null;
            }

            // JAVA composite type
            if (argumentClass == Double.class || argumentClass == Float.class ||
                    argumentClass == Long.class || argumentClass == Integer.class ||
                    argumentClass == Short.class || argumentClass == Character.class ||
                    argumentClass == Byte.class || argumentClass == Boolean.class)
            {
                if (args.size() > 0) {
                    return (T) args.get(0);
                }
            }

            // Argument of type Argument.class
            T argument = argumentClass.newInstance();
            if (Argument.class.isAssignableFrom(argument.getClass())) {
                Argument argInstance = (Argument) argument;
                argInstance.parseRawArguments(args);
                argument = (T) argInstance;
                return argument;
            }

            throw new Exception("Actuator argument type not matching any expected types");

        } catch (Exception ex) {
            Exception e = new IllegalArgumentException("Failed to cast actuator arguments: " + ex.getMessage());
            e.setStackTrace(ex.getStackTrace());
            throw e;
        }
    }
}
