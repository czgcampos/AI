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
		
		int n = 0;
		// Limit number of Customers
		
		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");		
				
		ContainerController newcontainer1 = a.initContainerInPlatform("localhost", "9889", "Container1");
		ContainerController newcontainer2 = a.initContainerInPlatform("localhost", "9890", "Container2");
		ContainerController newcontainer3 = a.initContainerInPlatform("localhost", "9891", "Container3");
		
		try {
			AgentController s = newcontainer1.createNewAgent("Seller1", "Agents.Seller", new Object[] {});// arguments
			s.start();
			s = newcontainer2.createNewAgent("Seller2", "Agents.Seller", new Object[] {});// arguments
			s.start();
			s = newcontainer3.createNewAgent("Seller3", "Agents.Seller", new Object[] {});// arguments
			s.start();
			s = newcontainer1.createNewAgent("Analyst", "Agents.Analyst", new Object[] {});
			try {
				AgentController c;
				while (true) {
					c = newcontainer1.createNewAgent("Customer1 " + n, "Agents.Customer", new Object[] {});// arguments
					c.start();
					c = newcontainer2.createNewAgent("Customer2 " + n, "Agents.Customer", new Object[] {});// arguments
					c.start();
					c = newcontainer3.createNewAgent("Customer3 " + n, "Agents.Customer", new Object[] {});// arguments
					c.start();
					n++;
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}