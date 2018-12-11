package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.*;

public class AgenteAeronave extends Agent{
	private double coordX;
	private double coordY;
	private double origemCoordX;
	private double origemCoordY;
	private double destCoordX;
	private double destCoordY;
	private double zonaProtegida;
	private double zonaAlerta;
	private double zonaProtegidaEstacao;
	private double zonaAlertaEstacao;
	private int nrPassageiros;
	private double direcaoX;
	private double direcaoY;
	private boolean alterouDir;
	private boolean alterouVel;
	private boolean autorizacaoPartida;
	private boolean autorizacaoChegada;
	private boolean dentroAP;
	private boolean fimViagem;
	private double velocidade;
	private double velocidadeMax;
	private double distPercorrer;
	private double distPercorrida;
	private double tempoDecorrido;
	private double tempoFalta;
	private List<String> aes;
	private int conta;
	private String name="";
	
	protected void setup() {
		super.setup();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("Plane");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		this.aes=new ArrayList<>();
		Object[] args = getArguments();
		if (args != null) {
			for(int i=0;i<args.length;i++)
				aes.add((String) args[i]);
		}
		conta=0;
		alterouDir=false;
		alterouVel=false;
		autorizacaoPartida=false;
		autorizacaoChegada=false;
		dentroAP=false;
		fimViagem=false;
		velocidade=0;
		velocidadeMax=100;
		zonaProtegida=100;
		zonaAlerta=1000;
		zonaProtegidaEstacao=0;
		zonaAlertaEstacao=0;
		name=this.getLocalName();
		coordX=0;
		coordY=0;
		direcaoX=0;
		direcaoY=0;
		distPercorrer=1;
		distPercorrida=0;
		tempoDecorrido=1;
		tempoFalta=0;
		destCoordX=0;
		destCoordY=0;
		origemCoordY=0;
		origemCoordY=0;
		addBehaviour(new Descolagem(this, 1000));
		addBehaviour(new ReceberMsg());
		addBehaviour(new Movimento(this, 1000));
		addBehaviour(new EnviarCoords(this, 1000));
		addBehaviour(new Aterragem());
	}
	
	private class Movimento extends TickerBehaviour {
		public Movimento(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			if(velocidade!=0) {
				coordX=coordX+velocidade*direcaoX;
				coordY=coordY+velocidade*direcaoY;
				try {
					DFAgentDescription d = new DFAgentDescription();
					ServiceDescription s = new ServiceDescription();
					s.setType("Interface");
					d.addServices(s);
					DFAgentDescription[] r = DFService.search(this.myAgent, d);
					if (r.length > 0) {
						for (int i = 0; i < r.length; ++i) {
							DFAgentDescription d2 = r[i];
							AID p = d2.getName();
							ACLMessage m = new ACLMessage(ACLMessage.INFORM);
							m.addReceiver(p);
							m.setContent(name+";"+coordX+";"+coordY);
							send(m);
						}
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				distPercorrida+=velocidade;
				distPercorrer-=velocidade;
				tempoDecorrido++;
				tempoFalta=distPercorrer/velocidade;
				if(alterouDir || alterouVel) {
					try {
						DFAgentDescription dfd = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType(aes.get(conta+1));
						dfd.addServices(sd);
						DFAgentDescription[] results = DFService.search(this.myAgent, dfd);
						if (results.length > 0) {
							for (int i = 0; i < results.length; ++i) {
								DFAgentDescription dfd2 = results[i];
								AID provider = dfd2.getName();
								ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM_IF);
								msg2.addReceiver(provider);
								if(alterouDir) {
									distPercorrer=Math.sqrt(((Math.pow((destCoordX - coordX), 2)) + (Math.pow((destCoordY - coordY), 2))));
									direcaoX=(destCoordX-coordX)/distPercorrer;
									direcaoY=(destCoordY-coordY)/distPercorrer;
									tempoFalta=distPercorrer/velocidade;
								}
								msg2.setContent(name+";"+tempoDecorrido+";"+tempoFalta);
								send(msg2);
							}
						}
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}
					alterouDir=false;
					alterouVel=false;
				}
			}
		}
	}
	
