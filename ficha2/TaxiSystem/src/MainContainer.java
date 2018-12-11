import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 *
 */

/**
 * @author Filipe Gonçalves
 */
public class MainContainer {

	Runtime rt;
	ContainerController container;

	public static void main(String[] args) {
		MainContainer a = new MainContainer();

		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");

		a.startAgentInPlatform("Manager", "Agents.Manager");

		int n;
		int limit_taxis = 5; // Limit number of Taxis

		// Start Agents Taxis!
		for (n = 0; n < limit_taxis; n++) {
			a.startAgentInPlatform("Taxi" + n, "Agents.Taxi");
			n++;
		}

		// Let all Taxis be ready
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Start first 10 Agents Customers!
		for (n = 0; n < 10; n++) {
			a.startAgentInPlatform("Customer" + n, "Agents.Customer");
			n++;
		}

		int limit_customers = 100; // Limit number of Customers
		// Start Agents Customers!
		for (n = 10; n < limit_customers; n++) {
			try {
				a.startAgentInPlatform("Customer" + n, "Agents.Customer");
				Thread.sleep(1000);
				n++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * // Example of Container Creation (not the main container) ContainerController
		 * newcontainer = a.initContainerInPlatform("localhost", "9888",
		 * "OtherContainer");
		 * 
		 * // Example of Agent Creation in new container try { AgentController ag =
		 * newcontainer.createNewAgent("agentnick", "ReceiverAgent", new Object[] {});//
		 * arguments ag.start(); } catch (StaleProxyException e) { e.printStackTrace();
		 * }
		 */
	}

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}