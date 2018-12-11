package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiverAgent extends Agent {

	protected void setup() 
    {
		System.out.println("Waiting for new Agents == from " + getLocalName());
		
		addBehaviour(new CyclicBehaviour(this) 
		{

			public void action() {
				ACLMessage msg = receive();
				if (msg!=null) {
					/*System.out.println( msg.getPostTimeStamp() + " == Answer" + " <- " 
							 +  msg.getContent() + " == from "
							 +  msg.getSender().getName() );*/
					System.out.println(" == Answer" + " <- " 
							 +  msg.getContent() + " == from "
							 +  msg.getSender().getName() );
					
					ACLMessage reply = msg.createReply();
                    reply.setPerformative( ACLMessage.INFORM );
                    reply.setContent("Welcome to your new home Agent " + msg.getSender().getName()+ ". You are most welcome!");
                    send(reply);
				}
				block();
			 }
		});
	}

	protected void takeDown() {
	}
}
