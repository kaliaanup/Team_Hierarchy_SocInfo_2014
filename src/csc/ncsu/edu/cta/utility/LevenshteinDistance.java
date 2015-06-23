package csc.ncsu.edu.cta.utility;

public class LevenshteinDistance {

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}
 
	public static int computeLevenshteinDistance(String str1,String str2) {
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];
 
		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;
 
		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
 
		return distance[str1.length()][str2.length()];    
	}
	
	public static int domainRulesSimilarity(String str1,String str2)
	{
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		
		int countSimilar = 0;
		int countChars = 0;
		
		if(computeLevenshteinDistance(str1,str2) == 1)
		{
			for (int i = 0; i < str1.length(); i++)
			{
				if(str1.charAt(i) == 'i' || 
				   str1.charAt(i) == '1' || 
				   str1.charAt(i) == 'l' || 
				   str1.charAt(i) == '(' || 
				   str1.charAt(i) == '5' || 
				   str1.charAt(i) == 's' || 
				   str1.charAt(i) == 'j' ||
				   str1.charAt(i) == ']' )
				{
					countChars++;
				}
			}
			
			for (int i = 0; i < str1.length(); i++)
			{
				if(str2.length() == str1.length())
				{
					if(str1.charAt(i) !=  str2.charAt(i))
					{
						//Threshold 1
						if((str1.charAt(i) == 'i' && str2.charAt(i) == '1') || (str1.charAt(i) == '1' && str2.charAt(i) == 'i') )
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == 'i' && str2.charAt(i) == 'l') || (str1.charAt(i) == 'l' && str2.charAt(i) == '1'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == '1' && str2.charAt(i) == 'l') || (str1.charAt(i) == 'l' && str2.charAt(i) == '1'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == '1' && str2.charAt(i) == '(') || (str1.charAt(i) == '(' && str2.charAt(i) == 'i'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == '1' && str2.charAt(i) == '(') || (str1.charAt(i) == '(' && str2.charAt(i) == '1'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == 'l' && str2.charAt(i) == '(') || (str1.charAt(i) == '(' && str2.charAt(i) == 'l'))
						{
							countSimilar ++;
						}
						//Threshold 2
						else if((str1.charAt(i) == '5' && str2.charAt(i) == 's') || (str1.charAt(i) == 's' && str2.charAt(i) == '5'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == ']' && str2.charAt(i) == 'i') || (str1.charAt(i) == 'i' && str2.charAt(i) == ']'))
						{
							countSimilar ++;
						}
						else if((str1.charAt(i) == ']' && str2.charAt(i) == 'j') || (str1.charAt(i) == 'j' && str2.charAt(i) == ']'))
						{
							countSimilar ++;
						}
					}
				}
			}
			if(countChars == countSimilar)
			{
				return 1;
			}
			
		}
		
		return 0;
	}
	
	public static void main(String args[])
	{
		/*System.out.println(computeLevenshteinDistance("741344bcdr","74I344s3"));
		System.out.println(domainRulesSimilarity("741344bcdr","74I344s3"));
		
		System.out.println(computeLevenshteinDistance("741344bcdr","74i344bcdr"));
		System.out.println(domainRulesSimilarity("741344bcdr","74i344bcdr"));
		
		System.out.println(computeLevenshteinDistance("741344bcdr","741344ccdr"));
		System.out.println(domainRulesSimilarity("741344bcdr","741344ccdr"));
		
		System.out.println(computeLevenshteinDistance("741344bcdr","715acdr"));
		System.out.println(domainRulesSimilarity("741344bcdr","715acdr"));
		
		System.out.println(computeLevenshteinDistance("741344bcdr","741344ccdr"));
		System.out.println(domainRulesSimilarity("741344bcdr","741344ccdr"));*/
		
		System.out.println(computeLevenshteinDistance("74I344s3","74134453"));
		System.out.println(domainRulesSimilarity("74I344s3","74134453"));
	}
}
