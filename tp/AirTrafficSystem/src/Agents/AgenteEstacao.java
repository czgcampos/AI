package Agents;

import java.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteEstacao extends Agent{
	private double coordX;
	private double coordY;
	private int totalPistas;
	private int nrPistasOcupadas;
	private int totalEstacionamentos;
	private int nrEstOcupados;
	private double zonaProtegida;
	private double zonaAlerta;
	private boolean condMeteo;
	private List<String> listaPrioridade;
	
	protected void setup() {
		super.setup();
		this.listaPrioridade = new ArrayList<>();
		this.zonaAlerta = 1000;
		this.zonaProtegida = 100;
		this.totalPistas = 5;
		this.nrPistasOcupadas = 0;
		this.totalEstacionamentos = 5;
		this.nrEstOcupados = 1;
		Object[] args = getArguments();
		if (args != null && args.length>0) {
			coordX=(double) args[0];
			coordY=(double) args[1];
			condMeteo=(boolean) args[2];
		}
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new receberPedidos());
	}
	
	private class receberPedidos extends CyclicBehaviour {
		public void action() {		
			ACLMessage msg = receive();
			ACLMessage resp=null;
			if(msg != null)
				resp=msg.createReply();
			if (msg != null && msg.getPerformative() == ACLMessage.REQUEST && condMeteo && nrPistasOcupadas < totalPistas) {
				nrPistasOcupadas++;
				nrEstOcupados--;
				resp.setContent(coordX+";"+coordY);
				resp.setPerformative(ACLMessage.CONFIRM);
				send(resp);
			}else if(msg != null && msg.getPerformative() == ACLMessage.REQUEST_WHEN && nrEstOcupados < totalEstacionamentos && condMeteo) {
				nrEstOcupados++;
				String[] coordsAviao = msg.getContent().split(";");
				double aviaoCoordX=Double.parseDouble(coordsAviao[1]);
				double aviaoCoordY=Double.parseDouble(coordsAviao[2]);
				double vel=Double.parseDouble(coordsAviao[3]);
				double distPercorrer=Math.sqrt(((Math.pow((aviaoCoordX - coordX), 2)) + (Math.pow((aviaoCoordY - coordY), 2))));
				adicionaLista(distPercorrer/vel,coordsAviao[0]);
				resp.setContent(coordX+";"+coordY+";"+zonaAlerta+";"+zonaProtegida+";"+distPercorrer);
				resp.setPerformative(ACLMessage.AGREE);
				send(resp);
			}else if(msg != null && msg.getPerformative() == ACLMessage.PROPOSE && nrPistasOcupadas<totalPistas) {
				nrPistasOcupadas++;
				resp.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				double vel=zonaProtegida-10*getVelocidade(msg.getContent());
				resp.setContent(vel+"");
				send(resp);
			}else if(msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				String n=msg.getContent();
				removeLista(n);
				nrPistasOcupadas--;
			}else if(msg != null && msg.getPerformative() == ACLMessage.INFORM_IF) {
				String[] coordsAviao = msg.getContent().split(";");
				double temp1=Double.parseDouble(coordsAviao[1]);
				double temp2=Double.parseDouble(coordsAviao[2]);
				removeLista(coordsAviao[0]);
				adicionaLista(temp1+temp2,coordsAviao[0]);
			}else
				block();
		}
	}
	
	private void adicionaLista(double tempo, String name) {
		double tempoAux=0.0;
		if(listaPrioridade.size()==0)
			listaPrioridade.add(0, name+";"+tempo);
		else {
			for(int i=0;i<listaPrioridade.size();i++) {
				String[] infoAux = listaPrioridade.get(i).split(";");
				tempoAux=Double.parseDouble(infoAux[1]);
				if(tempo<tempoAux) {
					listaPrioridade.add(i, name+";"+tempo);
					break;
				}
			}
		}
	}
	
	private double getVelocidade(String name) {
		double res=1.0;
		for(int i=0;i<listaPrioridade.size();i++) {
			String[] infoAux = listaPrioridade.get(i).split(";");
			if(infoAux[0].equals(name)) {
				res=i;
				break;
			}
		}
		return res;
	}
	
	private void removeLista(String name) {
		for(int i=0;i<listaPrioridade.size();i++) {
			String[] infoAux = listaPrioridade.get(i).split(";");
			if(infoAux[0].equals(name)) {
				listaPrioridade.remove(i);
				break;
			}
		}
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