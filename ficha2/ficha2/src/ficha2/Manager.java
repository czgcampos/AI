package ficha2;

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

public class Manager extends Agent {
	private AID[] agents;
	protected void setup() {
		super.setup();
		CyclicBehaviour comportamento = new CyclicBehaviour(this) {
			public void action() {
				float x=0,y=0,xt=0,yt=0,dist=200,aux=0;
				AID taxi=null;
				ACLMessage m = receive();
				if (m!=null) {
					x = Float.parseFloat(m.getContent());
				}
				m = receive();
				if (m!=null) {
					y = Float.parseFloat(m.getContent());
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Traveling");
					template.addServices(sd);
					DFAgentDescription[] result;
					try {
						result=DFService.search(myAgent, template);
						agents = new AID[result.length];
						ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
						for(int i=0;i<result.length;++i,aux=0) {
							agents[i]=result[i].getName();
							msg.addReceiver(agents[i]);
							msg.setContent("Tenho cliente à espera...");
							send(msg);
							msg = receive();
							if(msg!=null && msg.getPerformative()==0) {
								xt = Float.parseFloat(msg.getContent());
							}
							msg = receive();
							if(msg!=null && msg.getPerformative()==0) {
								yt = Float.parseFloat(msg.getContent());
							}
							aux=(float) Math.sqrt((x-xt)*(x-xt) + (y-yt)*(y-yt));
							if(dist>aux) {
								dist=aux;
								taxi=agents[i];
							}
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.addReceiver(taxi);
							msg2.setContent(""+x);
							send(msg2);
							msg2.setContent(""+y);
							send(msg2);
							msg = receive();
							ACLMessage reply = msg.createReply();
		                    reply.setPerformative( ACLMessage.INFORM );
		                    reply.setContent("Chegou ao destino");
		                    send(reply);
						}
						
					}catch(FIPAException e) {
						e.printStackTrace();
					}
				}
			}
		};
		this.addBehaviour(comportamento);
	}
}