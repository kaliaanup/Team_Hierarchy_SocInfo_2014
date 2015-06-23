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

public class FeaturesOutput {
	public static ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();
	public static ArrayList<TaggedWord> prevTaggedSentences = new ArrayList<TaggedWord>();
	public static ArrayList<TaggedWord> currTaggedSentences= new ArrayList<TaggedWord>();
	public static ArrayList<String> responses = new ArrayList<String>();
	public static ArrayList<String> messages = new ArrayList<String>();
	public static ArrayList<String> directives=new ArrayList<String>();
	
	public static ArrayList<ArrayList<String>> featuresP1 = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresP2 = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresP3 = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresP1P2 = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresP1P2P3 = new ArrayList<ArrayList<String>>();
	
	public static int[][] adjacentP1;
	public static int[][] adjacentP2;
	public static int[][] adjacentP3;
	public static int[][] adjacentP1P2;
	public static int[][] adjacentP1P2P3;
	
	public static ArrayList<String> uniqueIDArray = new ArrayList<String>();
	
	public static Tree parse1;
	public static Tree parse2;
	
	public static long totalResponseTimeP1=0;
	public static long totalResponseTimeP2=0;
	public static long totalResponseTimeP3=0;
	
	public static int negativeEmotionP1=0;
	public static int negativeEmotionP2=0;
	public static int negativeEmotionP3=0;
	
	public static int neutralEmotionP1=0;
	public static int neutralEmotionP2=0;
	public static int neutralEmotionP3=0;
	
	public static int positiveEmotionP1=0;
	public static int positiveEmotionP2=0;
	public static int positiveEmotionP3=0;
	
	public static int responsePatternP1=0;
	public static int responsePatternP2=0;
	public static int responsePatternP3=0;
	
	
	public static int numberOfMessages=0;
	
	public static void initialize()
	{
		totalResponseTimeP1=0;
		totalResponseTimeP2=0;
		totalResponseTimeP3=0;
		
		negativeEmotionP1=0;
		negativeEmotionP2=0;
		negativeEmotionP3=0;
		
		neutralEmotionP1=0;
		neutralEmotionP2=0;
		neutralEmotionP3=0;
		
		positiveEmotionP1=0;
		positiveEmotionP2=0;
		positiveEmotionP3=0;
		
		responsePatternP1=0;
		responsePatternP2=0;
		responsePatternP3=0;
		
		numberOfMessages=0;
	}
	
