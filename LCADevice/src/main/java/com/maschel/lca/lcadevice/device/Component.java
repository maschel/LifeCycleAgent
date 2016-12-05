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

package com.maschel.lca.lcadevice.device;

import com.maschel.lca.lcadevice.device.actuator.Actuator;
import com.maschel.lca.lcadevice.device.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Component class
 * This class should be used for adding components to a device, and can have (multiple) sub components (tree structure).
 */
public class Component {

    private List<Component> components = new ArrayList<Component>();
    private List<Actuator> actuators = new ArrayList<Actuator>();
    private List<Sensor> sensors = new ArrayList<Sensor>();

    private String name;

    /**
     * Default component constructor.
     * @param name The name of the component.
     */
    public Component(String name) {
        this.name = name;
    }

    public void add(Component component) {
        components.add(component);
    }
    public void remove(Component component) {
        components.remove(component);
    }
    public void add(Actuator actuator) {
        actuators.add(actuator);
    }
    public void remove(Actuator actuator) {
        actuators.remove(actuator);
    }
    public void add(Sensor sensor) {
        sensors.add(sensor);
    }
    public void remove(Sensor sensor) {
        sensors.remove(sensor);
    }

    /**
     * Update the sensor values of the current component and any sub components.
     */
    public void updateSensors() {
        for (Sensor s: sensors) {
            s.update();
        }
        // Recurse
        for (Component c: components) {
            c.updateSensors();
        }
    }

    /**
     * Get the children of the component (non recursive).
     * @return List of {@link Component}.
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Get the children of the current component and all sub components.
     * @return List of {@link Component}.
     */
    public List<Component> getDescendantComponents() {
        List<Component> components = getComponents();
        for (Component c: components) {
            components.addAll(c.getDescendantComponents());
        }
        return components;
    }

    /**
     * Get the actuators of the component (non recursive).
     * @return List of {@link Actuator}.
     */
    public List<Actuator> getActuators() {
        return actuators;
    }

    /**
     * Get the actuators of the current component and all sub components.
     * @return List of {@link Actuator}.
     */
    public List<Actuator> getDescendantActuators() {
        List<Actuator> act = getActuators();
        for (Component c: components) {
            act.addAll(c.getDescendantActuators());
        }
        return act;
    }

    /**
     * Get a actuator from the current component (or any sub component) by name.
     * @param name The name to search.
     * @return The {@link Component} with the given name or null if not found.
     */
    public Actuator getActuatorByName(String name) {
        for (Actuator a: actuators) {
            if (a.getName().equals(name))
                return a;
        }
        for (Component c: components) {
            if (c.getActuatorByName(name) != null) {
                return c.getActuatorByName(name);
            }
        }
        return null;
    }

    /**
     * Get a sensor from the current component (or andy sub component) by name.
     * @param name The name to search.
     * @return The {@link Sensor} with the given name or null if not found.
     */
    public Sensor getSensorByName(String name) {
        for (Sensor s: sensors) {
            if (s.getName().equals(name))
                return s;
        }
        for (Component c: components) {
            if (c.getSensorByName(name) != null) {
                return c.getSensorByName(name);
            }
        }
        return null;
    }

    /**
     * Get the sensors of the current component (non recursive).
     * @return List of {@link Sensor}
     */
    public List<Sensor> getSensors() {
        return sensors;
    }

    /**
     * Get the sensors of the current component and all (sub)components
     * @return List of {@link Sensor}
     */
    public List<Sensor> getDescendantSensors() {
        List<Sensor> sens = getSensors();
        for (Component c: components) {
            sens.addAll(c.getDescendantSensors());
        }
        return sens;
    }

    /**
     * Get the name of the component
     * @return component name
     */
    public String getName() {
        return this.getName();
    }
}
