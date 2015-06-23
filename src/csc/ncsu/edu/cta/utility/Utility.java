package csc.ncsu.edu.cta.utility;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class Utility {

	
	public static int[] emotion(String temp, StanfordCoreNLP pipeline)
	{
		Annotation annotation = new Annotation(temp);
		int[] emotionArray = new int[5];
		pipeline.annotate(annotation);
		String veryNegativeEmotionsUttered = new String("");
		veryNegativeEmotionsUttered = "";
		int vNCount=0;
		
		String negativeEmotionsUttered = new String("");
		negativeEmotionsUttered = "";
		int nCount=0;
		
		String neutralEmotionsUttered = new String("");
		neutralEmotionsUttered = "";
		int nuCount=0;
		
		String positiveEmotionsUttered = new String("");
		positiveEmotionsUttered = "";
		int pCount=0;
		
		String veryPositiveEmotionsUttered = new String("");
		veryPositiveEmotionsUttered = "";
		int vPCount=0;
		String finalString = new String("");
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
		{
		      Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
		      int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
		      if(sentiment == 0)
		      {
		    	  vNCount++;
		      }
		      else if(sentiment == 1)
		      {
		    	  nCount++;
		      }
		      else if(sentiment == 2)
		      {
		    	  nuCount++;
		      }
		      else if(sentiment == 3)
		      {
		    	  pCount++;
		      }
		      else if(sentiment == 4)
		      {
		    	  vPCount++;
		      }
		}
		veryNegativeEmotionsUttered = "VNE:"+vNCount;
		negativeEmotionsUttered = ":NE:"+nCount;
		neutralEmotionsUttered = ":NU:"+nuCount;
		positiveEmotionsUttered = ":PE:"+pCount;
		veryPositiveEmotionsUttered = ":VPE:"+vPCount;
		finalString = veryNegativeEmotionsUttered+negativeEmotionsUttered+neutralEmotionsUttered+positiveEmotionsUttered+veryPositiveEmotionsUttered;
		
		emotionArray[0]=vNCount;
		emotionArray[1]=nCount;
		emotionArray[2]=nuCount;
		emotionArray[3]=pCount;
		emotionArray[4]=vPCount;
		
		return emotionArray;
	}
	      
	public static String sentimentString(int sentiment) {
	    switch(sentiment)
	    {
		    case 0:
		      return "Very negative";
		    case 1:
		      return "Negative";
		    case 2:
		      return "Neutral";
		    case 3:
		      return "Positive";
		    case 4:
		      return "Very positive";
		    default:
		      return "Unknown sentiment label " + sentiment;
	    }
	  }
	
	@SuppressWarnings("deprecation")
	public static long responseTimeDelay(int index, ArrayList<ArrayList<String>> arrayList)
	{
		ArrayList<String> prevRow = new ArrayList<String>();
		ArrayList<String> currRow = new ArrayList<String>();
		long timeDiffSum=0;
		
		if(index>0)
		{
		prevRow = arrayList.get(index-1);
		currRow = arrayList.get(index);
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
		try {
			Date prevDate = sdf.parse(prevRow.get(2)+" "+prevRow.get(4));
			//Date prevDate = sdf.parse(prevRow.get(4));
			Date currDate = sdf.parse(currRow.get(2)+" "+currRow.get(4));
			//Date currDate = sdf.parse(currRow.get(4));
			if(prevDate.getDate() == currDate.getDate())
			{
				if(currDate.getTime() > prevDate.getTime())
				{
					timeDiffSum =  currDate.getTime() - prevDate.getTime();
				}
				else
				{
					timeDiffSum = 0;
				}
				
			}
			else
			{
				timeDiffSum = 0;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			timeDiffSum = 0;
		}
		
		}
		
		return timeDiffSum;
	}
	public static String filterName(String name)
	{
		name = name.toLowerCase();
		name = name.replace("_", " ");
		name = name.replace("\"", " ");
		//name = name.replace(".", " ");
		name = name.replaceAll("\\s+","");
		return name;
	}
	
	public static String identifyPattern(Tree parse1, Tree parse2, ArrayList<TaggedWord> prevTaggedSentences, ArrayList<TaggedWord> currTaggedSentences, ArrayList<String> directives)
	{
		String pattern = new String();
		String prevType = new String(); //Directive, Asks a question, Provide Information
		String currType = new String(); //Roger, Provide Information, Yup, Ack, Ok
		System.out.println(prevTaggedSentences);
		//Check if the prevTagged Sentence is a directive
		ArrayList<ArrayList<TaggedWord>> list=new ArrayList<ArrayList<TaggedWord>>();
		ArrayList<ArrayList<TaggedWord>> condition=new ArrayList<ArrayList<TaggedWord>>();
		findSentenceWithSubjects(parse1, list,"",condition);
		
		
		
		/*ArrayList<ArrayList<TaggedWord>> list1=new ArrayList<ArrayList<TaggedWord>>();
		ArrayList<ArrayList<TaggedWord>> condition1=new ArrayList<ArrayList<TaggedWord>>();
		findSentenceWithSubjects(parse2, list1,"",condition1);*/
		
		int directiveFlag=0;
		if(list.size()>0)
		{
			for(int j=0; j<list.size();j++)
			{
				ArrayList<TaggedWord> taggedYield = list.get(j);
				if(isADirective1(taggedYield,directives))
				{
					prevType = "Directive";
					System.out.println("Directive");
					directiveFlag=1;
				}
			}
		}
		
		/*-------------SENDER INFO-------------*/
		/*else if(isADirective(prevTaggedSentences))
		{
			prevType = "Directive";
		}*/
		if(directiveFlag==0)
		{
			if(isADirective(prevTaggedSentences))
			{
				prevType = "Directive";
			}
			else if(isAQuestion(prevTaggedSentences))
			{
				prevType = "Question";
			}
			else
			{
				prevType = "Information";
			}
		}
		/*-------------RECEIVER INFO-------------*/
		/*if(isADirective(currTaggedSentences))
		{
			currType = "Directive";
		}
		else if(isAQuestion(currTaggedSentences))
		{
			currType = "Question";
		}*/
		if(isAReply(currTaggedSentences))
		{
			currType = "reply";
		}
		else
		{
			currType = "Information";
		}
		/*-------------FIND PATTERNS-----------------*/
		if(prevType.equals("Directive") && (currType.equals("Information") || currType.equals("reply")))
		{
			pattern = "P1";
		}
		else if(prevType.equals("Question") && (currType.equals("Information") || currType.equals("reply")))
		{
			pattern = "P2";
		}
		else if(prevType.equals("Information") && currType.equals("reply"))
		{
			pattern = "P3";
		}
		else
		{
			pattern = "N";
		}
		
		return pattern;	
	}
	
	public static String identifyPatternFromEmail(Tree parse, ArrayList<TaggedWord> taggedSentence, ArrayList<String> directives)
	{
		String pattern = new String();
		
		//Check if the prevTagged Sentence is a directive
		ArrayList<ArrayList<TaggedWord>> list=new ArrayList<ArrayList<TaggedWord>>();
		ArrayList<ArrayList<TaggedWord>> condition=new ArrayList<ArrayList<TaggedWord>>();
		findSentenceWithSubjects(parse, list,"",condition);
		
		String type = new String();
		
		/*ArrayList<ArrayList<TaggedWord>> list1=new ArrayList<ArrayList<TaggedWord>>();
		ArrayList<ArrayList<TaggedWord>> condition1=new ArrayList<ArrayList<TaggedWord>>();
		findSentenceWithSubjects(parse2, list1,"",condition1);*/
		
		int directiveFlag=0;
		if(list.size()>0)
		{
			for(int j=0; j<list.size();j++)
			{
				ArrayList<TaggedWord> taggedYield = list.get(j);
				if(isADirective1(taggedYield,directives))
				{
					type = "D";
					//System.out.println("Directive");
					directiveFlag=1;
				}
			}
		}
		
		/*-------------SENDER INFO-------------*/
		/*else if(isADirective(prevTaggedSentences))
		{
			prevType = "Directive";
		}*/
		if(directiveFlag==0)
		{
			if(isADirective(taggedSentence))
			{
				type = "D";
			}
			else if(isAQuestion(taggedSentence))
			{
				type = "Q";
			}
			else if(isAReply(taggedSentence))
			{
				type = "I";
			}
			else
			{
				type = "N";
			}
		}
		return type;	
	}
	
	
	public static void findSentenceWithSubjects(Tree tree, ArrayList<ArrayList<TaggedWord>> list, String parent, ArrayList<ArrayList<TaggedWord>> condition)
	{
		//Following sentences are considered valid
		//If the parse tree directly starts with VP
			if(tree.value().equals("ROOT"))
			{
				int numChild = tree.getChildrenAsList().size();
				if(numChild > 0)
				{
					patternVPExtract(tree, list, 0);
				}
				for(int i=0; i<numChild;i++)
				{
					if(tree.getChild(i).value().equals("CC"))
					{
						patternVPExtract(tree, list, i+1);
					}
					
				}
			}
			else if(tree.value().equals("S"))
			{
				//if a sentence starts with Please
				int numChild = tree.getChildrenAsList().size();
				if(numChild >0)
				{
					
					for(int i=0; i<numChild;i++)
					{
						if(tree.getChild(i).value().equals("INTJ"))
						{
							int numNPChild = tree.getChild(i).getChildrenAsList().size();
							if(numNPChild > 0)
							{
								for(int j=0;j<numNPChild;j++) 
								{
									if(tree.getChild(i).getChild(j).value().equals("VB"))
									{
										int numPRPChild = tree.getChild(i).getChild(j).getChildrenAsList().size();
										if(numPRPChild > 0)
										{
											for(int k=0;k<numPRPChild;k++)
											{
												if(tree.getChild(i).getChild(j).getChild(k).value().toLowerCase().equals("please"))
												{
													ArrayList<TaggedWord> temp=new ArrayList<TaggedWord>();
													ArrayList<TaggedWord> temp1=new ArrayList<TaggedWord>();
													temp=tree.getChild(i).getChild(j).taggedYield();
													//System.out.pritln("SBAR"+tree.getChild(i).getChild(j).taggedYield());
													if(i+1<numChild)
													{
														if(tree.getChild(i+1).value().equals("VP"))
														{
															temp1=tree.getChild(i+1).taggedYield();
															//System.out.println("SBAR"+tree.getChild(i).getChild(j+1).taggedYield());
															for(int p=0;p<temp1.size();p++)
															{
																temp.add(temp1.get(p));
															}
															System.out.println(temp);
															list.add(temp);
														}
													}
												}
											}
										}
									}
								}
							}
						}
						//Check for conditions
						else if(tree.getChild(i).value().equals("SBAR"))
						{
							int numNPChild = tree.getChild(i).getChildrenAsList().size();
							if(numNPChild > 0)
							{
								for(int j=0;j<numNPChild;j++)
								{
									if(tree.getChild(i).getChild(j).value().equals("IN"))
									{
										int numPRPChild = tree.getChild(i).getChild(j).getChildrenAsList().size();
										if(numPRPChild > 0)
										{
											for(int k=0;k<numPRPChild;k++)
											{
												if(tree.getChild(i).getChild(j).getChild(k).value().toLowerCase().equals("if"))
												{
													condition.add(tree.getChild(i).taggedYield());
													System.out.println(tree.getChild(i).taggedYield());
												}
											}
										}
									}
								}
							}
							//add VP after a conditional statement
							if(i+1 < numChild)
							{
								patternVPExtract(tree, list, i+1);
							}
						}
						//If a sentence starts with you
						else if(tree.getChild(i).value().equals("NP"))
						{
							int numNPChild = tree.getChild(i).getChildrenAsList().size();
							if(numNPChild > 0)
							{
								for(int j=0;j<numNPChild;j++)
								{
									if(tree.getChild(i).getChild(j).value().equals("PRP"))
									{
										int numPRPChild = tree.getChild(i).getChild(j).getChildrenAsList().size();
										if(numPRPChild > 0)
										{
											for(int k=0;k<numPRPChild;k++)
											{
												if(tree.getChild(i).getChild(j).getChild(k).value().toLowerCase().equals("you"))
												{
													
													ArrayList<TaggedWord> temp=new ArrayList<TaggedWord>();
													ArrayList<TaggedWord> temp1=new ArrayList<TaggedWord>();
													int modalFlag=0;
													if(i>0)
													{
														if(tree.getChild(i-1).value().equals("MD"))
														{
															modalFlag=1;
														}
													}
													if(modalFlag !=1)
													{
													temp=tree.getChild(i).getChild(j).taggedYield();
													}
													//System.out.pritln("SBAR"+tree.getChild(i).getChild(j).taggedYield());
													if(i+1<numChild)
													{
														if(tree.getChild(i+1).value().equals("VP"))
														{
															temp1=tree.getChild(i+1).taggedYield();
															//System.out.println("SBAR"+tree.getChild(i).getChild(j+1).taggedYield());
															for(int p=0;p<temp1.size();p++)
															{
																temp.add(temp1.get(p));
																
															}
															System.out.println(temp);
															list.add(temp);
														}
													}
												}
											}
										}
									}
								}
							}
						}
						else if(tree.getChild(i).value().equals(","))
						{
							patternVPExtract(tree, list, i+1);
						}
					}//end of for loop
					//If a sentence starts immediately with a VP
					patternVPExtract(tree, list, 0);
				}
			}
			//Handle conditional statement  especially when a sentence begins with a conditional statement. Also check if conditional
			//statement contains any directive task
			else if(tree.value().equals("SBAR"))
			{
				int numChild = tree.getChildrenAsList().size();
				int ifFlag=0;
				int whFlag=0;
				if(tree.getChild(0).value().equals("S"))
				{
					return;
				}
				if(numChild > 0)
				{
					for(int j=0;j<numChild;j++)
					{
						if(tree.getChild(j).value().equals("IN"))
						{
							int numPRPChild = tree.getChild(j).getChildrenAsList().size();
							if(numPRPChild > 0)
							{
								for(int k=0;k<numPRPChild;k++)
								{
									if(tree.getChild(j).getChild(k).value().toLowerCase().equals("if") ||tree.getChild(j).getChild(k).value().toLowerCase().equals("that"))
									{
										condition.add(tree.taggedYield());
										System.out.println("cond"+tree.taggedYield());
										ifFlag=1;
									}
									/*else if(tree.getChild(j).getChild(k).value().toLowerCase().equals("that"))
									{
										list.add(tree.taggedYield());
										System.out.println(tree.taggedYield());
										
									}*/
								}
							}
						}
						//extract verb after conjunction
						if(tree.getChild(j).value().equals("S"))
						{
							int q=j;
							while(q-1 >= 0)
							{
								if(tree.getChild(q-1).value().equals("WHNP") || tree.getChild(q-1).value().equals("WHADVP"))
								{
									if(tree.getChild(q-1).getChild(0).value().equals("WP") || 
									   tree.getChild(q-1).getChild(0).value().equals("WDT") ||
									   tree.getChild(q-1).getChild(0).value().equals("WRB"))
									{
										whFlag=1;
									}
								}
								q--;
							}
							int numSChild = tree.getChild(j).getChildrenAsList().size();
							for(int k=0;k<numSChild;k++)
							{
								if(tree.getChild(j).getChild(k).value().equals(","))
								{
									if(k+1 < numSChild)
									{
										if(tree.getChild(j).getChild(k).value().equals("VP"))
										{
											patternVPExtract(tree.getChild(j).getChild(k), list, 0);
										}
									}
								}
							}
							
						}
					}
					if(ifFlag == 1 || whFlag==1)
					{
						return;
					}
					
				}
				//add VP after a conditional statement
			}
			else if(tree.value().equals("SQ") || tree.value().equals("SINV") || tree.value().equals("S") || tree.value().equals("PRN"))
			{
				int numChild = tree.getChildrenAsList().size();
				for(int j=0;j<numChild;j++)
				{
					if(tree.getChild(j).value().equals("MD"))
					{
						if(j+1 < numChild)
						{
							if(tree.getChild(j+1).value().equals("NP"))
							{
								if(tree.getChild(j+1).getChild(0).value().equals("PRP"))
								{
									if(tree.getChild(j+1).getChild(0).getChild(0).value().toLowerCase().equals("you"))
									{
										if(j+2 <numChild)
										{
											if(tree.getChild(j+2).value().equals("VP"))
											{
												list.add(tree.getChild(j+2).taggedYield());
												System.out.println(tree.getChild(j+2).taggedYield());
												int numVPChild=tree.getChild(j+2).getChildrenAsList().size();
												//check the children of VP. If S comes then return or else it will create repetitions
												for(int k=0; k<numVPChild; k++)
												{
													if(tree.getChild(j+2).getChild(k).value().equals("S"))
													{
														return;
													}
												}
											}
											
										}
									}
								}
							}
						}
				}
				else if(tree.getChild(j).value().equals("SINV"))
				{
						int numChild1 = tree.getChild(j).getChildrenAsList().size();
						for(int k=0;k<numChild1;k++)
						{
							if(tree.getChild(j).getChild(k).value().equals("VP"))
							{
								patternVPExtract(tree.getChild(j), list, k);
							}
						}
				}
					//if a verb phrase starts after SQ
				else if(tree.getChild(j).value().equals("VP"))
				{
					patternVPExtract(tree, list, j);
				}
			}
		}
		else if(tree.value().equals("FRAG"))
		{
			patternVPExtract(tree, list, 0);
		}
		/*else if(tree.value().equals("SINV"))
		{
				int numChild = tree.getChildrenAsList().size();
				for(int j=0;j<numChild;j++)
				{
					if(tree.getChild(j).value().equals("VP"))
					{
						patternVPExtract(tree, list, j);
					}
				}
		}*/	
		/*else if(tree.value().equals("VP"))
		{
				int numChild = tree.getChildrenAsList().size();
				for(int j=0;j<numChild;j++)
				{
					if(tree.getChild(j).value().equals(anObject))
					patternVPExtract(tree, list, j);
				}
		}*/
			//parent = tree.value();
		List<Tree> obj=tree.getChildrenAsList();
		if(obj.size()>0)
		{
			for(int a=0; a<obj.size();a++)
			{
				findSentenceWithSubjects(obj.get(a), list, parent, condition);
			}
		}
		return;
	}
	
	public static void patternVPExtract(Tree tree, ArrayList<ArrayList<TaggedWord>> list, int posn)
	{
		try
		{
		if(tree.getChild(posn).value().equals("VP"))
		{
			int numChild = tree.getChild(posn).getChildrenAsList().size();
			for(int i=0; i<numChild; i++)
			{
				if(tree.getChild(posn).getChild(i).value().equals("VB") || tree.getChild(posn).getChild(i).value().equals("VBP"))
				{
					list.add(tree.getChild(posn).taggedYield());
					System.out.println(tree.getChild(posn).taggedYield());
				}
				else if(tree.getChild(posn).getChild(i).value().equals("VP"))
				{
					if(i-1>=0)
					{
						if(tree.getChild(posn).getChild(i-1).value().equals("TO") || tree.getChild(posn).getChild(i-1).value().equals("MD"))
						{
							return;
						}
					}
					patternVPExtract(tree.getChild(posn), list, i);
				}
			}
				int size=tree.getChildrenAsList().size();
				if(posn+1 < size)
				{
					if(tree.getChild(posn+1).value().equals("CC") || tree.getChild(posn+1).value().equals("CC"))
					{
						if(posn+2 < size)
						{
							patternVPExtract(tree, list, posn+2);
						}
					}
				}
			
		}
		}
		catch (Exception e)
		{
			System.out.println("");
		}
		
	}
	
	public static boolean isADirective1(ArrayList<TaggedWord> taggedYield, ArrayList<String> directives)
	{
		
		//ArrayList<TaggedWord> taggedYield= taggedSentenceArrays.get(key);
		 int size = taggedYield.size();
		 String word=new String();
		 int checkFlag=0;
		 for(int j=0; j<size; j++)
		 {
			 if(checkFlag !=1)
			 {
			 //Rule 1: Please+VB
			 if(taggedYield.get(j).word().toLowerCase().equals("please") ||
					 taggedYield.get(j).word().toLowerCase().equals("kindly"))
			 {
				 checkFlag = 1;
				 if(j+1 < size)
				 {
					 if((taggedYield.get(j+1).tag().equals("VB") || taggedYield.get(j+1).tag().equals("VBP")) && 
							 (!taggedYield.get(j).word().toLowerCase().equals("want")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("need")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("be")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("like")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("have")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("continue")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("let")&&
									 !taggedYield.get(j+1).word().toLowerCase().equals("ok")))
					 {
						  if(j+2 < size)
						  {
							 if(taggedYield.get(j+2).word().toLowerCase().equals("not") || 
								     taggedYield.get(j+2).word().toLowerCase().equals("n't")||taggedYield.get(j+2).word().toLowerCase().equals("you"))
							 {
								 return false;
							 }
						 }
						  if(directives.contains(taggedYield.get(j+1).word().toLowerCase()))
						  {
							  return true;
						  }
					 }
					 //Rule 1a:please+be+sure+TO+VB 
					 if(taggedYield.get(j+1).word().toLowerCase().equals("be"))
					 {
						 if(j+2 < size)
						 {
							 	if(taggedYield.get(j+2).word().toLowerCase().equals("sure"))
							 	{
							 		if(j+3 < size)
							 		{
							 			if(taggedYield.get(j+3).tag().equals("TO"))
							 			{
							 				if(j+4 < size)
							 				{
							 					if(taggedYield.get(j+4).tag().equals("VB") || taggedYield.get(j+4).tag().equals("VBP"))
							 					{
							 						if(directives.contains(taggedYield.get(j+4).word().toLowerCase()))
													  {
														  return true;
													  }
							 					}
							 				}
							 			}
							 		}
							 	}
						 }
					 }
					 //Rule 1b: Please+let+PRP+VB
					 if(taggedYield.get(j+1).word().toLowerCase().equals("let"))
					 {
						 if(j+2 < size)
						 {
							 if(taggedYield.get(j+2).tag().equals("PRP"))
							 {
								 if(j+3 < size)
								 {
									 if(taggedYield.get(j+3).tag().equals("VB")||taggedYield.get(j+3).tag().equals("VBP"))
									 {
										 if(directives.contains(taggedYield.get(j+3).word().toLowerCase()))
										  {
											  return true;
										  }
									 }
								 }
							 }
						 }
					 }
				 }
			 }
			 //Rule 2: VB (exclude want, need, be , like, have, continue)
			 else if((taggedYield.get(j).tag().equals("VB") ||taggedYield.get(j).tag().equals("VBP"))&& 
					 (!taggedYield.get(j).word().toLowerCase().equals("want")&&
							 !taggedYield.get(j).word().toLowerCase().equals("need")&&
							 !taggedYield.get(j).word().toLowerCase().equals("be")&&
							 !taggedYield.get(j).word().toLowerCase().equals("like")&&
							 !taggedYield.get(j).word().toLowerCase().equals("have")&&
							 !taggedYield.get(j).word().toLowerCase().equals("continue")&&
							 !taggedYield.get(j).word().toLowerCase().equals("let") &&
							 !taggedYield.get(j).word().toLowerCase().equals("ok")))
			 	  {
				 	checkFlag = 1;
				  if(j+1 < size)
				  {
					 if(taggedYield.get(j+1).word().toLowerCase().equals("not") || 
						     taggedYield.get(j+1).word().toLowerCase().equals("n't") ||taggedYield.get(j+1).word().toLowerCase().equals("you"))
					 {
						 return false;
					 }
				 }
				  if(directives.contains(taggedYield.get(j).word().toLowerCase()))
				  {
					  return true;
				  }
			 }
			 //Rule 3: Be
			 else if(taggedYield.get(j).word().toLowerCase().equals("be"))
			 {
				 checkFlag = 1;
				 if(j+1 < size)
				 {
					 	//Rule 3a: Be+sure+TO+VB
					 	if(taggedYield.get(j+1).word().toLowerCase().equals("sure"))
					 	{
					 		if(j+2 < size)
					 		{
					 			if(taggedYield.get(j+2).tag().toLowerCase().equals("TO"))
					 			{
					 				if(j+3 < size)
					 				{
					 					if(taggedYield.get(j+3).tag().equals("VB")||taggedYield.get(j+3).tag().equals("VBP"))
					 					{
					 						if(directives.contains(taggedYield.get(j+3).word().toLowerCase()))
											  {
												  return true;
											  }
					 					}
					 				}
					 			}
					 		}
					 	}
				 }
			 }
			 //Rule 3: VB
			 else if(taggedYield.get(j).tag().equals("MD"))
			 {
				 checkFlag = 1;
				 if(j+1 < size)
				 {
					 if(taggedYield.get(j+1).word().toLowerCase().equals("you"))
					 {
						 if(j+2 < size)
						 {
							 //Rule 4a: MD+VB
							 if((taggedYield.get(j+2).tag().equals("VB")||taggedYield.get(j+2).tag().equals("VBP")) && 
								(!taggedYield.get(j+2).word().toLowerCase().equals("want")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("need")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("be")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("like")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("have")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("continue")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("let")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("ok")))
								 {
								 if(directives.contains(taggedYield.get(j+2).word().toLowerCase()))
								  {
									  return true;
								  }
							 	}
							 else if(taggedYield.get(j+2).word().toLowerCase().equals("please"))
							 {
								 if(j+3 < size)
								 {
									 if(taggedYield.get(j+3).tag().equals("VB"))
									 {
										 if(directives.contains(taggedYield.get(j+3).word().toLowerCase()))
										  {
											  return true;
										  }
									 }
							 	}
							 }
						 }
					 }
				 }
			 }
			 //Rule 4: You
			 else if(taggedYield.get(j).word().toLowerCase().equals("you"))
			 {
				 checkFlag = 1;
				 if(j+1 < size)
				 {
					 if(taggedYield.get(j+1).tag().equals("MD"))
					 {
						 if(j+2 < size)
						 {
							 //Rule 4a: MD+VB
							 if((taggedYield.get(j+2).tag().equals("VB")||taggedYield.get(j+2).tag().equals("VBP")) && 
								(!taggedYield.get(j+2).word().toLowerCase().equals("want")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("need")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("be")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("like")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("have")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("continue")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("let")&&
								 !taggedYield.get(j+2).word().toLowerCase().equals("ok")))
								 {
								 if(directives.contains(taggedYield.get(j+2).word().toLowerCase()))
								  {
									  return true;
								  }
							 	}
							 //Rule 4b: MD+want/need/have+TO+VB
							 	if(taggedYield.get(j+2).word().toLowerCase().equals("want") ||
									 taggedYield.get(j+2).word().toLowerCase().equals("need") ||
									 taggedYield.get(j+2).word().toLowerCase().equals("have")||
									 taggedYield.get(j+2).word().toLowerCase().equals("continue"))
							 	 {
							 			if(j+3 < size)
							 			{
							 				if(taggedYield.get(j+3).tag().equals("TO"))
							 				{
							 					if(j+4 < size)
							 					{
							 						 if(taggedYield.get(j+4).tag().equals("VB") || taggedYield.get(j+4).tag().equals("VBP"))
							 						 {
							 							if(directives.contains(taggedYield.get(j+4).word().toLowerCase()))
							 							  {
							 								  return true;
							 							  }
							 						}
							 					}
							 				}
							 			}
							 	 	}
							 //Rule 4c: MD+RB
							 	if(taggedYield.get(j+2).tag().equals("RB"))
							 	{
							 		if(j+3 < size)
						 			{
							 			//Rule 4ca: MD+RB+be+VB/VBP/VBG/VBD/VBN
						 				if(taggedYield.get(j+3).word().equals("be"))
						 				{
						 					if(j+4 < size)
						 					{
						 						if(taggedYield.get(j+4).tag().equals("VB")||
						 								taggedYield.get(j+4).tag().equals("VBP")||
						 								taggedYield.get(j+4).tag().equals("VBG")||
						 								taggedYield.get(j+4).tag().equals("VBD")||
						 								taggedYield.get(j+4).tag().equals("VBN"))
						 						{
						 							//word = WordnetDictionary.getBaseForm(taggedYield.get(j+4).word().toLowerCase());
													return true;
						 						}
						 						//Rule 4caa: MD+RB+be+able+TO+VB
						 						else if(taggedYield.get(j+4).word().toLowerCase().equals("able"))
						 						{
						 							if(j+5 < size)
						 							{
						 								if(taggedYield.get(j+5).tag().equals("TO"))
						 								{
						 									if(j+6 <size)
						 									{
						 										if(taggedYield.get(j+6).tag().equals("VB") || taggedYield.get(j+6).tag().equals("VBP"))
										 						 {
						 											if(directives.contains(taggedYield.get(j+6).word().toLowerCase()))
						 											  {
						 												  return true;
						 											  }
										 						}
						 									}
						 								}
						 							}
						 						}
						 					}
						 				}
						 			}
							 	}
							 //Rule 4d: MD+be
							 	if(taggedYield.get(j+2).word().toLowerCase().equals("be"))
					 				{
					 					if(j+3 < size)
					 					{
					 						if(taggedYield.get(j+3).tag().equals("VB")||
					 								taggedYield.get(j+3).tag().equals("VBP")||
					 								taggedYield.get(j+3).tag().equals("VBG")||
					 								taggedYield.get(j+3).tag().equals("VBD")||
					 								taggedYield.get(j+3).tag().equals("VBN"))
					 						{
					 							//word = WordnetDictionary.getBaseForm(taggedYield.get(j+3).word().toLowerCase());
												return true;
					 						}
					 						//Rule 4da: MD+be+able+TO+VB
					 						else if(taggedYield.get(j+3).word().toLowerCase().equals("able"))
					 						{
					 							if(j+4 < size)
					 							{
					 								if(taggedYield.get(j+4).tag().equals("TO"))
					 								{
					 									if(j+5 <size)
					 									{
					 										if(taggedYield.get(j+5).tag().equals("VB") || taggedYield.get(j+5).tag().equals("VBP"))
									 						 {
					 											if(directives.contains(taggedYield.get(j+5).word().toLowerCase()))
					 											  {
					 												  return true;
					 											  }
									 						}
					 									}
					 								}
					 							}
					 						}
					 						//Rule 4db: MD+be+responsible
					 						else if(taggedYield.get(j+3).word().equals("responsible"))
					 						{
					 							return true;
					 						}
					 					}
					 				}
						 		}
					 		}
				 		}
			 		}//Rule 5: Need+you+TO+VB
			 		else if(taggedYield.get(j).word().toLowerCase().equals("need"))
			 		{
			 			checkFlag = 1;
			 			if(j+1 <size)
			 			{
			 				if(taggedYield.get(j+1).word().toLowerCase().equals("you"))
			 				{
			 					if(j+2 <size)
			 					{
			 						if(taggedYield.get(j+2).tag().equals("TO"))
			 						{
			 							if(j+3 < size)
			 							{
			 								if(taggedYield.get(j+3).tag().equals("VB") || taggedYield.get(j+3).tag().equals("VBP"))
			 								{
				 								return true;
			 								}
			 								if(taggedYield.get(j+3).word().toLowerCase().equals("please"))
			 								{
			 									if(j+4 <size)
			 									{
			 										if(taggedYield.get(j+4).tag().equals("VB") || taggedYield.get(j+4).tag().equals("VBP"))
					 								{
			 											if(directives.contains(taggedYield.get(j+4).word().toLowerCase()))
			 											  {
			 												  return true;
			 											  }
					 								}
			 									}
			 								}
			 							}
			 						}
			 					}
			 				}
			 			}
			 		}
			 		else if(taggedYield.get(j).word().toLowerCase().equals("let"))
					 {
			 			checkFlag = 1;
						 if(j+1 < size)
						 {
							 if(taggedYield.get(j+1).tag().equals("PRP"))
							 {
								 if(j+2 < size)
								 {
									 if(taggedYield.get(j+2).tag().equals("VB")||taggedYield.get(j+2).tag().equals("VBP"))
									 {
										 if(directives.contains(taggedYield.get(j+2).word().toLowerCase()))
										  {
											  return true;
										  }
									 }
								 }
							 }
						 }
					 }
			 	}
		 	}
		 		
		 		
		return false;
	}
	
	public static boolean isADirective(ArrayList<TaggedWord> taggedSentences)
	{
		//starts with a verb
		/*if(taggedSentences.size()>1)
		{
			if((
					taggedSentences.get(0).tag().equals("VB") || 
					taggedSentences.get(0).tag().equals("VBP")
					
			   ) 
			   && 
			   (
					!taggedSentences.get(0).word().toLowerCase().equals("have") ||
					!taggedSentences.get(0).word().toLowerCase().equals("ok") ||
					!taggedSentences.get(0).word().toLowerCase().equals("yea") ||
					!taggedSentences.get(0).word().toLowerCase().equals("yeah") ||
					!taggedSentences.get(1).word().toLowerCase().equals("n't") ||
					!taggedSentences.get(1).word().toLowerCase().equals("not") ||
					!taggedSentences.get(1).word().toLowerCase().equals("are"))
			   )
			{
				return true;
			}
		}*/
		//starts with a noun and a verb
		/*if(taggedSentences.size()>2)
		{
			if((
					taggedSentences.get(0).tag().equals("NNP") ||
					taggedSentences.get(0).tag().equals("RB") ||
					taggedSentences.get(0).tag().equals("NN")
				)
				&& 
				(
					taggedSentences.get(1).tag().equals("VB") || 
					taggedSentences.get(1).tag().equals("VBP")
				) 
				&&
				(
					!taggedSentences.get(1).word().toLowerCase().equals("have") ||
					!taggedSentences.get(2).word().toLowerCase().equals("n't") ||
					!taggedSentences.get(2).word().toLowerCase().equals("not") ||
					!taggedSentences.get(2).word().toLowerCase().equals("are")
				))
			{
				return true;
			}
		}*/
		
		
		// starts with please and a verb
		/*if(taggedSentences.size()>2)
		{
			if(
					taggedSentences.get(0).word().toLowerCase().equals("please")
					&&
				   (
						  taggedSentences.get(1).tag().equals("VB") || 
						  taggedSentences.get(1).tag().equals("VBP")
					)
					&&
					(
							!taggedSentences.get(1).word().toLowerCase().equals("have") ||
							!taggedSentences.get(2).word().toLowerCase().equals("n't") ||
							!taggedSentences.get(2).word().toLowerCase().equals("not") ||
							!taggedSentences.get(2).word().toLowerCase().equals("are")
					)
			)
			{
				return true;
			}
		}*/
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(taggedSentences.get(i).word().toLowerCase().equals("please"))
			{
				if(i+2 < taggedSentences.size())
				{
				if(
						(taggedSentences.get(i+1).tag().equals("VB")|| 
						taggedSentences.get(i+1).tag().equals("VBP")
						)
						&&
						(	
							!taggedSentences.get(i+2).word().toLowerCase().equals("have") ||
							!taggedSentences.get(i+2).word().toLowerCase().equals("n't") ||
							!taggedSentences.get(i+2).word().toLowerCase().equals("not") ||
							!taggedSentences.get(i+2).word().toLowerCase().equals("are")
						)
				  )
				{
					return true;
				}
				}
			}
		}
		// Let us know or let me know
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+2 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("let"))
				{
					if(taggedSentences.get(i+1).word().toLowerCase().equals("us") ||
							taggedSentences.get(i+1).word().toLowerCase().equals("me"))
					{
					
						if(
								taggedSentences.get(i+2).tag().equals("VB")|| 
								taggedSentences.get(i+2).tag().equals("VBP")
						  )
						{
							return true;
						}
					}
				}
			}
		}
		
		//more complex
		//starts with a noun and a verb
		/*if(taggedSentences.size()>2)
		{
			if(
				(
					taggedSentences.get(0).word().toLowerCase().equals("ack") ||
					taggedSentences.get(0).word().toLowerCase().equals("roger") ||
					taggedSentences.get(0).word().toLowerCase().equals("rgr") ||
					taggedSentences.get(0).word().toLowerCase().equals("yup") ||
					taggedSentences.get(0).word().toLowerCase().equals("yes") ||
					taggedSentences.get(0).word().toLowerCase().equals("ok") ||
					taggedSentences.get(0).word().toLowerCase().equals("yep")
				)	
				&& 
				(
					taggedSentences.get(1).tag().equals("VB") || 
					taggedSentences.get(1).tag().equals("VBP")
				)
				&&
				(
					!taggedSentences.get(1).word().toLowerCase().equals("have") ||
					!taggedSentences.get(2).word().toLowerCase().equals("n't") ||
					!taggedSentences.get(2).word().toLowerCase().equals("not") ||
					!taggedSentences.get(2).word().toLowerCase().equals("are")
				)
			 )
			{
				return true;
			}
		}*/
		
		/*if(taggedSentences.size()>3)
		{
			if(
				(
					taggedSentences.get(0).word().toLowerCase().equals("ack")||
					taggedSentences.get(0).word().toLowerCase().equals("roger")||
					taggedSentences.get(0).word().toLowerCase().equals("rgr") ||
					taggedSentences.get(0).word().toLowerCase().equals("yup") ||
					taggedSentences.get(0).word().toLowerCase().equals("yes") ||
					taggedSentences.get(0).word().toLowerCase().equals("ok") ||
					taggedSentences.get(0).word().toLowerCase().equals("yep") ||
					taggedSentences.get(0).word().toLowerCase().equals("k")
				) 
				&&
				(
					taggedSentences.get(1).tag().equals("NNP") ||
					taggedSentences.get(1).tag().equals("RB") ||
					taggedSentences.get(1).tag().equals("NN")
				)
				&& 
				(
					taggedSentences.get(2).tag().equals("VB") || 
					taggedSentences.get(2).tag().equals("VBP")
				)
				&&
				(
					!taggedSentences.get(1).word().toLowerCase().equals("have") ||
					!taggedSentences.get(2).word().toLowerCase().equals("n't") ||
					!taggedSentences.get(2).word().toLowerCase().equals("not") ||
					!taggedSentences.get(2).word().toLowerCase().equals("are")
				)
			)
			{
				return true;
			}
		}*/
		/*if(taggedSentences.size() > 3)
		{
			if((
					taggedSentences.get(0).word().toLowerCase().equals("ack")||
					taggedSentences.get(0).word().toLowerCase().equals("roger")||
					taggedSentences.get(0).tag().toLowerCase().equals("rgr") ||
					taggedSentences.get(0).word().toLowerCase().equals("yup") ||
					taggedSentences.get(0).word().toLowerCase().equals("yes") ||
					taggedSentences.get(0).word().toLowerCase().equals("yep") ||
					taggedSentences.get(0).word().toLowerCase().equals("ok") ||
					taggedSentences.get(0).word().toLowerCase().equals("k")
				) 
				&&
			   (
					taggedSentences.get(1).word().toLowerCase().equals("please")
			   )
			   &&
			   (
				    taggedSentences.get(2).tag().equals("VB") || 
					taggedSentences.get(2).tag().equals("VBP")
				)
				&&
				(
						!taggedSentences.get(2).word().toLowerCase().equals("have") ||
						!taggedSentences.get(3).word().toLowerCase().equals("n't") ||
						!taggedSentences.get(3).word().toLowerCase().equals("not") ||
						!taggedSentences.get(3).word().toLowerCase().equals("are")
				)
				)
			  {
					return true;
			  }
		}*/
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+3 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("roger")||
						taggedSentences.get(i).word().toLowerCase().equals("rgr") ||
						taggedSentences.get(i).word().toLowerCase().equals("yup") ||
						taggedSentences.get(i).word().toLowerCase().equals("yes"))
				{		
					if(taggedSentences.get(i+1).word().toLowerCase().equals("let"))
					{
						if(taggedSentences.get(i+1).word().toLowerCase().equals("us") ||
								taggedSentences.get(i+1).word().toLowerCase().equals("me"))
						{
							if(taggedSentences.get(i+3).tag().equals("VB")|| 
							   taggedSentences.get(i+3).tag().equals("VBP"))
							{
								return true;
							}
						}
					}
				}
			}
		}
		//keep me posted
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+2 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("keep") &&
				   taggedSentences.get(i+1).word().toLowerCase().equals("me") &&
				   taggedSentences.get(i+2).word().toLowerCase().equals("posted"))
				   {
						return true;
				   }
			}
		}
		//I need
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+2 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("i") &&
				   taggedSentences.get(i+1).word().toLowerCase().equals("need"))
				   {
						return true;
				   }
			}
		}
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+1 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("need"))
				   {
					if(taggedSentences.get(i+1).word().toLowerCase().equals("you"))
					{
						return true;
					}
				   }
			}
		}
		/*for(int i=0; i<taggedSentences.size();i++)
		{
			if(taggedSentences.get(i).tag().equals("VB"))
			{
				return true;
			}
		}*/
		return false;
	}
	
	public static boolean isAQuestion(ArrayList<TaggedWord> taggedSentences)
	{
		
		if(taggedSentences.size() > 1)
		{
			if((
					taggedSentences.get(0).word().toLowerCase().equals("what")||
					taggedSentences.get(0).word().toLowerCase().equals("when")||	
					taggedSentences.get(0).word().toLowerCase().equals("why") ||
					taggedSentences.get(0).word().toLowerCase().equals("how")
					
				)
				&&
				(
						!taggedSentences.get(1).word().toLowerCase().equals("you")
				)
			   
			   )
			{
				return true;
			}
		}
		if(taggedSentences.size() > 2)
		{
			if((
					taggedSentences.get(0).tag().equals("NNP") ||
					taggedSentences.get(0).tag().equals("NN") ||
					taggedSentences.get(0).word().toLowerCase().equals("ok") ||
					   taggedSentences.get(0).word().toLowerCase().equals("ack") ||
					   taggedSentences.get(0).word().toLowerCase().equals("rgr") ||
					   taggedSentences.get(0).word().toLowerCase().equals("roger")||
					   taggedSentences.get(0).word().toLowerCase().equals("yes") ||
					   taggedSentences.get(0).word().toLowerCase().equals("yup") ||
					   taggedSentences.get(0).word().toLowerCase().equals("yep") ||
					   taggedSentences.get(0).word().toLowerCase().equals("k")
					
				)
				&& 
			   (
					taggedSentences.get(1).word().toLowerCase().equals("what")||
					taggedSentences.get(1).word().toLowerCase().equals("whats") ||
					taggedSentences.get(1).word().toLowerCase().equals("when")||	
					taggedSentences.get(1).word().toLowerCase().equals("why") ||
					taggedSentences.get(1).word().toLowerCase().equals("how")
				)
				&&
				(
						!taggedSentences.get(2).word().toLowerCase().equals("you")
				)
				)
			{
				return true;
			}
		}
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(taggedSentences.size()>1)
			{
			if(taggedSentences.get(i).word().equals("?"))
			{
				return true;
			}
			}
		}
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+2 < taggedSentences.size())
			{
				if((taggedSentences.get(i).word().toLowerCase().equals("do") ||
						taggedSentences.get(i).word().toLowerCase().equals("can") ||
						taggedSentences.get(i).word().toLowerCase().equals("will") ||
						taggedSentences.get(i).word().toLowerCase().equals("could") ||
						taggedSentences.get(i).word().toLowerCase().equals("will") ||
						taggedSentences.get(i).word().toLowerCase().equals("shall") ||
						taggedSentences.get(i).word().toLowerCase().equals("should")
					)	&&
				   taggedSentences.get(i+1).word().toLowerCase().equals("you"))
				   {
						return true;
				   }
			}
		}
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+1 < taggedSentences.size())
			{
				if(taggedSentences.get(i).word().toLowerCase().equals("whats"))
				{
					if(taggedSentences.get(i+1).word().toLowerCase().equals("your"))
					{
						return true;
					}
				}
				if(taggedSentences.get(i).word().toLowerCase().equals("has"))
				{
					if(taggedSentences.get(i+1).word().toLowerCase().equals("anyone"))
					{
						return true;
					}
				}
				if(taggedSentences.get(i).word().toLowerCase().equals("what"))
				{
					if(taggedSentences.get(i+1).word().toLowerCase().equals("is")||taggedSentences.get(i+1).word().toLowerCase().equals("are")||taggedSentences.get(i+1).word().toLowerCase().equals("were"))
					{
						return true;
					}
				}
				if(taggedSentences.get(i).word().toLowerCase().equals("did")||taggedSentences.get(i).word().toLowerCase().equals("do"))
				{
					if(taggedSentences.get(i+1).word().toLowerCase().equals("you"))
					{
						return true;
					}
				}
				
			}
		}
		
		return false;
	}
	public static boolean isAReply(ArrayList<TaggedWord> taggedSentences)
	{
		//got it
		for(int i=0; i<taggedSentences.size();i++)
		{
			if(i+1 < taggedSentences.size())
			{
			if (
			   taggedSentences.get(i).word().toLowerCase().equals("ok") ||
			   taggedSentences.get(i).word().toLowerCase().equals("okay") ||
			   taggedSentences.get(i).word().toLowerCase().equals("ack") ||
			   taggedSentences.get(i).word().toLowerCase().equals("rgr") ||
			   taggedSentences.get(i).word().toLowerCase().equals("roger")||
			   taggedSentences.get(i).word().toLowerCase().equals("rogere")||
			   taggedSentences.get(i).word().toLowerCase().equals("rgoer")||
			   taggedSentences.get(i).word().toLowerCase().equals("roer")||
			   taggedSentences.get(i).word().toLowerCase().equals("yes") ||
			   taggedSentences.get(i).word().toLowerCase().equals("yup") ||
			   taggedSentences.get(i).word().toLowerCase().equals("yep") ||
			   taggedSentences.get(i).word().toLowerCase().equals("yeah") ||
			   taggedSentences.get(i).word().toLowerCase().equals("k") ||
			   taggedSentences.get(i).word().toLowerCase().equals("thanks")||
			   taggedSentences.get(i).word().toLowerCase().equals("thank")||
			   taggedSentences.get(i).word().toLowerCase().equals("thx")||
			   taggedSentences.get(i).word().toLowerCase().equals("tk")||
			   taggedSentences.get(i).word().toLowerCase().equals("tks")||
			   taggedSentences.get(i).word().toLowerCase().equals("ty")||
			   taggedSentences.get(i).word().toLowerCase().equals("great")||
			   taggedSentences.get(i).word().toLowerCase().equals("nice")||
			   taggedSentences.get(i).word().toLowerCase().equals("affirm")||
			   taggedSentences.get(i).word().toLowerCase().equals("copy")||
			   taggedSentences.get(i).word().toLowerCase().equals("c")||
			   taggedSentences.get(i).word().toLowerCase().equals("cpy")||
			   taggedSentences.get(i).word().toLowerCase().equals("sorry")||
			   taggedSentences.get(i).word().toLowerCase().equals("fyi")||
			   taggedSentences.get(i).word().toLowerCase().equals("sure")||
			   taggedSentences.get(i).word().toLowerCase().equals("cool")||
			   (taggedSentences.get(i).word().toLowerCase().equals("got") &&  taggedSentences.get(i+1).word().toLowerCase().equals("it")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("thank") &&  taggedSentences.get(i+1).word().toLowerCase().equals("you")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("nice") &&  taggedSentences.get(i+1).word().toLowerCase().equals("job")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("good") &&  taggedSentences.get(i+1).word().toLowerCase().equals("job")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("looks") &&  taggedSentences.get(i+1).word().toLowerCase().equals("cool")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("no") &&  taggedSentences.get(i+1).word().toLowerCase().equals("problem")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("good") &&  taggedSentences.get(i+1).word().toLowerCase().equals("news")) ||
			   (taggedSentences.get(i).word().toLowerCase().equals("got") &&  taggedSentences.get(i+1).word().toLowerCase().equals("em"))
			   
			  ) 
			   
			   
			{
				return true;
			}
			}
		}
		return false;
	}
	public static String preProcess(String sentence)
	{
		sentence = sentence.replace("(en)"," ");
		sentence = sentence.replace("\""," ");
		sentence = sentence.replace(":"," ");
		sentence = sentence.replace("..."," ");
		//sentence = sentence.replace("."," ");
		sentence = sentence.replace("'"," ");
		
		sentence = sentence.trim();
		return sentence;
	}
	
	public static void uploadVerbs(String filename, ArrayList<String> array)
	 {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String temp = new String();
		try {
	 
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) 
				{
	 			String[] cell = line.split(cvsSplitBy);
				//ArrayList<String> row=new ArrayList<String>();
				
				if(cell[0] != null)
				{
					array.add(cell[0]);
					//System.out.println(cell[0]);
				}
				else
				{
					array.add("");
				}
				
				}
		}
	 	 catch (FileNotFoundException e) {
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
	 }
	
	public static void numberOfUniqueParticipants(ArrayList<ArrayList<String>> arrayList, ArrayList<String> uniqueUsers, int posn)
	{
		ArrayList<String> row = new ArrayList<String>();
		Set<String> s = new HashSet<String>();
		String name = new String();
		for(int i=1; i<arrayList.size();i++)
		{
			row = arrayList.get(i);
			name = row.get(posn);
			s.add(filterName(name));
		}
		for (Iterator<String> it = s.iterator(); it.hasNext(); ) {
	        name = it.next();
	        uniqueUsers.add(name);
	    }
		//copy uniqueUsers to a new arraylist
		/*ArrayList<String> temp = new ArrayList<String>();
		for(int i=0; i<uniqueUsers.size();i++)
		{
			temp.add(uniqueUsers.get(i));
		}*/
		
		//remove duplicates using Levenshtein distance
		
	}
	
	
}
