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
 * Negation pushed - DE morgan's - ish!
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
 * -(-(-(A | B))) - fails
 * 
 */
public class Main2 {
	
	private static char[] inputcon;
	private static ArrayList<String> inputs;
	private static ArrayList <String> symbols = new ArrayList<String>();
	private static ArrayList <ArrayList <String>> clauses = new ArrayList<ArrayList<String>>();
	
	
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
		
		for(String inpt : inputs)
		{
			symbols.clear();
			inpt = inpt.replaceAll(" ", "");
			makesymbols(inpt);
			parenthesise();
			System.out.println("After parenthesise" + symbols);
			convert();
			System.out.println("After convert" + symbols);
			makeclauses();
			clauses.add(symbols);
		}
		/*input = "-(-(-(A | B)))";
		System.out.println(input);
		input = input.replaceAll(" ", "");
		

		makesymbols(input);
		parenthesise();
		System.out.println("After parenthesise" + symbols);
		convert();
		System.out.println("After convert" + symbols);
		makeclauses();	*/

	}
	
	/**
	 * Converts the input into a form which is used by the program
	 */
	private static void makesymbols(String input)
	{
		input = input.replaceAll("\\)", " cbr ");
		input = input.replaceAll("\\(", "obr ");
		input = input.replaceAll("<->", " equ ");
		input = input.replaceAll("\\->", " imp ");
		input = input.replaceAll("\\-", "neg");
		input = input.replaceAll("\\&", " and ");
		input = input.replaceAll("\\|", " ore ");
		
		
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
				symbols.add("neg");
				symbols.add("obr");
				current = "";
			}
			if(current.isEmpty() == false)
			{
				symbols.add(current);
			}
			if(i >= inputcon.length)
			{
				break;
			}
		}
		
		
		
	}	
	/**
	 * Puts correct brackets in
	 */
	private static void parenthesise()
	{
		findnegation();
		System.out.println("After negation" + symbols);
		findconj();
		System.out.println("After conjunction" + symbols);
		finddisj();
		System.out.println("After disjunction" + symbols);
		findimp();
		findequiv();
	}
	
	/**
	 * Uses the parts before and after the symbol specified to parenthesise corectly
	 * @param behind
	 * @param infront
	 * @param pos
	 */
	private static void find(ArrayList<String> behind, ArrayList<String> infront, int pos)
	{
		/*if(behind.isEmpty())
		{
			symbols.add(pos, "cbr");
			symbols.add(pos-2, "obr");
		}
		else
		{
			int length = behind.size();
			symbols.add(pos, "cbr");
			symbols.add(pos-length, "obr");
		}
		
		pos += 2;
		if(infront.isEmpty())
		{
			symbols.add(pos+2, "cbr");
			symbols.add(pos+1, "obr");
		}
		else
		{
			int length = infront.size();
			symbols.add(pos+length+1, "cbr");
			symbols.add(pos+1, "obr");
		*/
		
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
		for(int i:occ)
		{
			ArrayList<String> infront = findforward(i);
			ArrayList<String> behind = findbackward(i);
			find(behind,infront,i);			
		}
	}
	
	/**
	 * Finds every disjunction and parenthesises
	 */
	private static void finddisj()
	{
		ArrayList<Integer> occ = freq2("ore");
		for(int i:occ)
		{
			ArrayList<String> infront = findforward(i);
			ArrayList<String> behind = findbackward(i);
			find(behind,infront,i);			
		}
	}
	
	/**
	 * Finds every implication and parenthesises
	 */
	private static void findimp()
	{
		ArrayList<Integer> occ = freq2("imp");
		for(int i:occ)
		{
			ArrayList<String> infront = findforward(i);
			ArrayList<String> behind = findbackward(i);
			find(behind,infront,i);			
		}
	}
	
	/**
	 * Finds every equivalence and parenthesises
	 */
	private static void findequiv()
	{
		ArrayList<Integer> occ = freq2("equ");
		for(int i:occ)
		{
			ArrayList<String> infront = findforward(i);
			ArrayList<String> behind = findbackward(i);
			find(behind,infront,i);			
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
			System.out.println("After convert double negation" + symbols);
			nochange = equivalence(nochange);
			System.out.println("After equivalence" + symbols);
			nochange = implication(nochange);
			System.out.println("After implication" + symbols);
			nochange = demorgan(nochange);
			System.out.println("After De'Morgans" + symbols);
			
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
				System.out.println("infront" + infront);
				System.out.println("behind" + behind);
				
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
				if(infront.size() == 1)
				{
					String temp = symbols.get(i - behind.size());
					System.out.println(temp);
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
		for(int i = 0; i < (symbols.size()-1); i++)
		{
			if(symbols.get(i).equals("neg") && symbols.get(i+1).equals("obr"))
			{
				ArrayList<String> infront = findforward(i);
				for(int j = i; j < i + (infront.size()-1); j++)
				{
					if(symbols.get(j).equals("neg") && symbols.get(j+1).equals("obr"))
					{
						symbols.remove(j);
					}
				}
				demorgan2(i);
				nochange = false;
			}
		}
		
		return nochange;
	}
	
	private static void demorgan2(int i)
	{
		if(symbols.get(i).equals("neg") && symbols.get(i+1).equals("obr"))
		{
			ArrayList<String> infront = findforward(i);
			symbols.remove(i+infront.size());
			for(int j = i + (infront.size()-1); j >= i; j--)
			{
				if(symbols.get(j).equals("neg"))
				{
					symbols.remove(j);
				}
				else
				{
					if(symbols.get(j).contains("neg"))
					{
						String temp = symbols.get(j);
						temp = temp.replace("neg", "negneg");
						symbols.set(j, temp);
					}
				}
				if(symbols.get(j).equals("and"))
				{
					symbols.set(j, "ore");
				}
				if(symbols.get(j).equals("ore"))
				{
					symbols.set(j, "and");
				}
				if(symbols.get(j).matches("[A-Z]+[0-9]*"))
				{
					String temp = symbols.get(j);
					temp = "neg" + temp;
					symbols.set(j, temp);
				}
			}
			symbols.remove(i);
		}
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
				clause.clear();;
			}
			else
			{
				clause.add(symbols.get(j));
			}
		}
		System.out.println("{" + toletters(clause) + "}");
	}
	
	/**
	 * Converts back from the symbols format to letters
	 * @param clause
	 * @return
	 */
	private static String toletters(ArrayList<String> clause)
	{
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
	

}
