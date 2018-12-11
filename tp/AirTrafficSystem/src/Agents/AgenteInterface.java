package Agents;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler.ChartTheme;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteInterface extends Agent implements ExampleChart<BubbleChart> {

    private BubbleChart bubbleChart;
    private double[] xData_AA=new double[] {0.0,0.0,0.0,4000.0,4000.0,0.0};
    private double[] yData_AA=new double[] {0.0,0.0,5000.0,7500.0,7500.0,5000.0};
    private double[] bubbleData_AA = new double []{3,3,3,3,3,3};
    private double[] bubbleData_AA_AP = new double []{120,120,120,120,120,120};
    private int iAA=0;
    private double[] xData_AE=new double[] {0.0,0.0,4000.0,7500.0};
    private double[] yData_AE=new double[] {0.0,5000.0,7500.0,2500.0};
    private double[] bubbleData_AE = new double []{3,3,3,3};
    private double[] bubbleData_AE_AP = new double []{120,120,120,120};
    private int iAE=0;
    private static final String Agente_Estacao = "AE";
    private static final String Agente_Aeronave = "AA";
    private static final String Agente_Estacao_AP = "AE_AP";
    private static final String Agente_Aeronave_AP = "AA_AP";
    
	protected void setup() {
		super.setup();
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("Interface");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new ReceberCoords());
		final XChartPanel<BubbleChart> chartPanel = this.buildPanel();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("XChart");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.add(chartPanel);
				frame.pack();
				frame.setVisible(true);
			}
		});
		TimerTask chartUpdaterTask = new TimerTask() {
			public void run() {
				updateData();
				chartPanel.revalidate();
				chartPanel.repaint();
			}
		};
		java.util.Timer timer = new Timer();
		timer.scheduleAtFixedRate(chartUpdaterTask, 0, 1000);
	}
	
	private class ReceberCoords extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if(msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				String[] coordsAviao = msg.getContent().split(";");
				int pos=Integer.parseInt(coordsAviao[0].replace("A", ""));
				double aviaoCoordX=Double.parseDouble(coordsAviao[1]);
				double aviaoCoordY=Double.parseDouble(coordsAviao[2]);
				xData_AA[pos-1]=aviaoCoordX;
				yData_AA[pos-1]=aviaoCoordY;
			}else
				block();
		}
	}
	
    public  XChartPanel<BubbleChart> buildPanel() {
        return new XChartPanel<BubbleChart>(getChart());
    }

    public  BubbleChart getChart() {
        bubbleChart = new BubbleChartBuilder().width(1366).height(500).theme(ChartTheme.GGPlot2).xAxisTitle("X").yAxisTitle("Y").title("Real-time Bubble Chart").build();
        bubbleChart.addSeries(Agente_Estacao_AP, xData_AE, yData_AE, bubbleData_AE_AP);
        bubbleChart.addSeries(Agente_Aeronave_AP, xData_AA, yData_AA, bubbleData_AA_AP);
        bubbleChart.addSeries(Agente_Estacao, xData_AE, yData_AE, bubbleData_AE);
        bubbleChart.addSeries(Agente_Aeronave, xData_AA, yData_AA, bubbleData_AA);
        return bubbleChart;
    }

    public  void updateData() {
    	bubbleChart.updateBubbleSeries(Agente_Estacao_AP,xData_AE,yData_AE,bubbleData_AE_AP);
        bubbleChart.updateBubbleSeries(Agente_Aeronave_AP,xData_AA,yData_AA,bubbleData_AA_AP);
        bubbleChart.updateBubbleSeries(Agente_Estacao,xData_AE,yData_AE,bubbleData_AE);
        bubbleChart.updateBubbleSeries(Agente_Aeronave,xData_AA,yData_AA,bubbleData_AA);
    }
    
    protected void takeDown() {
		super.takeDown();	
		try {
			DFService.deregister(this);
		}catch(FIPAException e) {
			e.printStackTrace();
		}
	}
}