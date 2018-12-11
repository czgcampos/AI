package Agents;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Analyst extends Agent {

	private ArrayList<Integer> profit;
	private ArrayList<Integer> avg_profit;
	private ArrayList<String> product_most_selled;
	private ArrayList<Integer> quantity_most_selled;

	private int container;
	private Location nextSite;

	protected void setup() {
		super.setup();

		profit = new ArrayList<Integer>(3);
		avg_profit = new ArrayList<Integer>(3);
		product_most_selled = new ArrayList<String>(3);
		quantity_most_selled = new ArrayList<Integer>(3);

		container = 1;

		addBehaviour(new ReceberQuery());
		addBehaviour(new VerLucro());

	}

	private class ReceberQuery extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) { // receber pedidos de requisitos

				System.out.println("ANALYST -> Message received from " + msg.getSender().getLocalName() + " -> "
						+ msg.getContent());
				
				String[] parts = msg.getContent().split(";");
				String[] most_selled = parts[2].split(":");

				String agentname = msg.getSender().getLocalName();
				// Get last char from Agent Name: "1", "2", "3" and save in String agentname
				agentname = agentname.substring(getLocalName().length() - 1);

				// Parse information from Message received
				profit.add(Integer.parseInt(agentname)-1, Integer.parseInt(parts[0]));
				avg_profit.add(Integer.parseInt(agentname)-1, Integer.parseInt(parts[1]));
				product_most_selled.add(Integer.parseInt(agentname)-1, most_selled[0]);
				quantity_most_selled.add(Integer.parseInt(agentname)-1, Integer.parseInt(most_selled[1]));

				if (product_most_selled.size() == 3) {
					// Report conclusions and go back to Container1
					System.out.println("--------------------------");
					System.out.println("Containers Report:");
					System.out.println("--------------------------");

					int glob_profit = 0;
					int glob_avg_profit = 0;
					String glob_product = new String();
					int glob_quantity = 0;

					for (int i = 0; i < profit.size(); i++) {
						glob_profit += profit.get(i);
						glob_avg_profit += avg_profit.get(i);
						if (glob_quantity < quantity_most_selled.get(i)) {
							glob_quantity = quantity_most_selled.get(i);
							glob_product = product_most_selled.get(i);
						}

						System.out.println("\tContainer " + (i + 1));
						System.out.println("\t\tProfit: " + profit.get(i));
						System.out.println("\t\tAvg_Profit: " + avg_profit.get(i));
						System.out.println("\t\tProduct Most Selled: " + product_most_selled.get(i));
						System.out.println("\t\tQuantity Selled: " + quantity_most_selled.get(i));
						System.out.println("--------------------------");
					}
					System.out.println("Global Report:");
					System.out.println("--------------------------");
					System.out.println("\t\tProfit: " + glob_profit);
					System.out.println("\t\tAvg_Profit: " + glob_avg_profit);
					System.out.println("\t\tProduct Most Selled: " + glob_product);
					System.out.println("\t\tQuantity Selled: " + glob_quantity);
					System.out.println("--------------------------");

					doDelete();
				}
			}
			else {
				block();
			}
		}
	}

	private class VerLucro extends Behaviour {

		public void action() {

			// Jump to Next Container
			// nextSite = new jade.core.ContainerID("Container" + a, null);
			ContainerID destination = new ContainerID();
			destination.setName("Container" + container);
			System.out.println("ANALYST -> Moving to Container " + container);
			doMove(destination);

			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST_WHENEVER);
			AID reader = new AID("Seller" + container, AID.ISLOCALNAME);
			msg.addReceiver(reader);
			msg.setContent("Pedido de Analyst");
			send(msg);

			System.out.println("ANALYST -> Message sent to Seller" + container);

			container++;

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if (container == 4)
				return true;
			return false;
		}

	}

	protected void takeDown() {
		super.takeDown();
	}

}