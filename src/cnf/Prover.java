package cnf;

import java.util.ArrayList;

public class Prover {
	
	private ArrayList<ArrayList<String>> clauses = new ArrayList<ArrayList<String>>();
	private ArrayList<String> clause1 = new ArrayList<String>();
	private ArrayList<String> clause2 = new ArrayList<String>();
	private String proof = "";
	
	
	public Prover()
	{
		
	}
	
	public void run(ArrayList<ArrayList<String>> cnfclauses)
	{
		String literal = null;
		this.clauses.addAll(cnfclauses);
		while(true)
		{
			literal = null;
			for(int i = 0 ; i < clauses.size()-2; i++)
			{
				literal = choosetwo(i);
				if(literal != null)
				{
					break;
				}
			}
			if(literal == null)
			{
				System.out.println("Satisfiable but no proof");
				System.exit(1);
			}
			resolve(literal);
			clause1.clear();
			clause2.clear();
			
		}
	}
	
	private String choosetwo(int pos)
	{
		ArrayList<String> current = new ArrayList<String>();
		current = clauses.get(pos);
		for(int i = pos+1; i < clauses.size(); i++)
		{
			String literal = found(current,clauses.get(i));
			if(literal != null)
			{
				return literal;
			}
		}
		
		return null;
	}
	
	private String found(ArrayList<String> one, ArrayList<String> two)
	{
		String temp = "";
		boolean literal = false;
		for(int i = 0; i < one.size(); i++)
		{
			literal = false;
			if(one.get(i).contains("neg"))
			{
				temp = one.get(i);
				temp = temp.replace("neg", "");
				literal = true;
			}
			if(one.get(i).matches("[A-Z]+[0-9]*"))
			{
				temp = one.get(i);
				temp = "neg" + temp;
				literal = true;
			}
			
			if(literal && two.contains(temp))
			{
				clause1.addAll(one);
				clause2.addAll(two);
				return one.get(i);
			}
		}
		
		return null;
	}
	
	private void resolve(String literal)
	{
		if(clause1.size() == 1 && clause2.size() == 1)
		{
			System.out.println("Falsehood - unsatisfiable");
			System.exit(1);
		}
		
		String temp = literal;
		temp = "neg" + literal;
		
		ArrayList<String> newclause = new ArrayList<String>();
		newclause.addAll(clause1);
		newclause.addAll(clause2);
		newclause.remove(literal);
		newclause.remove(temp);
		int i = 0;
		if(newclause.get(0).equals("ore"))
		{
			newclause.remove(0);
		}
		
		while(true)
		{
			if(i+1 >= newclause.size())
			{
				if(newclause.get(i).equals("ore"))
				{
					newclause.remove(i);
				}
				break;
			}
			if((isop(newclause.get(i)) && isop(newclause.get(i+1))))
			{
				if(newclause.get(i).equals("ore"))
				{
					newclause.remove(i);
				}
			}
			if(!isop(newclause.get(i)) && !isop(newclause.get(i+1)))
			{
				newclause.add(i+1,"ore");
			}
			i++;
			
		}
	proof += "[CLAUSES]   {" + Main2.toletters(clause1) + "}     {" +  Main2.toletters(clause2) + "}\n";
	proof += "[RESOLVANT] {" + Main2.toletters(newclause) + "}\n";
		clauses.remove(clause1);
		clauses.remove(clause2);
		for(int j = 0; j < clauses.size(); j++)
		{
			if(clauses.get(j).equals(newclause))
			{
				System.out.println("Proof:");
				System.out.println(proof);
				System.out.println("Old clause set contains same clauses a new clause set, therefore SAT");
				System.exit(1);
			}
		}
		clauses.add(newclause);
		
	
		
	}
	
	private boolean isop(String word)
	{
		if(word.equals("ore"))
		{
			return true;
		}
		if(word.equals("obr"))
		{
			return true;
		}
		if(word.equals("cbr"))
		{
			return true;
		}
		return false;
	}

}
