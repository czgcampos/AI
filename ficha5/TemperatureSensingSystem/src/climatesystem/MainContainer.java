package climatesystem;

import jade.core.Runtime;

import java.util.ArrayList;
import java.util.List;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {
	static final int DEPARTMENTS = 10;

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

	public void startAgentInPlatform(String name, String classpath, Object[] args) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, args);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startAgentInPlatform(String name, String classpath) {
		startAgentInPlatform(name,classpath, new Object[0] );
	}

	public static void main(String[] args) {
		MainContainer a = new MainContainer();
		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");		

		List<String> departments = new ArrayList<>();
		for(int i=1;i<=DEPARTMENTS;i++) {
			String departmentName = "Department"+String.format("%03d", i);
			departments.add(departmentName);
			a.startAgentInPlatform(departmentName, "climatesystem.TemperatureSensorAgent");
		}

		a.startAgentInPlatform("Monitor", "climatesystem.MonitorAgent",new Object[] {departments});
	}

}
