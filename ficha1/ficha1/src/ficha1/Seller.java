package ficha1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Seller extends Agent {
	private int total;
	protected void setup() {
		super.setup();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Buy-Sell");
		sd.setName("Seller");
		dfd.addServices(sd);
		try {
			DFService.register(this,dfd);
		}catch(FIPAException e) {
			e.printStackTrace();
		}
		ParallelBehaviour comportamento = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ANY) {
			public int onEnd() {
				doDelete();
				return 0;
			}
		};
		this.addBehaviour(comportamento);
		comportamento.addSubBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg!=null) {
					total+=13;
					ACLMessage reply = msg.createReply();
                    reply.setPerformative( ACLMessage.INFORM );
                    reply.setContent("Comprei");
                    send(reply);
				}
			}			
		});
		comportamento.addSubBehaviour(new TickerBehaviour(this,10000) {
			protected void onTick() {
				System.out.println(""+total);
			}			
		});
	}
	protected void takeDown() {
		super.takeDown();
		try {
			DFService.deregister(this);
		}catch(FIPAException e) {
			e.printStackTrace();
		}
	}
}