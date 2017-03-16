package cnf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.util.ArrayList;

/*
 * Negation pushed - DE morgan's
 * Implication removed
 * Equivalence removed
 * Distributivity
 * Double negation
 * 
 */

public class Main {
	
	private static char[] inputcon;
	private static ArrayList <String> chars;
	private static ArrayList <String> parts;

	public static void main(String[] args) {
		
		String input = "A & (B v -C)";
		/*ArrayList<String> test= new ArrayList<String>();
		test.add("(");
		test.add(")");
		test.add("(");
		System.out.println(test);*/
		inputcon = input.toCharArray();
		System.out.println(inputcon);
		chars = new ArrayList<String>();

		parenthesise();
		splitup();
		System.out.println(chars);
		
		convert();
		
		makeclauses();
		

	}

	private static void parenthesise()
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
			nochange = doublenegation(nochange);
		}

	}
	
	private static boolean doublenegation(boolean nochange) {
		
		for(int i = 0; i < chars.size(); i++)
		{
			if(chars.get(i).matches("--"))
			{
				chars.get(i).replaceAll("--","");
				nochange = false;
			}
		}
		return nochange;
	}


	private static void makeclauses()
	{
		String clause = "";
		System.out.println("here");
		for(int i = 0; i < parts.size(); i++)
		{
			if(parts.get(i) == "&")
			{
				System.out.println("{" + clause + "}");
				clause = "";
			}
			else
			{
				clause = clause + parts.get(i);
			}
		}
	}
	
	
	

}
