package csc.ncsu.edu.main;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import csc.ncsu.edu.cta.utility.LevenshteinDistance;


//import local folders
import csc.ncsu.edu.config.ProjectConfig;
import csc.ncsu.edu.cta.utility.Utility;

public class NaiveOutput {
	public static ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
	public static ArrayList<TaggedWord> prevTaggedSentences = new ArrayList<TaggedWord>();
	public static ArrayList<TaggedWord> currTaggedSentences= new ArrayList<TaggedWord>();
	public static ArrayList<String> responses = new ArrayList<String>();
	public static ArrayList<String> messages = new ArrayList<String>();
	public static ArrayList<String> directives=new ArrayList<String>();
	
	public static ArrayList<ArrayList<String>> featuresNaive = new ArrayList<ArrayList<String>>();
	
	public static int[][] adjacentNaive;
	
	public static ArrayList<String> uniqueIDArray = new ArrayList<String>();
	
	public static Tree parse1;
	public static Tree parse2;
	
	public static long totalResponseTimeNaive=0;
	
	public static int negativeEmotionNaive=0;
	
	public static int neutralEmotionNaive=0;
	
	public static int positiveEmotionNaive=0;
	
	public static int responsePatternNaive=0;
	
	
	public static int numberOfMessages=0;
	
	public static void initialize()
	{
		totalResponseTimeNaive=0;
		negativeEmotionNaive=0;
		neutralEmotionNaive=0;
		positiveEmotionNaive=0;
		responsePatternNaive=0;
		numberOfMessages=0;
	}
	
