import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {

	Runtime rt;
	ContainerController container;

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		this.rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {
		this.rt = Runtime.instance();
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);
	}

	public void startAgentInPlatform(String name, String classpath, Object args[]) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, args);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Main a = new Main();
		a.initMainContainerInPlatform("localhost", "9888", "Main");
		a.startAgentInPlatform("AI", "Agents.AgenteInterface",new Object[0]);
		Object[] aargs3=new Object[3];
		aargs3[0]=(double)0.0;
		aargs3[1]=(double)0.0;
		aargs3[2]=(boolean)true;
		a.startAgentInPlatform("AE1", "Agents.AgenteEstacao",aargs3);
		Object[] aargs4=new Object[3];
		aargs4[0]=(double)0.0;
		aargs4[1]=(double)5000.0;
		aargs4[2]=(boolean)true;
		a.startAgentInPlatform("AE2", "Agents.AgenteEstacao",aargs4);
		Object[] aargs5=new Object[3];
		aargs5[0]=(double)4000.0;
		aargs5[1]=(double)7500.0;
		aargs5[2]=(boolean)true;
		a.startAgentInPlatform("AE3", "Agents.AgenteEstacao",aargs5);
		Object[] aargs6=new Object[3];
		aargs6[0]=(double)7500.0;
		aargs6[1]=(double)2500.0;
		aargs6[2]=(boolean)true;
		a.startAgentInPlatform("AE4", "Agents.AgenteEstacao",aargs6);
		Object[] aargs1=new Object[3];
		aargs1[0]=new String("AE1");
		aargs1[1]=new String("AE2");
		aargs1[2]=new String("AE1");
		a.startAgentInPlatform("AA1", "Agents.AgenteAeronave",aargs1);
		Object[] aargs2=new Object[2];
		aargs2[0]=new String("AE1");
		aargs2[1]=new String("AE4");
		a.startAgentInPlatform("AA2", "Agents.AgenteAeronave",aargs2);
		Object[] aargs7=new Object[2];
		aargs7[0]=new String("AE2");
		aargs7[1]=new String("AE1");
		a.startAgentInPlatform("AA3", "Agents.AgenteAeronave",aargs7);
		Object[] aargs8=new Object[2];
		aargs8[0]=new String("AE2");
		aargs8[1]=new String("AE4");
		a.startAgentInPlatform("AA4", "Agents.AgenteAeronave",aargs8);
		Object[] aargs9=new Object[2];
		aargs9[0]=new String("AE3");
		aargs9[1]=new String("AE1");
		a.startAgentInPlatform("AA5", "Agents.AgenteAeronave",aargs9);
		Object[] aargs10=new Object[2];
		aargs10[0]=new String("AE3");
		aargs10[1]=new String("AE4");
		a.startAgentInPlatform("AA6", "Agents.AgenteAeronave",aargs10);
	}
}