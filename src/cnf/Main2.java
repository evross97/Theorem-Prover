package cnf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Negation pushed - DE morgan's --
 * Implication removed --
 * Equivalence removed --
 * Distributivity
 * Double negation --
 * 
 */


/*
 * ( = obr
 * ) = cbr
 * - = neg
 * & = and
 * | = ore
 * -> = imp
 * <-> = equ
 * 
 * 
 */
public class Main2 {
	
	private static char[] inputcon;
	private static ArrayList<String> inputs;
	private static ArrayList <String> symbols = new ArrayList<String>();
	private static ArrayList <ArrayList <String>> finishedclauses = new ArrayList<ArrayList<String>>();
	private static boolean debug = false;
	private static Prover prover = new Prover();
	
	
	public static void main(String[] args) {
		inputs = new ArrayList<String>();
		String input;
		try
		{
			FileReader in = new FileReader("src/cnf/in.txt");
			BufferedReader reader = new BufferedReader(in);
			while((input = reader.readLine()) != null)
			{
				inputs.add(input);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Clauses:");
		for(int i = 0; i < inputs.size(); i++)
		{
			try
			{
					String inpt = inputs.get(i);
					symbols.clear();
					inpt = inpt.replaceAll(" ", "");
					symbols = makesymbols(inpt);
					finddoubles();
					parenthesise();
					if(debug)System.out.println("After parenthesise" + symbols);
					convert();
					if(debug)System.out.println("After convert" + symbols);
					makeclauses();
			}
			catch(Exception e)
			{
				System.err.println("ERROR  Could not convert your formulas into CNF");;
				System.exit(1);
			}
			
		}
		
		prover.run(finishedclauses);
	}
	
	private static void finddoubles()
	{
		int i = 0;
		while(i + 2 < symbols.size())
		{
			if((symbols.get(i).equals(symbols.get(i+2))) && (symbols.get(i+1).equals("ore") | symbols.get(i + 1).equals("and")))
			{
				symbols.remove(i+2);
				symbols.remove(i+1);
			}
			i++;
		}
	}

	
	/**
	 * Converts the input into a form which is used by the program
	 */
	private static ArrayList<String> makesymbols(String input)
	{
		input = input.replaceAll("\\)", " cbr ");
		input = input.replaceAll("\\(", "obr ");
		input = input.replaceAll("<->", " equ ");
		input = input.replaceAll("\\->", " imp ");
		input = input.replaceAll("\\-", "neg");
		input = input.replaceAll("\\&", " and ");
		input = input.replaceAll("\\|", " ore ");
		
		ArrayList<String> tempsymbols = new ArrayList<String>();
		tempsymbols.addAll(symbols);
		inputcon = input.toCharArray();
		int i = 0;
		String current = "";
		while(true)
		{
			current = "";
			while(inputcon[i]!= ' ')
			{
				current = current + inputcon[i];
				i++;
				if(i >= inputcon.length)
				{
					break;
				}
			}
			i++;
			if(current.equals("negobr"))
			{
				tempsymbols.add("neg");
				tempsymbols.add("obr");
				current = "";
			}
			if(current.isEmpty() == false)
			{
				tempsymbols.add(current);
			}
			if(i >= inputcon.length)
			{
				break;
			}
		}
		
		return tempsymbols;
		
	}	
	/**
	 * Puts correct brackets in
	 */
	private static void parenthesise()
	{
		findnegation();
		if(debug)System.out.println("After negation" + symbols);
		findconj();
		if(debug)System.out.println("After conjunction" + symbols);
		finddisj();
		if(debug)System.out.println("After disjunction" + symbols);
		findimp();
		if(debug)System.out.println("After implication" + symbols);
		findequiv();
		if(debug)System.out.println("After equivalence" + symbols);
		if(!symbols.contains("obr"))
		{
			symbols.add("cbr");
			symbols.add(0, "obr");
		}
	}
	
	/**
	 * Uses the parts before and after the symbol specified to parenthesise corectly
	 * @param behind
	 * @param infront
	 * @param pos
	 */
	private static void find(ArrayList<String> behind, ArrayList<String> infront, int pos)
	{
		symbols.add(pos+infront.size()+1, "cbr");
		symbols.add(pos-behind.size(), "obr");
	}
	
	/**
	 * Finds the part after the position specified
	 * @param pos
	 * @return
	 */
	private static ArrayList<String> findforward(int pos)
	{
		ArrayList<String> result = new ArrayList<String>();
		int nobrackets = 0;
		ArrayList<String> temp = new ArrayList<String>(symbols.subList(pos+1, symbols.size()));
		for(int j = 0; j < temp.size(); j++)
		{
			if(temp.get(j).equals("obr"))
			{
				nobrackets++;
			}
			if(temp.get(j).equals("cbr"))
			{
				nobrackets--;
			}
			result.add(temp.get(j));
			if(nobrackets == 0)
			{
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Finds the part before the position specified
	 * @param pos
	 * @return
	 */
	private static ArrayList<String> findbackward(int pos)
	{
		ArrayList<String> result = new ArrayList<String>();
		int nobrackets = 0;
		ArrayList<String> temp = new ArrayList<String>(symbols.subList(0, pos));
		Collections.reverse(temp);
		for(int j = 0; j < temp.size(); j++)
		{
			if(temp.get(j).equals("cbr"))
			{
				nobrackets++;
			}
			if(temp.get(j).equals("obr"))
			{
				nobrackets--;
			}
			result.add(0,temp.get(j));
			if(nobrackets == 0)
			{
				return result;
			}
			
		}
		return result;
	}
	
	/**
	 * Returns how many times a word appears in the list
	 * @param word
	 * @return
	 */
	private static int freq(String word)
	{
		int occ = 0;
		for(int i = 0; i< symbols.size(); i++)
		{
			if(symbols.get(i).contains(word))
			{
				occ++;
			}
		}
		return occ;
	}
	
	/**
	 * Finds all the instances of negation and parenthesises
	 */
	private static void findnegation()
	{
		int i = 0;
		int occ = freq("neg");
		while(true)
		{
			if(symbols.get(i).contains("neg"))
			{
				if(symbols.get(i).equals("neg"))
				{
					ArrayList<String> infront = findforward(i);
					symbols.add(i+ infront.size(), "cbr");
				}
				else
				{
					symbols.add(i+1, "cbr");
				}
				symbols.add(i, "obr");
				i++;
				occ--;
			}
			if(occ == 0)
			{
				break;
			}
			i++;
				
		}
	}
	/**
	 * Returns the position of every occurrence of a specified sign
	 * @param sign
	 * @return
	 */
	private static ArrayList<Integer> freq2(String sign)
	{
		ArrayList<Integer> occ = new ArrayList<Integer>();
		for(int i = 0; i< symbols.size(); i++)
		{
			if(symbols.get(i).contains(sign))
			{
				occ.add(i);
			}
		}
		
		return occ;
	}
	
	/**
	 * Finds every disjunction and parenthesises
	 */
	private static void findconj()
	{
		ArrayList<Integer> occ = freq2("and");
		int i = 0;
		int orig = occ.size();
		while(i < orig)
		{
			ArrayList<String> infront = findforward(occ.get(0));
			ArrayList<String> behind = findbackward(occ.get(0));
			find(behind,infront,(occ.get(0)));
			occ = freq2("and");
			for(int j = 0; j <= i; j ++)
			{
				occ.remove(0);
			}
			i++;			
		}
	}
	
	/**
	 * Finds every disjunction and parenthesises
	 */
	private static void finddisj()
	{
		ArrayList<Integer> occ = freq2("ore");
		int i = 0;
		int orig = occ.size();
		while(i < orig)
		{
			ArrayList<String> infront = findforward(occ.get(0));
			ArrayList<String> behind = findbackward(occ.get(0));
			find(behind,infront,(occ.get(0)));
			occ = freq2("ore");
			for(int j = 0; j <= i; j ++)
			{
				occ.remove(0);
			}
			i++;			
		}
	}
	
	/**
	 * Finds every implication and parenthesises
	 */
	private static void findimp()
	{
		ArrayList<Integer> occ = freq2("imp");
		int i = 0;
		int orig = occ.size();
		while(i < orig)
		{
			ArrayList<String> infront = findforward(occ.get(0));
			ArrayList<String> behind = findbackward(occ.get(0));
			find(behind,infront,(occ.get(0)));
			occ = freq2("imp");
			for(int j = 0; j <= i; j ++)
			{
				occ.remove(0);
			}
			i++;			
		}
	}
	
	/**
	 * Finds every equivalence and parenthesises
	 */
	private static void findequiv()
	{
		ArrayList<Integer> occ = freq2("equ");
		int i = 0;
		int orig = occ.size();
		while(i < orig)
		{
			ArrayList<String> infront = findforward(occ.get(0));
			ArrayList<String> behind = findbackward(occ.get(0));
			find(behind,infront,(occ.get(0)));
			occ = freq2("equ");
			for(int j = 0; j <= i; j ++)
			{
				occ.remove(0);
			}
			i++;			
		}
	}

	/**
	 * Converts the list of symbols into clause normal form
	 */
	private static void convert()
	{
		boolean nochange = true;
		while(true)
		{
			nochange = true;
			
			nochange = doublenegation(nochange);
			if(debug)System.out.println("After convert double negation" + symbols);
			nochange = equivalence(nochange);
			if(debug)System.out.println("After equivalence" + symbols);
			nochange = implication(nochange);
			if(debug)System.out.println("After implication" + symbols);
			nochange = demorgan(nochange);
			if(debug)System.out.println("After De'Morgans" + symbols);
			nochange = distribute(nochange);
			if(debug)System.out.println("After distributivity" + symbols);
			if(nochange)
			{
				break;
			}
		}

	}
	/**
	 * Gets rid of double negation
	 * @param nochange
	 * @return
	 */
	private static boolean doublenegation(boolean nochange) {
		
		for(int i = 0; i < symbols.size(); i++)
		{
			if(symbols.get(i).equals("negneg"))
			{
				ArrayList<String> infront = findforward(i);
				symbols.remove(i+infront.size());
				symbols.remove(i+1);
				symbols.remove(i);
				
			}
			if(symbols.get(i).contains("negneg"))
			{
				String temp = symbols.get(i).replaceAll("negneg", "");
				symbols.set(i, temp);
				nochange = false;
			}
		}
		return nochange;
	}


	private static boolean equivalence(boolean nochange)
	{
		for(int i = 0; i < symbols.size(); i++)
		{
			if(symbols.get(i).equals("equ"))
			{
				ArrayList<String> infront = findforward(i);
				ArrayList<String> behind = findbackward(i);
				
				symbols.set(i, "and");
				//RHS
				symbols.add((i + infront.size() + 1), "cbr");
				ArrayList<String> behind2 = behind;
				behind2.add("imp");
				behind2.add(0, "obr");
				symbols.addAll(i+1,	behind2);
				
				//LHS
				symbols.add(i ,"cbr");
				ArrayList<String> infront2 = infront;
				infront2.add("imp");
				symbols.addAll(i-(behind.size()-2), infront2);
				symbols.add(i - (behind.size()-1), "obr");
				nochange = false;
			}
		}
		
		return nochange;
	}
	
	private static boolean implication(boolean nochange)
	{
		for(int i = 0; i < symbols.size(); i++)
		{
			if(symbols.get(i).equals("imp"))
			{
				symbols.set(i, "ore");
				ArrayList<String> infront = findforward(i);
				ArrayList<String> behind = findbackward(i);
				symbols.add(i + infront.size() + 1, "cbr");
				if(behind.size() == 1)
				{
					String temp = symbols.get(i - behind.size());
					symbols.set(i - behind.size(), "neg"+temp);
				}
				else
				{
					symbols.add(i-behind.size(), "neg");
				}
				symbols.add(i-behind.size(), "obr");
				nochange = false;
			}
		}
		
		return nochange;
	}

	
	private static boolean demorgan(boolean nochange)
	{
		boolean foundneg = false;
		for(int i = 0; i < (symbols.size()-1); i++)
		{
			if(symbols.get(i).equals("neg") && symbols.get(i+1).equals("obr"))
			{
				ArrayList<String> infront = findforward(i);
				symbols.remove(i+infront.size());
				nochange = false;
				for(int j = i+1; j < i + (infront.size()); j++)
				{
					if(symbols.get(j).equals("neg"))
					{
						symbols.set(j, "negneg");
						foundneg = true;
						break;
					}
					if(symbols.get(j).equals("and"))
					{
						symbols.set(j, "ore");
					}
					else
					{
						if(symbols.get(j).equals("ore"))
						{
							symbols.set(j, "and");
						}
					}
					if(symbols.get(j).matches("[A-Z]+[0-9]*"))
					{
						String temp = symbols.get(j);
						temp = "neg" + temp;
						symbols.set(j, temp);
					}
					else
					{
						if(symbols.get(j).contains("neg"))
						{
							String temp = symbols.get(j);
							temp = temp.replace("neg", "");
							symbols.set(j, temp);
						}
					}
				}
				symbols.remove(i);
				symbols.remove(i);
				if(foundneg)
				{
					nochange = doublenegation(nochange);
				}
			}
		}
		return nochange;
	}
	
	/**
	 * Checks if the distributivity law needs to be applied
	 * @param nochange
	 * @return
	 */
	private static boolean distribute(boolean nochange)
	{
		int pos = ismiddleore("ore");
		boolean findand = symbols.contains("and");
		if(pos != 0 && findand)
		{
			ArrayList<String> behind = findbackward(pos);
			ArrayList<String> infront = findforward(pos); 
			
			int posand = symbols.indexOf("and");
			
			ArrayList<String> behind2 = findbackward(posand);
			ArrayList<String> infront2 = findforward(posand);
			
			ArrayList<String> old = new ArrayList<String>();
			old.addAll(behind2);
			old.add(symbols.get(posand));
			old.addAll(infront2);
			symbols.set(pos, "and");
			
			ArrayList<String> new1 = new ArrayList<String>();
			ArrayList<String> new2 = new ArrayList<String>();
			if(posand > pos)
			{
				for(int i = 0; i < infront.size(); i++)
				{
					symbols.remove(pos+1);
				}
				new1.addAll(behind);
				new1.add(0, "obr");
				new1.add("ore");
				new1.addAll(infront2);
				new1.add("cbr");
				symbols.addAll(pos+1, new1);
				
				new2.addAll(behind2);
				new2.add(0, "ore");
				new2.add("cbr");
				if(behind.size() == 1)
				{
					symbols.add(pos - behind.size(), "obr");
				}
				symbols.addAll(pos, new2);
				
				int pos2 = ismiddle("and");
				int behsize = behind.size() + new2.size() + 1;
				symbols.add(pos2 - behsize, "obr");
			}
			else
			{
				new1.addAll(infront2);
				new1.add(0, "ore");
				symbols.addAll(pos + infront.size(), new1);
				if(infront.size() == 1)
				{
					symbols.add(pos+1, "obr");
				}
				
				for(int i = 0; i < behind.size()-1; i++)
				{
					symbols.remove(pos-(behind.size()-1));
				}
				new2.addAll(behind2);
				new2.add("ore");
				new2.addAll(infront);
				new2.add("cbr");
				
				symbols.addAll(pos-(behind.size()-1), new2);
				
				
			}
			
			nochange = false;
		}
		return nochange;
	}
	
	/**
	 * Check to see if the center operator is the symbols given - if it return its position
	 * @return
	 */
	private static int ismiddleore(String word)
	{
		int br = 0;
		for(int i = 0; i < symbols.size(); i++)
		{
			if(symbols.get(i).equals("obr"))
			{
				br++;
			}
			if(symbols.get(i).equals("cbr"))
			{
				br--;
			}
			if(symbols.get(i).equals(word) && br == 1)
			{
				return i;
			}
		}
		return 0;
	}
	
	private static int ismiddle(String word)
	{
		int br = 0;
		for(int i = 0; i < symbols.size(); i++)
		{
			if(symbols.get(i).equals("obr"))
			{
				br++;
			}
			if(symbols.get(i).equals("cbr"))
			{
				br--;
			}
			if(symbols.get(i).equals(word) && br == 0)
			{
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Once finishes the list is split by conjunctives and put into sets
	 */
	private static void makeclauses()
	{

		ArrayList<String> clause = new ArrayList<String>();
		int occ = freq("ore");
		int i = 0;
		while(true)
		{
			if(symbols.get(i).equals("ore"))
			{
				ArrayList<String> infront = findforward(i);
				ArrayList<String> behind = findbackward(i);
				if(symbols.get(i - behind.size()).equals("obr"))
				{
					symbols.remove(i - behind.size());
					symbols.remove(i-2);
					i -= 2;
				}
				if(symbols.get(i + infront.size()).equals("cbr"))
				{
					symbols.remove(i + infront.size());
					symbols.remove(i+1);
				}
				occ--;
			}
			i++;
			if(occ == 0)
			{
				break;
			}
		}
		symbols.remove(0);
		symbols.remove(symbols.size()-1);
		for(int j = 0; j < symbols.size(); j++)
		{
			if(symbols.get(j).equals("and"))
			{
				System.out.println("{" + toletters(clause) + "}");
				clause.clear();
			}
			else
			{
				clause.add(symbols.get(j));
			}
		}
		
		System.out.println("{" + toletters(clause) + "}");
	}
	
	private static ArrayList<String> removebr(ArrayList<String> clause)
	{
		int i = 0;
		while(true)
		{
			if(clause.get(i).equals("obr") || clause.get(i).equals("cbr"))
			{
				clause.remove(i);
			}
			else
			{
				i++;
			}
			if(i == clause.size())
			{
				break;
			}
		}
		return clause;
	}
	
	/**
	 * Converts back from the symbols format to letters
	 * @param clause
	 * @return
	 */
	public static String toletters(ArrayList<String> clause)
	{
		ArrayList<String> clause2 = new ArrayList<String>();
		clause2.addAll(clause);
		clause2 = removebr(clause2);
		finishedclauses.add(clause2);
		boolean done;
		String fin = "";
		for(String i:clause)
		{
			done = false;
			if(i.equals("obr"))
			{
				fin += "(";
				done = true;
			}
			if(i.equals("cbr"))
			{
				fin += ")";
				done = true;
			}
			if(i.equals("ore"))
			{
				fin += ",";
				done = true;
			}
			if(i.contains("neg"))
			{
				i = i.replace("neg", "-");
				fin += " " + i + " ";
				done = true;
			}
			if(!done)
			{
				fin += " " + i + " ";
			}
		}
		fin = deletebrackets(fin);
		fin = deleteleftover(fin);
		return fin;
	}
	
	/**
	 * Deletes unimportant brackets
	 * @param fin
	 * @return
	 */
	private static String deletebrackets(String fin)
	{
		fin = fin.replaceFirst("\\(", "");
		fin = fin.substring(0, fin.length()-1);
		return fin;
	}
	
	/**
	 * Deletes weird brackets!
	 */
	private static String deleteleftover(String fin)
	{
		fin = fin.replaceAll("\\(", "");
		fin = fin.replaceAll("\\)", "");
		return fin;
	}
	

}
