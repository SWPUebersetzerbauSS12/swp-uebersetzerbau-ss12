import java.util.Vector;

/* 
 * Representation of Nonterminals and there respective Productions
 * as head and rump
 * nonterminals are represented as <N> terminals as T
 * 
 * Author: Patrick Schlott
 * 
 */

public class Productions {

	//The Nonterminal representing 
	private String head;
	//The rump as Productions of Strings
    public Vector < Vector<String> > productions;

    public Productions (String head){
        this.head = head;
        productions = new Vector<Vector<String>>();
    }
    /*
     * Getter for field head
     */
    public String getHead(){
    	return head;
    }
    /*
     * Inserts a new production to the rump
     */
    public void InsertProduction (Vector<String> production) {
        this.productions.add(production);
    }
	
}