	public static void initializeAdjacentMatrix()
	{
		int sizeOfMatrix = uniqueIDArray.size();
		adjacentNaive = new int[sizeOfMatrix][sizeOfMatrix];
		
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentNaive[i][j]=0;
			}
		}
		
	}
	public static void resultantOutput()
	{
		ArrayList<String> prevRow = new ArrayList<String>();
		ArrayList<String> currRow = new ArrayList<String>();
		
		LexicalizedParser lp = LexicalizedParser.loadModel(ProjectConfig.grammer);
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		//responses.add("N");//Initialize the first message with N
		for(int i=0; i<uniqueIDArray.size();i++)
		{
			initialize();
			for(int j=2; j<arrayList.size();j++)
			{
				prevRow = arrayList.get(j-1);
				currRow = arrayList.get(j);
				if(j != arrayList.size())
				{
					//check if one of the name in uniqueIDArray is Responder here sender is Respondee..Hence a responder responds to a respondee
					if(Utility.filterName(currRow.get(3)).equals(uniqueIDArray.get(i)))
					{
						updateDataStructures(lp, pipeline, j, prevRow.get(5), currRow.get(5), Utility.filterName(prevRow.get(3)),  Utility.filterName(currRow.get(3)));
						numberOfMessages=numberOfMessages+1;
					}
				}
				//update data structures
			}
			updateFeatureAttributes(i);
		}
	}
	public static void updateFeatureAttributes(int userID)
	{
		long averageTimeResponseDelay=0;
		
		/*--------------------------P1---------------------------------------*/
		ArrayList<String> user = new ArrayList<String>();
		user.add(String.valueOf(userID));//store user ID
		user.add(String.valueOf(responsePatternNaive));//store total no of P1
		if(responsePatternNaive > 0)
		{
			averageTimeResponseDelay = totalResponseTimeNaive/Long.valueOf(responsePatternNaive);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		user.add(String.valueOf(negativeEmotionNaive));
		user.add(String.valueOf(neutralEmotionNaive));
		user.add(String.valueOf(positiveEmotionNaive));
		user.add(String.valueOf(averageTimeResponseDelay));
		user.add(String.valueOf(numberOfMessages));
		featuresNaive.add(user);
		
	}
	public static int getIndexofUsers(String name)
	{
		int index=0;
		for(int i=0; i<uniqueIDArray.size();i++)
		{
			if(uniqueIDArray.get(i).equals(name))
			{
				index = i;
				return index;
			}
		}
		return index;
	}
	public static void generateCSVFileDouble(ArrayList<ArrayList<String>> data, String filename)
	{
		ArrayList<String> subData = new ArrayList<String>();
		try
		{
		    FileWriter writer = new FileWriter(filename);
		    
		    for(int i=0; i<data.size();i++)
		    {
		    	
		    	subData = data.get(i);
		    	for(int j=0; j<subData.size();j++)
		    	{
		    		writer.append(subData.get(j));
				    writer.append(',');
		    	}
		    	writer.append('\n');
		    }
		    
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	public static void generateCSVFileSingle(ArrayList<String> data, String filename)
	{
		try
		{
		    FileWriter writer = new FileWriter(filename);
		    
		    	for(int j=0; j<data.size();j++)
		    	{
		    		writer.append(String.valueOf(j));
		    		writer.append(',');
		    		writer.append(data.get(j));
				    writer.append('\n');
		    	}
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	public static void generateCSVFileMatrix(int[][] data, String filename, int matrixSize)
	{
		try
		{
		    FileWriter writer = new FileWriter(filename);
		    for(int i=0; i<matrixSize;i++)
		    {
		    	for(int j=0; j<matrixSize;j++)
		    	{
		    		writer.append(String.valueOf(data[i][j]));
		    		writer.append(',');
		    	}
		    	writer.append('\n');
		    }	
		    writer.flush();
		    writer.close();
			
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	public static void updateDataStructures(LexicalizedParser lp, StanfordCoreNLP pipeline, int index, String prevSentence, String currSentence, String respondee, String responder)
	{
		//parse1 = lp.parse(prevSentence);
		//parse2 = lp.parse(currSentence);
		//prevTaggedSentences = parse1.taggedYield();
		//currTaggedSentences = parse2.taggedYield();
		
		String message=new String(""); 
		String response=new String("");
		int[] prevEmotion=new int[5];
		int[] currEmotion=new int[5];
		long responseDelay=0;
		int indexResponder=0;
		int indexRespondee=0;
		
		System.out.println(prevSentence);
		
		if(responder.equals(respondee) || (csc.ncsu.edu.cta.utility.LevenshteinDistance.domainRulesSimilarity(respondee, responder) == 1))
		{
			//System.out.println(sender+"----------"+reciever);
			responses.add("N");//no hierarchy if both are the same person
		}
		else
		{
			response = "N1";
			prevEmotion = Utility.emotion(prevSentence, pipeline);
			currEmotion = Utility.emotion(currSentence, pipeline);
			responseDelay = Utility.responseTimeDelay(index, arrayList);
			responses.add(response); //Investigate if the current sentence is a response to the previous and investigate what's the pattern\
			indexRespondee = getIndexofUsers(respondee);
			indexResponder = getIndexofUsers(responder);
			/*if(response.equals("N1"))
			{*/
				responsePatternNaive = responsePatternNaive + 1;
				totalResponseTimeNaive = totalResponseTimeNaive + responseDelay;
				negativeEmotionNaive   = negativeEmotionNaive + currEmotion[0]+currEmotion[1];
				neutralEmotionNaive    = neutralEmotionNaive  + currEmotion[2];
				positiveEmotionNaive   = positiveEmotionNaive + currEmotion[3] + currEmotion[4];
				if(adjacentNaive[indexResponder][indexRespondee] == 0)
				{
					adjacentNaive[indexResponder][indexRespondee] = adjacentNaive[indexResponder][indexRespondee] + 1;
				}
			//}
			
		}
		
	}
	
	public static void main (String args[])
	{
		
		initialize();
		Utility.uploadVerbs(ProjectConfig.directiveVerbsLexicon, directives);
		String csvFileName=ProjectConfig.dataSource;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String temp = new String();
		try {
	 
			br = new BufferedReader(new FileReader(csvFileName));
			while ((line = br.readLine()) != null) 
				{
	 			String[] cell = line.split(cvsSplitBy);
				ArrayList<String> row=new ArrayList<String>();
				
				//Message Id
				if(cell[0] != null)
				{
					row.add(cell[0]);
					//System.out.print(cell[0]);
				}
				else
				{
					row.add("");
				}
				//Chat Room
				//System.out.print(",");
				if(cell[1] != null)
				{
					row.add(cell[1]);
					//System.out.print(cell[1]);
				}
				else
				{
					row.add("");
				}
				//Date
				//System.out.print(",");
				if(cell[2] != null)
				{
					row.add(cell[2]);
					//System.out.print(cell[2]);
				}
				else
				{
					row.add("");
				}
				//System.out.print(",");
				//User Name or ID
				if(cell[3] != null)
				{
					//row.add(filterName(cell[3]));
					row.add(cell[3]);
					//System.out.print(cell[3]);
				}
				else
				{
					row.add("");
				}
				//System.out.print(",");
				//Time
				if(cell[4] != null)
				{
					row.add(cell[4]);
					//System.out.print(cell[4]);
				}
				else
				{
					row.add("");
				}
				//System.out.print(",");
				//Comment
				
				
				temp = "";
				if(cell[5] != null)
				{
					
					temp = temp+cell[5];
					for(int j=6;j<cell.length;j++)
					{
						temp=temp+","+cell[j];
					}
					temp = temp.replace("(en)","");
					row.add(Utility.preProcess(temp));
					
					//System.out.print(temp);
				}
				else
				{
					row.add("");
				}
				//System.out.println("");
				arrayList.add(row);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//getUniqueUserIDs
		
		Utility.numberOfUniqueParticipants(arrayList, uniqueIDArray, 3);
		initializeAdjacentMatrix();
		resultantOutput();
		
		generateCSVFileDouble(featuresNaive, ProjectConfig.featuresFileNaive);
		
		generateCSVFileSingle(uniqueIDArray, ProjectConfig.userIDFile);
		
		generateCSVFileMatrix(adjacentNaive, ProjectConfig.adjacentFileNaive, uniqueIDArray.size());
		
		//System.out.println(uniqueIDArray);
	}
}
