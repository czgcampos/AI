package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class SenderAgent extends Agent {

	protected void setup() {

		// Prepare Message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		AID reader = new AID("Receiver", AID.ISLOCALNAME);
		msg.addReceiver(reader);
		msg.setContent("Hello Receiver. I request a new home to live. == from "
				+ getLocalName());

		// Send Message
		send(msg);

		// Receive Reply
		addBehaviour(new SimpleBehaviour() {

			public boolean done() {
				// TODO Auto-generated method stub
				return false;
			}

			public void action() {
				// TODO Auto-generated method stub
				ACLMessage msg = receive();
				if (msg != null)
					System.out.println(" == Answer" + " <- " + msg.getContent()
							+ " == from " + msg.getSender().getName());
				/*
				 * System.out.println( msg.getPostTimeStamp() + " == Answer" +
				 * " <- " + msg.getContent() + " == from " +
				 * msg.getSender().getName() );
				 */
				block();
			}
		});
		

	}

	protected void takeDown() {
	}
}
