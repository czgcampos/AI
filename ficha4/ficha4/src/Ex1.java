import jess.*;

public class Ex1 {

	public static void main(String[] args)
	{
		try
		{	
			Rete engine= new Rete();
			engine.batch("jessexs/ex1.clp");
			engine.reset();
			//engine.executeCommand("(assert (person Joao 40))");
			//engine.executeCommand("(facts)");

			engine.run();
		}
		catch(JessException ex)
		{
			System.out.println(ex);
		}
	}
}
