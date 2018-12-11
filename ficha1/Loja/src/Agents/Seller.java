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
		addBehaviour(new VerLucro(this, 10000));

	}

	private class ReceberPedidosEProdutos extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) { // receber pedidos de requisição
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
						System.out.println("produto " + produtoPedido + " pedido por " + clienteP + " n�o dispon�vel");
						resp.setContent(produtoPedido);
						resp.setPerformative(ACLMessage.REFUSE);
					}
				} else {
					System.out.println("produto " + produtoPedido + " pedido por " + clienteP + " n�o existe");
					resp.setContent(produtoPedido);
					resp.setPerformative(ACLMessage.REFUSE);
				}
				send(resp);
			}

			block();
		}
	}

	private class VerLucro extends TickerBehaviour {
		public VerLucro(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		protected void onTick() {
			int total = aValor * aVendido + bValor * bVendido + cValor * cVendido + dValor * dVendido;
			System.out.println("Valor Angariado: " + total);
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
