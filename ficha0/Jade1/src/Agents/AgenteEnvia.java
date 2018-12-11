package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgenteEnvia extends Agent {
	private int conta;
	protected void setup() {
		super.setup();
		CyclicBehaviour comportamento = new CyclicBehaviour(this) {
			public void action() {
				conta++;
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID reader = new AID("AgenteRecebe", AID.ISLOCALNAME);
				msg.addReceiver(reader);
				msg.setContent("Hello tone "+conta);
				send(msg);
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}