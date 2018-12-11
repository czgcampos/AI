package ficha1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/* Versao 1
public class Buyer extends Agent {
	protected void setup() {
		super.setup();
		TickerBehaviour comportamento = new TickerBehaviour(this,1000) {
			protected void onTick() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID reader = new AID("Seller", AID.ISLOCALNAME);
				msg.addReceiver(reader);
				msg.setContent("Quero comprar!");
				System.out.println(msg.getContent());
				send(msg);
				msg = receive();
				if (msg != null)
					System.out.println(msg.getContent());
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}*/

public class Buyer extends Agent {
	private AID[] agents;
	protected void setup() {
		super.setup();
		TickerBehaviour comportamento = new TickerBehaviour(this,1000) {
			protected void onTick() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Buy-Sell");
				template.addServices(sd);
				try {
					DFAgentDescription[] result=DFService.search(myAgent, template);
					agents = new AID[result.length];
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					for(int i=0;i<result.length;++i) {
						agents[i]=result[i].getName();
						msg.addReceiver(agents[i]);
						msg.setContent("Quero comprar!");
						System.out.println(msg.getContent());
						send(msg);
					}
					msg = receive();
					if (msg != null)
						System.out.println(msg.getContent());
				}catch(FIPAException e) {
					e.printStackTrace();
				}
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}