import jess.JessException;
import jess.Rete;

public class Ex2 {
	public static void main(String[] args)
	{
		try
		{	
			Rete engine= new Rete();
			engine.batch("jessexs/ex2.clp");
			engine.reset();
			//engine.executeCommand("(facts)");

			engine.run();
		}
		catch(JessException ex)
		{
			System.out.println(ex);
		}
	}
}
