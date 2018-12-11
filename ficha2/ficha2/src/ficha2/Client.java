package ficha2;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Client extends Agent {
	
	private float x_pos;
	private float y_pos;
	private float x_dest;
	private float y_dest;
	
	protected void setup() {
		super.setup();
		Random r = new Random();
		x_pos = r.nextFloat() * (100);
		y_pos = r.nextFloat() * (100);
		x_dest = r.nextFloat() * (100);
		y_dest = r.nextFloat() * (100);
		CyclicBehaviour comportamento = new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID reader = new AID("Manager", AID.ISLOCALNAME);
				msg.addReceiver(reader);
				msg.setContent(""+x_pos);
				send(msg);
				msg.setContent(""+y_pos);
				send(msg);
				msg = receive();
				if(msg != null)
					System.out.println(msg.getContent());
				x_pos=x_dest;
				y_pos=y_dest;
				Random r2 = new Random();
				x_dest = r2.nextFloat() * (100);
				y_dest = r2.nextFloat() * (100);
			}
		};
		this.addBehaviour(comportamento);
	}
	protected void takeDown() {
		super.takeDown();
	}
}