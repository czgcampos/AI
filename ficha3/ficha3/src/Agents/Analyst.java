package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Analyst extends Agent {
	protected void setup() {
		super.setup();
		TickerBehaviour comportamento = new TickerBehaviour(this,1000) {
			protected void onTick() {
				try {
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("seller");
					dfd.addServices(sd);
					DFAgentDescription[] results = DFService.search(this.myAgent, dfd);
					if (results.length > 0) {
						for (int i = 0; i < results.length; ++i) {
							DFAgentDescription dfd1 = results[i];
							AID provider = dfd1.getName();
							ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
							msg.addReceiver(provider);
							msg.setContent("Quero saber coisas!");
							send(msg);
							msg = receive();
							if (msg != null && msg.getPerformative() == ACLMessage.CONFIRM) {
								String[] args = msg.getContent().split(",");
								System.out.println("Valor Angariado: " + args[0]);
								System.out.println("Valor Media: " + args[1]);
								System.out.println("Produto mais vendido: " + args[2]);
							}
						}
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}