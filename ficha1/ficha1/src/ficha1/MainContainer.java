package ficha1;

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

		a.initMainContainerInPlatform("localhost", "9091", "MainContainer");
		
		// Example of Container Creation (not the main container)
		ContainerController newcontainer = a.initContainerInPlatform("localhost", "9888", "AgentsContainer");
		
		// Example of Agent Start in new container
		
		try {
			// In Agents.HelloAgent, Agents is the Java Package, HelloAgent is the Java Class File of the Agent
			AgentController ag1 = newcontainer.createNewAgent("Seller", "ficha1.Seller", new Object[] {});
			AgentController ag2 = newcontainer.createNewAgent("Buyer", "ficha1.Buyer", new Object[] {});
			ag1.start();
			ag2.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}
}