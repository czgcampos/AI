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
	private int aVendido; // número de produtos A vendidos
	private int bVendido; // número de produtos B vendidos
	private int cVendido; // número de produtos C vendidos
	private int dVendido; // número de produtos D vendidos
	private static int aValor = 5; // valor de venda do produto A
	private static int bValor = 2; // valor de venda do produto B
	private static int cValor = 7; // valor de venda do produto C
	private static int dValor = 3; // valor de venda do produto D

	protected void setup() {
		super.setup();

		// Register Agent
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("seller");
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

		addBehaviour(new ReceberPedidosEProdutos());
		addBehaviour(new ReceberPedidoAnalyst());

	}
	
	private class ReceberPedidoAnalyst extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.PROPOSE) {
				ACLMessage resp = msg.createReply();
				int total = aValor * aVendido + bValor * bVendido + cValor * cVendido + dValor * dVendido;
				int media = total/(aVendido+bVendido+cVendido+dVendido);
				int maior = Math.max(Math.max(Math.max(aVendido,bVendido),cVendido),dVendido);
				resp.setContent(total+","+media+","+maior);
				resp.setPerformative(ACLMessage.CONFIRM);
				send(resp);
			}
		}
	}

	private class ReceberPedidosEProdutos extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) { 
				// receber pedidos de requisição
				String clienteP = msg.getSender().getLocalName();
				ACLMessage resp = msg.createReply();
				String produtoPedido = msg.getContent();
				if (products.contains(produtoPedido)) {
					if (produtoPedido.equals("A")) {
						aVendido++;
						System.out.println("produto A requisitado por " + clienteP);
						resp.setContent("A");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("B")) {
						bVendido++;
						System.out.println("produto B requisitado por " + clienteP);
						resp.setContent("B");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("C")) {
						cVendido++;
						System.out.println("produto C requisitado por " + clienteP);
						resp.setContent("C");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else if (produtoPedido.equals("D")) {
						dVendido++;
						System.out.println("produto D requisitado por " + clienteP);
						resp.setContent("D");
						resp.setPerformative(ACLMessage.CONFIRM);
					} else {
						System.out.println("produto " + produtoPedido + " pedido por " + clienteP + " não disponível");
						resp.setContent(produtoPedido);
						resp.setPerformative(ACLMessage.REFUSE);
					}
				} else {
					System.out.println("produto " + produtoPedido + " pedido por " + clienteP + " não existe");
					resp.setContent(produtoPedido);
					resp.setPerformative(ACLMessage.REFUSE);
				}
				send(resp);
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

}