	private class Descolagem extends TickerBehaviour {
		public Descolagem(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			if(conta<aes.size()-1 && velocidade==0 && !autorizacaoPartida){
				try {
					DFAgentDescription dfd1 = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();
					sd1.setType(aes.get(conta));
					dfd1.addServices(sd1);
					DFAgentDescription[] results1 = DFService.search(this.myAgent, dfd1);
					if (results1.length > 0) {
						for (int i = 0; i < results1.length; ++i) {
							DFAgentDescription dfd2 = results1[i];
							AID provider1 = dfd2.getName();
							ACLMessage msgLocal1 = new ACLMessage(ACLMessage.REQUEST);
							msgLocal1.addReceiver(provider1);	
							System.out.println(name+" pediu para usar pista a "+aes.get(conta));
							send(msgLocal1);
						}
					}
				}catch (FIPAException e) {
					e.printStackTrace();
				}
			}else if(conta<aes.size()-1 && velocidade==0 && !autorizacaoChegada && autorizacaoPartida) {
				try {
					DFAgentDescription dfd3 = new DFAgentDescription();
					ServiceDescription sd3 = new ServiceDescription();
					sd3.setType(aes.get(conta+1));
					dfd3.addServices(sd3);
					DFAgentDescription[] results2 = DFService.search(this.myAgent, dfd3);
					if (results2.length > 0) {
						for (int i = 0; i < results2.length; ++i) {
							DFAgentDescription dfd4 = results2[i];
							AID provider2 = dfd4.getName();
							ACLMessage msgLocal2 = new ACLMessage(ACLMessage.REQUEST_WHEN);
							msgLocal2.addReceiver(provider2);
							msgLocal2.setContent(name+";"+coordX+";"+coordY+";"+velocidadeMax/2);
							System.out.println(name+" pediu para iniciar viagem a "+aes.get(conta+1));
							send(msgLocal2);
						}
					}
				}catch (FIPAException e) {
					e.printStackTrace();
				}
			}else if(conta<aes.size()-1 && velocidade==0 && autorizacaoChegada && autorizacaoPartida) {
				try {
					DFAgentDescription dfd5 = new DFAgentDescription();
					ServiceDescription sd5 = new ServiceDescription();
					sd5.setType(aes.get(conta));
					dfd5.addServices(sd5);
					DFAgentDescription[] results3 = DFService.search(this.myAgent, dfd5);
					if (results3.length > 0) {
						for (int i = 0; i < results3.length; ++i) {
							DFAgentDescription dfd6 = results3[i];
							AID provider3 = dfd6.getName();
							ACLMessage msgLocal3 = new ACLMessage(ACLMessage.INFORM);
							msgLocal3.addReceiver(provider3);
							msgLocal3.setContent(name);
							send(msgLocal3);
						}
					}
				}catch (FIPAException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class EnviarCoords extends TickerBehaviour {
		public EnviarCoords(Agent a, long period) {
			super(a, period);
		}
		protected void onTick() {
			if(velocidade!=0){
				try {
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Plane");
					dfd.addServices(sd);
					DFAgentDescription[] results = DFService.search(this.myAgent, dfd);
					if (results.length > 0) {
						for (int i = 0; i < results.length; ++i) {
							DFAgentDescription dfd2 = results[i];
							AID provider = dfd2.getName();
							if(!provider.equals(this.myAgent.getAID())){
								ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
								msg.addReceiver(provider);
								msg.setContent(coordX+";"+coordY+";"+direcaoX+";"+direcaoY+";"+distPercorrer+";"+destCoordX+";"+destCoordY+";"+origemCoordX+";"+origemCoordY+";"+nrPassageiros);
								send(msg);
							}
						}
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		}
	}
	
	private class ReceberMsg extends CyclicBehaviour {
		public void action() {
			if(conta<aes.size()-1) {
				ACLMessage msg = receive();
				if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
					String[] coordsOutro = msg.getContent().split(";");
					double coordXOutro=Double.parseDouble(coordsOutro[0]);
					double coordYOutro=Double.parseDouble(coordsOutro[1]);
					double dist = Math.sqrt(((Math.pow((coordXOutro - coordX), 2)) + (Math.pow((coordYOutro - coordY), 2))));
					double destXOutro=Double.parseDouble(coordsOutro[5]);
					double destYOutro=Double.parseDouble(coordsOutro[6]);
					double origemXOutro=Double.parseDouble(coordsOutro[7]);
					double origemYOutro=Double.parseDouble(coordsOutro[8]);
					if(dist>zonaProtegida && dist<=zonaAlerta) {
						double dirXOutro=Double.parseDouble(coordsOutro[2]);
						double dirYOutro=Double.parseDouble(coordsOutro[3]);
						double ang = Math.acos((direcaoX*dirXOutro+direcaoY*dirYOutro)/((Math.sqrt(dirXOutro*dirXOutro+dirYOutro*dirYOutro))*(Math.sqrt(direcaoX*direcaoX+direcaoY*direcaoY))));
						if(origemCoordX==destXOutro && origemCoordY==destYOutro && origemXOutro==destCoordX && origemYOutro==destCoordY) {
						    double rx = (direcaoX * Math.cos(Math.toRadians(-45))) - (direcaoY * Math.sin(Math.toRadians(-45)));
						    double ry = (direcaoX * Math.sin(Math.toRadians(-45))) + (direcaoY * Math.cos(Math.toRadians(-45)));
						    direcaoX = rx;
						    direcaoY = ry;
						    alterouDir=true;
						}else if((ang!=0 && ang!=Math.PI) && (destCoordX!=destXOutro || destCoordY!=destYOutro) && (origemCoordX!=origemXOutro || origemCoordY!=origemYOutro)) {
							double distOutro=Double.parseDouble(coordsOutro[4]);
							double passOutro=Double.parseDouble(coordsOutro[9]);
							if(distOutro>distPercorrer && velocidade<zonaProtegidaEstacao+zonaProtegidaEstacao/4) {
								velocidade+=zonaProtegidaEstacao/4;
								alterouVel=true;
							} else if(distOutro<distPercorrer && velocidade>zonaProtegidaEstacao-zonaProtegidaEstacao/4){
								velocidade-=zonaProtegidaEstacao/4;
								alterouVel=true;
							} else if(passOutro<nrPassageiros && velocidade<zonaProtegidaEstacao+zonaProtegidaEstacao/4) {
								velocidade+=zonaProtegidaEstacao/4;
								alterouVel=true;
							} else if(passOutro>nrPassageiros && velocidade>zonaProtegidaEstacao-zonaProtegidaEstacao/4){
								velocidade-=zonaProtegidaEstacao/4;
								alterouVel=true;
							} else if(velocidade<zonaProtegidaEstacao+zonaProtegidaEstacao/4){
								Random r = new Random();
								velocidade+=10 + (20 - 10) * r.nextDouble();
								alterouVel=true;
							}
						}
					}else if(velocidade!=0 && dist<=zonaProtegida && (destCoordX!=destXOutro || destCoordY!=destYOutro) && (origemCoordX!=origemXOutro || origemCoordY!=origemYOutro)) {
						System.out.println("Choque entre "+this.myAgent.getLocalName()+ " e "+msg.getSender().getLocalName());
						doDelete();
					}
				}else if(msg != null && msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					velocidade=Double.parseDouble(msg.getContent());
				}else if (msg != null && msg.getPerformative() == ACLMessage.CONFIRM) {
					autorizacaoPartida=true;
					String[] coordsEstacao1 = msg.getContent().split(";");
					coordX=Double.parseDouble(coordsEstacao1[0]);
					coordY=Double.parseDouble(coordsEstacao1[1]);
					origemCoordX=Double.parseDouble(coordsEstacao1[0]);
					origemCoordY=Double.parseDouble(coordsEstacao1[1]);
					System.out.println(aes.get(conta)+" confirmou pedido de "+name);
				}else if (msg != null && msg.getPerformative() == ACLMessage.AGREE) {
					autorizacaoChegada=true;
					String[] coordsEstacao2 = msg.getContent().split(";");
					destCoordX=Double.parseDouble(coordsEstacao2[0]);
					destCoordY=Double.parseDouble(coordsEstacao2[1]);
					zonaAlertaEstacao=Double.parseDouble(coordsEstacao2[2]);
					zonaProtegidaEstacao=Double.parseDouble(coordsEstacao2[3]);
					distPercorrer=Double.parseDouble(coordsEstacao2[4]);
					direcaoX=(destCoordX-coordX)/distPercorrer;
					direcaoY=(destCoordY-coordY)/distPercorrer;
					velocidade=zonaProtegidaEstacao;
					distPercorrida=0;
					System.out.println(aes.get(conta+1)+" confirmou pedido de "+name);
				}else
					block();
			}
		}
	}
	
private class Aterragem extends CyclicBehaviour{
		public void action() {
			if(conta<aes.size()-1 && !dentroAP  && velocidade!=0) {
				if(Math.sqrt(Math.pow(destCoordX - coordX, 2) + Math.pow(destCoordY - coordY, 2)) <= zonaAlertaEstacao)
					dentroAP=true;
				if(dentroAP){
					try {
						DFAgentDescription dfd = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType(aes.get(conta+1));
						dfd.addServices(sd);
						DFAgentDescription[] results = DFService.search(this.myAgent, dfd);
						if (results.length > 0) {
							for (int i = 0; i < results.length; ++i) {
								DFAgentDescription dfd2 = results[i];
								AID provider = dfd2.getName();
								ACLMessage msg2 = new ACLMessage(ACLMessage.PROPOSE);
								msg2.addReceiver(provider);
								msg2.setContent(name);
								System.out.println(name+" pediu para efetuar aterragem "+aes.get(conta+1));
								send(msg2);
							}
						}
					} catch(FIPAException e) {
						e.printStackTrace();
					}
				}
			}
			if(conta<aes.size()-1 && !fimViagem && velocidade!=0){
				if(Math.sqrt(Math.pow(destCoordX - coordX, 2) + Math.pow(destCoordY - coordY, 2)) <= zonaProtegidaEstacao)
					fimViagem=true;
				if(fimViagem) {
					try {
						DFAgentDescription dfd3 = new DFAgentDescription();
						ServiceDescription sd3 = new ServiceDescription();
						sd3.setType(aes.get(conta+1));
						dfd3.addServices(sd3);
						DFAgentDescription[] results2 = DFService.search(this.myAgent, dfd3);
						if (results2.length > 0) {
							for (int i = 0; i < results2.length; ++i) {
								DFAgentDescription dfd4 = results2[i];
								AID provider2 = dfd4.getName();
								ACLMessage msg4 = new ACLMessage(ACLMessage.INFORM);
								msg4.addReceiver(provider2);
								msg4.setContent(name);
								System.out.println(name+" declarou fim de viagem "+aes.get(conta));
								send(msg4);
							}
						}
					}catch(FIPAException e) {
						 e.printStackTrace();
					}
					velocidade=0;
					conta++;
					autorizacaoPartida=false;
					autorizacaoChegada=false;
					fimViagem=false;
					dentroAP=false;
				}
			}
		}
	}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		super.takeDown();
	}
}