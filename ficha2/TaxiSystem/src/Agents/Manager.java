package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Manager extends Agent {

	@Override
	protected void setup() {
		super.setup();
		this.addBehaviour(new Receiver());
	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}

	private class taxiSend extends OneShotBehaviour {

		private AID taxi;

		public taxiSend(AID taxiAID) {
			this.taxi = taxiAID;

		}

		@Override
		public void action() {

			ACLMessage mensagem = new ACLMessage(ACLMessage.CFP);
			mensagem.addReceiver(taxi);
			myAgent.send(mensagem);

		}

	}

	private class Receiver extends CyclicBehaviour {
		private int xOrigin, yOrigin, numTaxis, xDestination, yDestination;
		private int minDistance = 1000;
		private int taxisProcessed = 0;
		private AID closestTaxi;
		private String customerName;

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.REQUEST) {
					System.out.println("Customer requested a vehicle");
					String[] coordinates = msg.getContent().split(",");
					xOrigin = Integer.parseInt(coordinates[0]);
					yOrigin = Integer.parseInt(coordinates[1]);
					xDestination = Integer.parseInt(coordinates[2]);
					yDestination = Integer.parseInt(coordinates[3]);
					customerName = msg.getSender().getLocalName();

					// Time to contact all taxis
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("taxi");
					template.addServices(sd);

					DFAgentDescription[] result;

					try {
						result = DFService.search(myAgent, template);
						AID[] taxis;
						taxis = new AID[result.length];
						numTaxis = result.length;

						ParallelBehaviour pb = new ParallelBehaviour(myAgent, ParallelBehaviour.WHEN_ALL) {

							@Override
							public int onEnd() {
								System.out.println("All taxis inquired.");
								return super.onEnd();
							}
						};
						myAgent.addBehaviour(pb);

						for (int i = 0; i < result.length; ++i) {
							taxis[i] = result[i].getName();
							System.out.println(taxis[i].getName());
							pb.addSubBehaviour(new taxiSend(taxis[i]));
						}

					} catch (FIPAException e) {
						e.printStackTrace();

					}

				} else if (msg.getPerformative() == ACLMessage.INFORM) {
					String[] coordinates = msg.getContent().split(",");
					taxisProcessed++;
					int xTaxi = Integer.parseInt(coordinates[0]);
					int yTaxi = Integer.parseInt(coordinates[1]);
					int distance = (int) Math
							.sqrt(((Math.pow((xTaxi - xOrigin), 2)) + (Math.pow((yTaxi - yOrigin), 2))));
					System.out.println("D" + msg.getSender().getLocalName() + ":" + distance);
					if (distance < minDistance) {
						minDistance = distance;
						closestTaxi = msg.getSender();
					}
					if (taxisProcessed == numTaxis) {
						System.out.println("Taxi Chosen:" + closestTaxi.getName());
						ACLMessage mensagem = new ACLMessage(ACLMessage.CONFIRM);
						mensagem.addReceiver(closestTaxi);
						mensagem.setContent(customerName + "," + xDestination + "," + yDestination);
						myAgent.send(mensagem);
						taxisProcessed = 0;
						minDistance = 1000;
						closestTaxi = null;
					}

				}

			} else {
				block();
			}
		}

	}

}
