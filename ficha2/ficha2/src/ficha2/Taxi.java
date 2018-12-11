package ficha2;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Taxi extends Agent {
	private float x_pos;
	private float y_pos;
	private boolean available;
	
	protected void setup() {
		super.setup();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Traveling");
		sd.setName("Taxi");
		dfd.addServices(sd);
		try {
			DFService.register(this,dfd);
		}catch(FIPAException e) {
			e.printStackTrace();
		}
		Random r = new Random();
		x_pos = r.nextFloat() * (100);
		y_pos = r.nextFloat() * (100);
		available=true;
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
				if (msg!=null && msg.getPerformative()==11) {
					ACLMessage reply = msg.createReply();
                    reply.setPerformative( ACLMessage.ACCEPT_PROPOSAL );
                    reply.setContent(""+x_pos);
                    send(reply);
                    reply.setContent(""+y_pos);
                    send(reply);
				}
			}
		});
		comportamento.addSubBehaviour(new TickerBehaviour(this,10000) {
			protected void onTick() {
				ACLMessage msg = receive();
				if (msg!=null && msg.getPerformative()==7) {
					msg = receive();
					if(msg!=null && msg.getPerformative()==0) {
						x_pos = Float.parseFloat(msg.getContent());
					}
					msg = receive();
					if(msg!=null && msg.getPerformative()==0) {
						y_pos = Float.parseFloat(msg.getContent());
					}
				}
			}			
		});
	}
	protected void takeDown() {
		super.takeDown();
	}
}