	public static void initializeAdjacentMatrix()
	{
		int sizeOfMatrix = uniqueIDArray.size();
		adjacentP1 = new int[sizeOfMatrix][sizeOfMatrix];
		adjacentP2 = new int[sizeOfMatrix][sizeOfMatrix];
		adjacentP3 = new int[sizeOfMatrix][sizeOfMatrix];
		adjacentP1P2 = new int[sizeOfMatrix][sizeOfMatrix];
		adjacentP1P2P3 = new int[sizeOfMatrix][sizeOfMatrix];
		
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentP1[i][j]=0;
			}
		}
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentP2[i][j]=0;
			}
		}
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentP3[i][j]=0;
			}
		}
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentP1P2[i][j]=0;
			}
		}
		for(int i=0; i<sizeOfMatrix;i++)
		{
			for(int j=0; j<sizeOfMatrix;j++)
			{
				adjacentP1P2P3[i][j]=0;
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
		ArrayList<String> userP1 = new ArrayList<String>();
		userP1.add(String.valueOf(userID));//store user ID
		userP1.add(String.valueOf(responsePatternP1));//store total no of P1
		if(responsePatternP1 > 0)
		{
			averageTimeResponseDelay = totalResponseTimeP1/Long.valueOf(responsePatternP1);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		userP1.add(String.valueOf(negativeEmotionP1));
		userP1.add(String.valueOf(neutralEmotionP1));
		userP1.add(String.valueOf(positiveEmotionP1));
		userP1.add(String.valueOf(averageTimeResponseDelay));
		userP1.add(String.valueOf(numberOfMessages));
		featuresP1.add(userP1);
		/*--------------------------P2---------------------------------------*/
		ArrayList<String> userP2 = new ArrayList<String>();
		userP2.add(String.valueOf(userID));//store user ID
		userP2.add(String.valueOf(responsePatternP2));//store total no of P1
		if(responsePatternP2 > 0)
		{
			averageTimeResponseDelay = totalResponseTimeP2/Long.valueOf(responsePatternP2);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		userP2.add(String.valueOf(negativeEmotionP2));
		userP2.add(String.valueOf(neutralEmotionP2));
		userP2.add(String.valueOf(positiveEmotionP2));
		userP2.add(String.valueOf(averageTimeResponseDelay));
		userP2.add(String.valueOf(numberOfMessages));
		featuresP2.add(userP2);
		/*--------------------------P3---------------------------------------*/
		ArrayList<String> userP3 = new ArrayList<String>();
		userP3.add(String.valueOf(userID));//store user ID
		userP3.add(String.valueOf(responsePatternP3));//store total no of P1
		if(responsePatternP3 > 0)
		{
			averageTimeResponseDelay = totalResponseTimeP3/Long.valueOf(responsePatternP3);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		userP3.add(String.valueOf(negativeEmotionP3));
		userP3.add(String.valueOf(neutralEmotionP3));
		userP3.add(String.valueOf(positiveEmotionP3));
		userP3.add(String.valueOf(averageTimeResponseDelay));
		userP3.add(String.valueOf(numberOfMessages));
		featuresP3.add(userP3);
		/*--------------------------P1P2---------------------------------------*/
		ArrayList<String> userP1P2 = new ArrayList<String>();
		userP1P2.add(String.valueOf(userID));//store user ID
		userP1P2.add(String.valueOf(responsePatternP1+responsePatternP2));//store total no of P1
		if((responsePatternP1+responsePatternP2) > 0)
		{
			averageTimeResponseDelay = (totalResponseTimeP1 + totalResponseTimeP2)/Long.valueOf(responsePatternP1+responsePatternP2);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		userP1P2.add(String.valueOf(negativeEmotionP1+negativeEmotionP2));
		userP1P2.add(String.valueOf(neutralEmotionP1+neutralEmotionP2));
		userP1P2.add(String.valueOf(positiveEmotionP1+neutralEmotionP2));
		userP1P2.add(String.valueOf(averageTimeResponseDelay));
		userP1P2.add(String.valueOf(numberOfMessages));
		featuresP1P2.add(userP1P2);
		/*--------------------------P1P2P3---------------------------------------*/
		ArrayList<String> userP1P2P3 = new ArrayList<String>();
		userP1P2P3.add(String.valueOf(userID));//store user ID
		userP1P2P3.add(String.valueOf(responsePatternP1+responsePatternP2+responsePatternP3));//store total no of P1
		if((responsePatternP1+responsePatternP2+responsePatternP3) > 0)
		{
			averageTimeResponseDelay = (totalResponseTimeP1 + totalResponseTimeP2 + totalResponseTimeP3)/Long.valueOf(responsePatternP1+responsePatternP2+responsePatternP3);
		}
		else
		{
			averageTimeResponseDelay = 0;
		}
		
		userP1P2P3.add(String.valueOf(negativeEmotionP1+negativeEmotionP2 + negativeEmotionP3));
		userP1P2P3.add(String.valueOf(neutralEmotionP1+neutralEmotionP2 + neutralEmotionP3));
		userP1P2P3.add(String.valueOf(positiveEmotionP1+neutralEmotionP2+positiveEmotionP3));
		userP1P2P3.add(String.valueOf(averageTimeResponseDelay));
		userP1P2P3.add(String.valueOf(numberOfMessages));
		featuresP1P2P3.add(userP1P2P3);
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
		parse1 = lp.parse(prevSentence);
		parse2 = lp.parse(currSentence);
		prevTaggedSentences = parse1.taggedYield();
		currTaggedSentences = parse2.taggedYield();
		
		String message=new String(""); 
		String response=new String("");
		int[] prevEmotion=new int[5];
		int[] currEmotion=new int[5];
		long responseDelay=0;
		int indexResponder=0;
		int indexRespondee=0;
		
		if(responder.equals(respondee) || (csc.ncsu.edu.cta.utility.LevenshteinDistance.domainRulesSimilarity(respondee, responder) == 1))
		{
			//System.out.println(sender+"----------"+reciever);
			responses.add("N");//no hierarchy if both are the same person
		}
		else
		{
			response = Utility.identifyPattern(parse1, parse2, prevTaggedSentences, currTaggedSentences, directives);
			prevEmotion = Utility.emotion(prevSentence, pipeline);
			currEmotion = Utility.emotion(currSentence, pipeline);
			responseDelay = Utility.responseTimeDelay(index, arrayList);
			responses.add(response); //Investigate if the current sentence is a response to the previous and investigate what's the pattern\
			indexRespondee = getIndexofUsers(respondee);
			indexResponder = getIndexofUsers(responder);
			if(response.equals("P1"))
			{
				responsePatternP1 = responsePatternP1 + 1;
				totalResponseTimeP1 = totalResponseTimeP1 + responseDelay;
				negativeEmotionP1   = negativeEmotionP1 + currEmotion[0]+currEmotion[1];
				neutralEmotionP1    = neutralEmotionP1  + currEmotion[2];
				positiveEmotionP2   = positiveEmotionP1 + currEmotion[3] + currEmotion[4];
				if(adjacentP1[indexResponder][indexRespondee] == 0)
				{
					adjacentP1[indexResponder][indexRespondee] = adjacentP1[indexResponder][indexRespondee] + 1;
				}
				if(adjacentP1P2[indexResponder][indexRespondee] == 0)
				{
					adjacentP1P2[indexResponder][indexRespondee] = adjacentP1P2[indexResponder][indexRespondee] + 1;
				}
				if(adjacentP1P2P3[indexResponder][indexRespondee] == 0)
				{
					adjacentP1P2P3[indexResponder][indexRespondee] = adjacentP1P2P3[indexResponder][indexRespondee] + 1;
				}
			}
			else if(response.equals("P2"))
			{
				totalResponseTimeP2 = totalResponseTimeP2 + responseDelay;
				negativeEmotionP2   = negativeEmotionP2 + currEmotion[0]+currEmotion[1];
				neutralEmotionP2    = neutralEmotionP2  + currEmotion[2];
				positiveEmotionP2   = positiveEmotionP2 + currEmotion[3] + currEmotion[4];
				responsePatternP2 = responsePatternP2 + 1;
				if(adjacentP2[indexResponder][indexRespondee] == 0)
				{
					adjacentP2[indexResponder][indexRespondee] = adjacentP2[indexResponder][indexRespondee] + 1;
				}
				if(adjacentP1P2[indexResponder][indexRespondee] == 0)
				{
					adjacentP1P2[indexResponder][indexRespondee] = adjacentP1P2[indexResponder][indexRespondee] + 1;
				}
				if(adjacentP1P2P3[indexResponder][indexRespondee] == 0)
				{
					adjacentP1P2P3[indexResponder][indexRespondee] = adjacentP1P2P3[indexResponder][indexRespondee] + 1;
				}
				
			}
			else if(response.equals("P3"))
			{
				totalResponseTimeP3 = totalResponseTimeP3 + responseDelay;
				negativeEmotionP3   = negativeEmotionP3 + currEmotion[0]+currEmotion[1];
				neutralEmotionP3    = neutralEmotionP3  + currEmotion[2];
				positiveEmotionP3   = positiveEmotionP3 + currEmotion[3] + currEmotion[4];
				responsePatternP3 = responsePatternP3 + 1;
				if(adjacentP3[indexResponder][indexRespondee] == 0)
				{
					adjacentP3[indexResponder][indexRespondee] = adjacentP3[indexResponder][indexRespondee] + 1;
				}
				if(adjacentP1P2P3[indexResponder][indexRespondee] == 0)
				{
					adjacentP1P2P3[indexResponder][indexRespondee] = adjacentP1P2P3[indexResponder][indexRespondee] + 1;
				}
			}
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
		
		generateCSVFileDouble(featuresP1, ProjectConfig.featuresFileP1);
		generateCSVFileDouble(featuresP2, ProjectConfig.featuresFileP2);
		generateCSVFileDouble(featuresP3, ProjectConfig.featuresFileP3);
		generateCSVFileDouble(featuresP1P2, ProjectConfig.featuresFileP1P2);
		generateCSVFileDouble(featuresP1P2P3, ProjectConfig.featuresFileP1P2P3);
		
		generateCSVFileSingle(uniqueIDArray, ProjectConfig.userIDFile);
		
		generateCSVFileMatrix(adjacentP1, ProjectConfig.adjacentFileP1, uniqueIDArray.size());
		generateCSVFileMatrix(adjacentP2, ProjectConfig.adjacentFileP2, uniqueIDArray.size());
		generateCSVFileMatrix(adjacentP3, ProjectConfig.adjacentFileP3, uniqueIDArray.size());
		generateCSVFileMatrix(adjacentP1P2, ProjectConfig.adjacentFileP1P2, uniqueIDArray.size());
		generateCSVFileMatrix(adjacentP1P2P3, ProjectConfig.adjacentFileP1P2P3, uniqueIDArray.size());
		
		//System.out.println(uniqueIDArray);
	}
}
