package climatesystem;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jess.*;

public class MonitorAgent extends Agent {
	Rete engine;
	Writer writer;

	@Override
	protected void setup() {
		super.setup();
		
		engine = new Rete();
		try {
			writer = new StringWriter();
			
			engine.batch("climatesystem/JadeAgent.clp");
			engine.eval(getDepartmentFactsString(getArguments()));
			engine.addUserfunction(new ChangeSystems(this));
			engine.reset();
			engine.run();
			//engine.executeCommand("(facts)");
		} catch (JessException e) {
			System.err.println("Error with Jess: "+e);
			System.exit(1);
		}

		addBehaviour(new ReceiveTemperaturesFromAgents());
		addBehaviour(new PrintTop3Departments(this, 10000));
	}

	private String getDepartmentFactsString(Object[] arguments) {
		StringBuilder facts = new StringBuilder();
		if(arguments.length==1 && arguments[0] instanceof java.util.ArrayList) {
			facts.append("(deffacts departments");
			for(String dep : (ArrayList<String>) arguments[0]) {
				facts.append(" (Department (name \"");
				facts.append(dep);
				facts.append("\"))");
			}
			facts.append(") (defglobal ?*avg* = 25) (defglobal ?*stddev* = 0)" +
						" (defglobal ?*heat1* = \"ja\") (defglobal ?*heat2* = \"undefined\") (defglobal ?*heat3* = \"undefined\")" +
						" (defglobal ?*cool1* = \"undefined\") (defglobal ?*cool2* = \"undefined\") (defglobal ?*cool3* = \"undefined\")");
		}
		else {
			System.err.println("Error with initializeDepartmentFacts");
		}
		return facts.toString();
	}
	
	
	public class ChangeSystems implements jess.Userfunction {
		private Agent agent;
		
		public ChangeSystems(Agent a) {
			agent = a;
	 	}

		public String getName() {
			return ("changesystem");
		}

		// Called when (send ...) is executed at Jess
		public Value call(ValueVector vv, Context context) throws JessException {

	         // get function arguments
	        String cmd = vv.get(1).stringValue(context);
	        String department = vv.get(2).resolveValue(context).stringValue(context);

	        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent(cmd);
			msg.setConversationId(""+System.currentTimeMillis());
			msg.addReceiver(agent.getAID(department));
			//System.out.println(agent.getLocalName() + ": sending command '" + cmd + "' to "+department);
			agent.send(msg);
			
	        	return Funcall.TRUE;
	     }
	 }

	private class ReceiveTemperaturesFromAgents extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg!=null && msg.getPerformative()==ACLMessage.INFORM) {
			
			    	try {
			    		Fact fact = new Fact("ACLMessage", engine);
				    	fact.setSlotValue("sender", new Value(msg.getSender().getLocalName(), RU.STRING));
				    	fact.setSlotValue("content", new Value(Integer.parseInt(msg.getContent()), RU.INTEGER));
				    	engine.assertFact(fact);
					engine.run();
				} catch (JessException e) {
					System.err.println("ReceiveTemperaturesFromAgents - Jess assert error: "+e);
					System.exit(1);
				}
    			}
		}
	}
	
	private class PrintTop3Departments extends TickerBehaviour {
		
		public PrintTop3Departments(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			
			System.out.println("--------------------------------------------");
			try {
				System.out.println("Top 3 HEATING activated: \n\t1) "+engine.eval("?*heat1*").stringValue(engine.getGlobalContext())+
						"\n\t2) "+engine.eval("?*heat2*").stringValue(engine.getGlobalContext())+
						"\n\t3) "+engine.eval("?*heat3*").stringValue(engine.getGlobalContext()));
				System.out.println("Top 3 COOLING activated: \n\t1) "+engine.eval("?*cool1*").stringValue(engine.getGlobalContext())+
						"\n\t2) "+engine.eval("?*cool2*").stringValue(engine.getGlobalContext())+
						"\n\t3) "+engine.eval("?*cool3*").stringValue(engine.getGlobalContext()));
				System.out.println("Average temperature: "+engine.eval("?*avg*").toString());
				System.out.println("Standard deviation: "+engine.eval("?*stddev*").toString());
			} catch (JessException e) {
				System.err.println("Error with PrintTop3Departments: "+e.toString());
			}
		}
	}
}
