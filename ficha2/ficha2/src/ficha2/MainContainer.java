package ficha2;

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
			AgentController t1 = newcontainer.createNewAgent("Taxi1", "ficha2.Taxi", new Object[] {});
			AgentController t2 = newcontainer.createNewAgent("Taxi2", "ficha2.Taxi", new Object[] {});
			AgentController t3 = newcontainer.createNewAgent("Taxi3", "ficha2.Taxi", new Object[] {});
			AgentController t4 = newcontainer.createNewAgent("Taxi4", "ficha2.Taxi", new Object[] {});
			AgentController t5 = newcontainer.createNewAgent("Taxi5", "ficha2.Taxi", new Object[] {});
			AgentController m = newcontainer.createNewAgent("Manager", "ficha2.Manager", new Object[] {});
			AgentController c1 = newcontainer.createNewAgent("Client1", "ficha2.Client", new Object[] {});
			AgentController c2 = newcontainer.createNewAgent("Client2", "ficha2.Client", new Object[] {});
			AgentController c3 = newcontainer.createNewAgent("Client3", "ficha2.Client", new Object[] {});
			AgentController c4 = newcontainer.createNewAgent("Client4", "ficha2.Client", new Object[] {});
			AgentController c5 = newcontainer.createNewAgent("Client5", "ficha2.Client", new Object[] {});
			AgentController c6 = newcontainer.createNewAgent("Client6", "ficha2.Client", new Object[] {});
			AgentController c7 = newcontainer.createNewAgent("Client7", "ficha2.Client", new Object[] {});
			AgentController c8 = newcontainer.createNewAgent("Client8", "ficha2.Client", new Object[] {});
			AgentController c9 = newcontainer.createNewAgent("Client9", "ficha2.Client", new Object[] {});
			AgentController c10 = newcontainer.createNewAgent("Client10", "ficha2.Client", new Object[] {});
			m.start();
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			c1.start();
			c2.start();
			c3.start();
			c4.start();
			c5.start();
			c6.start();
			c7.start();
			c8.start();
			c9.start();
			c10.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
	}
}