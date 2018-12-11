package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgenteRecebe extends Agent {
	protected void setup() {
		CyclicBehaviour comportamento = new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg!=null)
					System.out.println(msg.getContent());
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}