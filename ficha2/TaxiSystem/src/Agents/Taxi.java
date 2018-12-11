package Agents;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Taxi extends Agent {
	int x, y;

	@Override
	protected void setup() {
		System.out.println("Starting Taxi");
		super.setup();
		Random rand = new Random();
		x = rand.nextInt(100);
		y = rand.nextInt(100);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("taxi");
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new Receiver());
	}

	@Override
	protected void takeDown() {
		System.out.println("Ending Taxi");
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.takeDown();

	}

	private class Receiver extends CyclicBehaviour {
		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.CFP) {
					System.out.println("Call For Proposal Requested!");
					ACLMessage response = msg.createReply();
					response.setPerformative(ACLMessage.INFORM);
					response.setContent("" + x + "," + y);
					send(response);
				} else if (msg.getPerformative() == ACLMessage.CONFIRM) {
					System.out.println("We got a customer!");
					String[] info = msg.getContent().split(",");

					AID receiver = new AID();
					receiver.setLocalName(info[0]);

					ACLMessage mensagem = new ACLMessage(ACLMessage.CONFIRM);
					mensagem.addReceiver(receiver);
					myAgent.send(mensagem);
					x = Integer.parseInt(info[1]);
					y = Integer.parseInt(info[2]);

				}

			} else {
				block();
			}

		}

	}
}
