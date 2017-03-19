package cnf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Negation pushed - DE morgan's
 * Implication removed
 * Equivalence removed
 * Distributivity
 * Double negation
 * 
 */

public class Main {
	
	private static String input;
	private static String looking;
	private static char[] inputcon;
	private static ArrayList <String> chars;
	private static ArrayList <String> parts;

	public static void main(String[] args) {
		
		input = "-A & (B | --C)";
		/*ArrayList<String> test= new ArrayList<String>();
		test.add("(");
		test.add(")");
		test.add("(");
		System.out.println(test);*/
	
		chars = new ArrayList<String>();
		input = input.replaceAll(" ", "");
		looking = input;
		inputcon = input.toCharArray();
		
		/*parenthesise();
		
		splitup();
		
		convert();
		//removeslashes();
		makeclauses();*/
		
		find("(\\-+[A-Z]+[0-9]*)");
		System.out.println(input);
		find("(\\(\\-A\\)\\&\\(B\\|\\(\\-\\-C\\)\\))");
		System.out.println(input);
		

	}

	private static void parenthesise()
	{
		findnegation();
		System.out.println("After negation" + input);
		inputcon = input.toCharArray();
		looking = input;
		findconj();
		System.out.println("After conjunction" + input);
		inputcon = input.toCharArray();
		looking = input;
		input = inputcon.toString();
		finddisj();
		input = inputcon.toString();
		findimp();
		input = inputcon.toString();
		findequiv();
		input = inputcon.toString();
	}
	
	private static void find(String regex)
	{
		ArrayList <String> matches = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		while(matcher.find())
		{					
			matches.add(matcher.group(1));
		}
		System.out.println("Matches" + matches);
		for(String i : matches)
		{
			System.out.println("match" + i);
			input = input.replaceAll(i, "(" + i + ")");
		}
	}
	
	private static String findforward(int pos)
	{
		String result = "";
		int nobrackets = 0;
		boolean done = false;
		char[] temp;
		temp = Arrays.copyOfRange(inputcon, pos+1, inputcon.length);
		for(int j = 0; j < temp.length; j++)
		{
			done = false;
			if(temp[j] == '(')
			{
				nobrackets++;
				result = result + "\\\\(";
				done = true;
			}
			if(temp[j] == ')')
			{
				nobrackets--;
				result = result + "\\\\)";
				done = true;
			}
			if(temp[j] == '-')
			{
				result = result + "\\\\-";
				done = true;
			}
			if(temp[j] == '|')
			{
				result = result + "\\\\|";
				done = true;
			}
			if (!done)
			{
				result = result + temp[j];
			}
			if(nobrackets == 0)
			{
				return result;
			}
			
		}
		return result;
	}
	
	private static String findbackward(int pos)
	{
		String result = "";
		int nobrackets = 0;
		boolean done = false;
		char[] temp;
		temp = Arrays.copyOfRange(inputcon, 0, pos);
		temp = reverse(temp);
		for(int j = 0; j < temp.length; j++)
		{
			done = false;
			if(temp[j] == ')')
			{
				nobrackets++;
				result = "\\\\)" + result;
				done = true;
			}
			if(temp[j] == '(')
			{
				nobrackets--;
				result = "\\\\(" + result;
				done = true;
			}
			if(temp[j] == '-')
			{
				result = "\\\\-" + result;
				done = true;
			}
			if(temp[j] == '|')
			{
				result = "\\\\|" + result;
				done = true;
			}
			if(!done)
			{
				result = temp[j] + result;
			}
			System.out.println(result);
			if(nobrackets == 0)
			{
				return result;
			}
			
		}
		return result;
	}
	
	private static char[] reverse(char[] old)
	{
		char[] rev = new char[old.length];
		for(int i = 0; i< old.length; i++)
		{
			rev[old.length-1-i] = old[i];
		}
		return rev;
	}
	
	private static void findnegation()
	{
		if(input.matches("(.*)\\-(.*)"))
		{
			find("(\\-+[A-Z]+[0-9]*)");
		}
	}
	
	private static void findconj()
	{
		for(int i = 0; i < inputcon.length; i++)
		{
			if(inputcon[i] == '&')
			{
				String infront = findforward(i);
				String behind = findbackward(i);
				if(infront == "" && behind != "")
				{
					find("(" + behind + "\\\\&[A-Z]+[0-9]*)");
				}
				if(infront != "" && behind == "")
				{
					find("([A-Z]+[0-9]*\\\\&" + infront + ")");
				}
				if(infront != "" && behind != "")
				{
					System.out.println("What I've found " + "(" + behind + "\\\\&" + infront + ")");
					find("(" + behind + "\\\\&" + infront + ")");
				}
			}
			find("([A-Z]+[0-9]*\\\\&[A-Z]+[0-9]*)");
		}
	}
	
	private static void finddisj()
	{
		
	}
	
	private static void findimp()
	{
		
	}
	
	private static void findequiv()
	{
		
	}
	
	
	private static void splitup()
	{
		String current = "";
		for(int i = 0; i < inputcon.length; i++)
		{
			if(inputcon[i] == ' ' && current != "")
			{
				chars.add(current);
				current = "";
			}
			else
			{
				if(inputcon[i] == '(' | inputcon[i] == ')')
				{
					if(current != "")
					{
						chars.add(current);
						current = "";
					}
					String temp = String.valueOf(inputcon[i]);
					chars.add(temp);
				}
				else
				{
					if(inputcon[i] != ' ')
					{
						current = current + inputcon[i];
					}
				}
			}
		}
		if(current != "")
		{
			chars.add(current);
		}
	}
	
	private static void convert()
	{
		boolean nochange = true;
		while(true)
		{
			nochange = true;
			nochange = doublenegation(nochange);
			
			if(nochange)
			{
				break;
			}
		}

	}
	
	private static boolean doublenegation(boolean nochange) {
		
		for(int i = 0; i < chars.size(); i++)
		{
			if(chars.get(i).matches("(.*)--(.*)"))
			{
				String temp = chars.get(i);
				temp = temp.replaceAll("--","");
				nochange = false;
				chars.set(i, temp);
			}
		}
		return nochange;
	}


	private static void makeclauses()
	{
		String clause = "";
		for(int i = 0; i < chars.size(); i++)
		{
			String temp = chars.get(i);
			if(temp.matches("(.*)&(.*)"))
			{
				clause = ridbrackets(clause);
				
				System.out.println("{" + clause + "}");
				clause = "";
			}
			else
			{
				if(temp.matches("(.*)\\|(.*)"))
				{
					clause = clause + ",";
				}
				else
				{
					clause = clause + temp;
				}
			}
		}
		clause = ridbrackets(clause);
		System.out.println("{" + clause + "}");
	}
	
	private static String ridbrackets(String clause)
	{
		clause = clause.replaceAll("\\(", "");
		clause = clause.replaceAll("\\)", "");
		return clause;
	}
	

}
