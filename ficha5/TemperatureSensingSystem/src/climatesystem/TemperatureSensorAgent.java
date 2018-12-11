package climatesystem;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

enum MACHINESTATE { OFF, COOLING, HEATING }

public class TemperatureSensorAgent extends Agent {
	int temperature;
	Random rand;
	MACHINESTATE currentState;
	
	@Override
	protected void setup() {
		super.setup();
		temperature = 25;
		rand = new Random();
		currentState = MACHINESTATE.OFF;
		
		this.addBehaviour(new UpdateTemperature(this, 500));		
		this.addBehaviour(new InformMonitorAgent(this, 2000));		
		this.addBehaviour(new MachineControl(this));
	}
	
	private class UpdateTemperature extends TickerBehaviour {
		
		public UpdateTemperature(Agent a, long period) {
			super(a, period);
		}
		
		@Override
		protected void onTick() {
			switch (currentState) {
				case COOLING:
					temperature += -1;
					break;
				case HEATING:
					temperature += 1;
					break;
				default:
					temperature += rand.nextInt(3)-1;
					break;
			}
		}
	}
	
	private class InformMonitorAgent extends TickerBehaviour {
		
		public InformMonitorAgent(Agent a, long period) {
			super(a, period);
		}
		
		@Override
		protected void onTick() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setContent(Integer.toString(temperature));
			msg.setConversationId(""+System.currentTimeMillis());
			
			AID receiver = myAgent.getAID("Monitor"); 
			msg.addReceiver(receiver);
			myAgent.send(msg);
		}
	}
	
	private class MachineControl extends CyclicBehaviour {
		public MachineControl(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			ACLMessage receivedMsg = myAgent.receive();
			
			if (receivedMsg!=null && receivedMsg.getPerformative()==ACLMessage.REQUEST) {
				switch(receivedMsg.getContent()) {
					case "cool":
						currentState = MACHINESTATE.COOLING;
						break;
					case "heat":
						currentState = MACHINESTATE.HEATING;
						break;
					default:
						currentState = MACHINESTATE.OFF;
						break;
				}
    			}
		}
	}
}
