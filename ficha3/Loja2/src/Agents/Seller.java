package Agents;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Seller extends Agent {

	private ArrayList<String> products;
	private int aVendido; // n�mero de produtos A vendidos
	private int bVendido; // n�mero de produtos B vendidos
	private int cVendido; // n�mero de produtos C vendidos
	private int dVendido; // n�mero de produtos D vendidos
	private static int aValor = 5; // valor de venda do produto A
	private static int bValor = 2; // valor de venda do produto B
	private static int cValor = 7; // valor de venda do produto C
	private static int dValor = 3; // valor de venda do produto D

	private int total_customers;

	protected void setup() {
		super.setup();

		// Register Agent
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		// Register in DF with Agent Name: "Seller1", "Seller2" or "Seller3" depending
		// on which container is located
		sd.setType(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		products = new ArrayList<String>();
		products.add("A");
		products.add("B");
		products.add("C");
		products.add("D");
		aVendido = 0;
		bVendido = 0;
		cVendido = 0;
		dVendido = 0;

		total_customers = 0;

		addBehaviour(new ReceberPedidosEProdutos());

	}

	private class ReceberPedidosEProdutos extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) { 
				// receber pedidos de requisi��o
				String clienteP = msg.getSender().getLocalName();
				ACLMessage resp = msg.createReply();
				String produtoPedido = msg.getContent();
				if (products.contains(produtoPedido)) {
					if (produtoPedido.equals("A")) {
						aVendido++;
						total_customers++;
						System.out.println(myAgent.getLocalName() + " -> produto A requisitado por " + clienteP);
						resp.setContent("A");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("B")) {
						bVendido++;
						total_customers++;
						System.out.println(myAgent.getLocalName() + " -> produto B requisitado por " + clienteP);
						resp.setContent("B");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("C")) {
						cVendido++;
						total_customers++;
						System.out.println(myAgent.getLocalName() + " -> produto C requisitado por " + clienteP);
						resp.setContent("C");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("D")) {
						dVendido++;
						total_customers++;
						System.out.println(myAgent.getLocalName() + " -> produto D requisitado por " + clienteP);
						resp.setContent("D");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else {
						System.out.println(myAgent.getLocalName() + " -> produto " + produtoPedido + " pedido por " + clienteP + " nao disponivel");
						resp.setContent(produtoPedido);
						resp.setPerformative(ACLMessage.REFUSE);
					}
				} else {
					System.out.println(myAgent.getLocalName() + " -> produto " + produtoPedido + " pedido por " + clienteP + " nao existe");
					resp.setContent(produtoPedido);
					resp.setPerformative(ACLMessage.REFUSE);
				}
				send(resp);
			}
			else if (msg != null && msg.getPerformative() == ACLMessage.REQUEST_WHENEVER) { 
				// receber pedidos de queries
				int total = aValor * aVendido + bValor * bVendido + cValor * cVendido + dValor * dVendido;
				int total_avg = 0;
				if (total > 0 || total_customers > 0)
					total_avg = total/total_customers;
				//System.out.println(myAgent.getLocalName() + " -> Valor Angariado: " + total);
				
				System.out.println(myAgent.getLocalName() + " -> Message Received from Analyst");
				
				// Find product most selled
				String product_most_selled = findMax(aVendido, bVendido, cVendido, dVendido);
				
				//String clienteP = msg.getSender().getLocalName();
				ACLMessage resp = msg.createReply();
				resp.setContent(total + ";" + total_avg + ";" + product_most_selled);
				resp.setPerformative(ACLMessage.INFORM);
				send(resp);
				
				System.out.println(myAgent.getLocalName() + " -> Message sent to Analyst");
				
			}

			block();
		}
	}

	protected void takeDown() {
		super.takeDown();
		// De-register Agent from DF before killing it
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
	}

	// Find the product most selled
	private String findMax(int... vals) {
		int max = Integer.MIN_VALUE;
		String res = new String();
		int pos = 1;

		for (int d : vals) {
			if (d > max) {
				max = d;
				// Switch position by Product String
				switch (pos) {
					case 1: res = "A:" + Integer.toString(max);
							break;
					case 2: res = "B:" + Integer.toString(max);
							break;
					case 3: res = "C:" + Integer.toString(max);
							break;
					case 4: res = "D:" + Integer.toString(max);
							break;
				}
			}
			pos++;
		}
		return res;
	}
}
