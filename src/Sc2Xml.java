import java.io.*;
import sc.analysis.*;
import sc.node.*;

class Sc2Xml extends DepthFirstAdapter
{
    private int indentation;
	private PrintStream out;
    
    public Sc2Xml(String baseFileName)
    {

	if (baseFileName == null){
	    this.out = System.out;
	    
	}
	else{
	    try {
			String fileName = baseFileName + ".sc";
		this.out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
    }
    
    
    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
	for(int i = 0; i < this.indentation; i++){this.out.print(" ");}
	this.indentation++;
	this.out.println("<" + node.getClass().getSimpleName() + ">");
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
	this.indentation--;
	for(int i = 0; i < this.indentation; i++){this.out.print(" ");}
	this.out.println("</" + node.getClass().getSimpleName() + ">");
    }

}
