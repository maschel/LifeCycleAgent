package nl.hu.msda.device;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * Hello world!
 *
 */
public class TestAgent extends Agent
{
    // The title of the book to buy
    private String targetBookTitle;
    // The list of known seller agents
    private AID[] sellerAgents = {  new AID("seller1", AID.ISLOCALNAME),
                                    new AID("seller2", AID.ISLOCALNAME)};

    // Agent Initialisation
    protected void setup() {
        // Print welcome message
        System.out.println( "Hello! Buyer agent " + getAID().getName() + " is ready." );

        // Get the title of the book to buy as a start-up argument
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetBookTitle = (String) args[0];
            System.out.println("Trying to buy " + targetBookTitle);

            // Add a TickerBehaviour that schedules a request to seller agents every minute
            addBehaviour(new TickerBehaviour(this, 60000) {
                @Override
                protected void onTick() {
                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        }
        else {
            // Make the agent terminate immediately
            System.out.println("No book title specified");
            doDelete();
        }
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Print a dismissal message
        System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
    }
}
