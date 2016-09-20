package nl.hu.msda.device;

import jade.core.Agent;

/**
 * Hello world!
 *
 */
public class TestAgent extends Agent
{
    protected void setup() {
        System.out.println( "Hello World! agent: " + getAID().getName() + " is ready." );
    }
}
