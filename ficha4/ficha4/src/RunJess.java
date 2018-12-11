
import static java.lang.System.in;
import static java.lang.System.out;

import java.util.InputMismatchException;
import java.util.Scanner;

import jess.*;

public class RunJess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			// Run a Jess program...
	        Rete engine = new Rete();
	        engine.batch("Ex1.clp");
	        engine.reset();
	        
	        engine.run();
	        
	        engine.executeCommand("(defrule ex1.1 (person ?n ?a) => (assert (name ?n)) (assert (age ?a)))");
	        engine.run();
	        
	        engine.executeCommand("(defrule ex1.2 (person ?n ?a) => (printout t \"Person \" ?n \" tem \" ?a \" anos.\" crlf))");
	        engine.run();
	        
	        Scanner input = new Scanner(in);
	        boolean ok = false; 
	        String txt = "";
	        while(!ok) {
	            try {
	                txt = input.nextLine();
	                ok = true;
	            }
	            catch(InputMismatchException e){ 
	            	out.println("Texto Invalido"); 
	                out.print("Novo valor: ");
	                input.nextLine(); 
	            }
	        }
	        input.close();
	        
	        engine.executeCommand("(defrule ex1.3 (person ?n ?a) => (assert (nameandsurname ?n " + txt + ")))");
	        engine.run();
	        
	        engine.executeCommand("(defrule ex1.5 (person ?n ?a) (nameandsurname ?f ?l) => (if (= n f) then (printout t \"Primeiro nome \" ?n, Ultimo nome ?l \" tem \" ?a \" anos.\" crlf)))");
	        engine.run();
	        
	        engine.executeCommand("(facts)");
	        engine.run();
	        
			
		} catch (JessException ex) {
			System.err.println(ex);
		}

	}

}
