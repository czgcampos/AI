package Agents;
import jade.core.Agent;

  public class HelloAgent extends Agent 
  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() 
      { 
          System.out.println("Hello World. ");
          System.out.println("My name is "+ getLocalName());
          
          doDelete();
      }
	
	protected void takeDown() {
	}
  }