import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MainContainer {

	Runtime rt;
	ContainerController container;

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

	public static void main(String[] args) {
		MainContainer a = new MainContainer();

		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");
		
		/*
		a.startAgentInPlatform("Seller", "Agents.Seller");
		
		int n = 0;
		int limit = 100; // Limit number of Customers
		try {
			while (n<limit) {
				a.startAgentInPlatform("Customer " + n, "Agents.Customer");
				n++;
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		
		
		// Example of Container Creation (not the main container)
		ContainerController newcontainer1 = a.initContainerInPlatform("localhost", "9888", "Container1");
		ContainerController newcontainer2 = a.initContainerInPlatform("localhost", "9888", "Container2");
		ContainerController newcontainer3 = a.initContainerInPlatform("localhost", "9888", "Container3");
		
		// Example of Agent Creation in new container
		try {
			// Start Seller1, Seller2 and Seller3 Agents respectively in Container1, Container2 and Container3 (1 Seller per Container)
			AgentController seller1 = newcontainer1.createNewAgent("Seller1", "Agents.Seller", new Object[] {});// arguments
			AgentController seller2 = newcontainer2.createNewAgent("Seller2", "Agents.Seller", new Object[] {});// arguments
			AgentController seller3 = newcontainer3.createNewAgent("Seller3", "Agents.Seller", new Object[] {});// arguments
			
			seller1.start();
			seller2.start();
			seller3.start();
			
			int n = 0;
			int limit = 100; // Limit number of Customers
			
			while (n<limit) {
				AgentController customer1 = newcontainer1.createNewAgent("Customer1", "Agents.Customer", new Object[] {});// arguments
				AgentController customer2 = newcontainer2.createNewAgent("Customer2", "Agents.Customer", new Object[] {});// arguments
				AgentController customer3 = newcontainer3.createNewAgent("Customer3", "Agents.Customer", new Object[] {});// arguments
				
				customer1.start();
				customer2.start();
				customer3.start();
				
				if (n%10 == 0 && n > 0)
				{
					// Start new Analyst Agent in Container1 every 10 seconds
					a.startAgentInPlatform("Analyst", "Agents.Analyst");
					//AgentController analyst = newcontainer1.createNewAgent("Analyst", "Agents.Analyst", new Object[] {});// arguments
					//analyst.start();
				}
				
				n++;
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}
}