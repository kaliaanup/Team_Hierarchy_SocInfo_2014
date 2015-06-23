package csc.ncsu.edu.main;


import csc.ncsu.edu.cta.utility.DatabaseConn;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import csc.ncsu.edu.config.ProjectConfig;
import csc.ncsu.edu.cta.utility.Utility;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class EnronFeatureOutput1{

	public static ArrayList<String> employeelist=new ArrayList<String>();
	public static ArrayList<Integer> employeeId=new ArrayList<Integer>();
	public static ArrayList<String> emailChains=new ArrayList<String>();
	public static ArrayList<TaggedWord> taggedSentence = new ArrayList<TaggedWord>();
	public static ArrayList<String> directives=new ArrayList<String>();
	public static Tree parse;
	
	public static ArrayList<ArrayList<String>> featuresD = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresQ = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresI = new ArrayList<ArrayList<String>>();
	public static ArrayList<ArrayList<String>> featuresDQ = new ArrayList<ArrayList<String>>();
	
	
	public static int responsePatternD=0;
	public static int responsePatternQ=0;
	public static int responsePatternI=0;
	
	
	public static void initialize()
	{
		
		responsePatternD=0;
		responsePatternQ=0;
		responsePatternI=0;
		
	
	}
	public static void resultantOutput(LexicalizedParser lp, String email, String subject, Integer ID)
	{
		
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US); 
		iterator.setText(email); int start = iterator.first();
		
		String temp = new String();
		for (int end = iterator.next();end != BreakIterator.DONE; start = end, end = iterator.next())
		{
			temp = email.substring(start,end);
			
			temp = temp.replace('"', '\"');
			temp = temp.replace("\"", "");
			temp=temp.trim();
			if(temp.length()>0)
			{
			/*-------------------PARSE EACH SENTENCE USING STANFORD PARSER------------------------*/
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			parse = lp.parse(temp);//for pennPrint and taggedYield
			
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			taggedSentence = parse.taggedYield();
			
			String response=new String("");
			response = Utility.identifyPatternFromEmail(parse, taggedSentence, directives);
			
			if(response.equals("D"))
			{
				responsePatternD = responsePatternD + 1;
				
			}
			else if(response.equals("Q"))
			{
				responsePatternQ = responsePatternQ + 1;
			}
			else if(response.equals("I"))
			{
				if(subject.contains("RE:") || subject.contains("Re:"))
				{
					responsePatternI = responsePatternI + 1;
				}
				
			}
			
			
			//List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
			}

		} 
	}
	
	public static void updateFeatureAttributes(int userID)
	{
		
		/*--------------------------D---------------------------------------*/
		ArrayList<String> userP1 = new ArrayList<String>();
		userP1.add(String.valueOf(userID));//store user ID
		userP1.add(String.valueOf(responsePatternD));//store total no of P1
		featuresD.add(userP1);
		/*--------------------------Q---------------------------------------*/
		ArrayList<String> userP2 = new ArrayList<String>();
		userP2.add(String.valueOf(userID));//store user ID
		userP2.add(String.valueOf(responsePatternQ));//store total no of P1
		featuresQ.add(userP2);
		/*--------------------------P3---------------------------------------*/
		ArrayList<String> userP3 = new ArrayList<String>();
		userP3.add(String.valueOf(userID));//store user ID
		userP3.add(String.valueOf(responsePatternI));//store total no of P1
		featuresI.add(userP3);
		/*--------------------------P1P2P3---------------------------------------*/
		ArrayList<String> userP1P2 = new ArrayList<String>();
		userP1P2.add(String.valueOf(userID));//store user ID
		userP1P2.add(String.valueOf(responsePatternD+responsePatternQ));//store total no of P1
		featuresDQ.add(userP1P2);
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
	public static void main(String args[])
	{
		String sql1="";
		String sql2="";
		String sql3="";
		String sql4="";
		String sql5="";
		
		ResultSet rs1=null;
		ResultSet rs2=null;
		ResultSet rs3=null;
		ResultSet rs4=null;
		ResultSet rs5=null;
		
		Statement stmt1=null;
		Statement stmt2=null;
		Statement stmt3=null;
		Statement stmt4=null;
		Statement stmt5=null;
		LexicalizedParser lp = LexicalizedParser.loadModel(ProjectConfig.grammer);
		
		String email = "";
		//upload the ground truths for enron emails
		Utility.uploadVerbs(ProjectConfig.enronemployeelist, employeelist);
		System.out.println(employeelist);
		DatabaseConn.openMySqlConnection();
		
		for(int i=0;i<employeelist.size();i++)
		{
		//Access people personid from the enron DB
			email = employeelist.get(i);
			sql1="SELECT personid FROM enron.people where email='"+email+"@enron.com';";
			try 
			{
				if(DatabaseConn.conn!=null)
				{
					stmt1=DatabaseConn.conn.createStatement();
					rs1=stmt1.executeQuery(sql1);
					
					if(rs1.next() == false)
					{
						employeeId.add(-1);
					}
					else
					{
						employeeId.add(Integer.parseInt(rs1.getString(1)));
					}
				}
			}
			catch (SQLException | OutOfMemoryError e)
			{
				e.printStackTrace();
			}
		}
		System.out.println(employeeId);
		System.out.println(employeeId.size());
		
		int countMessage=0;
		int replyMessage=0;
		String subject="";
		Integer recipientId=0;
		Integer messageId=0;
		String emailChainString="";
		String messagedt="";
		String messageBodies="";
		
		for(int i=139; i<140;i++)
		{
			if(employeeId.get(i)!=-1)
			{
						countMessage=0;
						initialize();
						System.out.println("------------");
						System.out.println(employeeId.get(i));
						/*-------------------------------------------QUERY 1------------------------------------------------------------------------*/
						sql2="select messageid, subject from enron.messages where senderid="+employeeId.get(i)+" order by messagedt;";
						try 
						{
							if(DatabaseConn.conn!=null)
							{
								stmt2=DatabaseConn.conn.createStatement();
								rs2=stmt2.executeQuery(sql2);
								
								while(rs2.next())
								{
									
									emailChainString ="";
									messageBodies ="";
									
									
									countMessage++;
									messageId = Integer.parseInt(rs2.getString(1));
									subject = rs2.getString(2);
									
									
									//First Message
									emailChainString = emailChainString + employeeId.get(i) + ",";
									emailChainString = emailChainString + messagedt+",";
									emailChainString = emailChainString + countMessage+",";
									emailChainString = emailChainString + subject+",";
									
									
									
						/*-------------------------------------------QUERY 2------------------------------------------------------------------------*/
									//extract the email bodies
									sql3="select body from enron.bodies where messageid="+messageId+";";
									//sql1="select senderid from enron.messages where subject='"+subject+"';";
									try 
									{
										if(DatabaseConn.conn!=null)
										{
											stmt3=DatabaseConn.conn.createStatement();
											rs3=stmt3.executeQuery(sql3);
											
											while(rs3.next())
											{
												messageBodies = rs3.getString(1); 
												
											}
										}
									}
									catch (SQLException | OutOfMemoryError e)
									{
										e.printStackTrace();
									}
									
									//extract recipient ID
									sql4="select recipientid from enron.recipients where messageid="+messageId+";";
									//sql1="select senderid from enron.messages where subject='"+subject+"';";
									try 
									{
										if(DatabaseConn.conn!=null)
										{
											stmt4=DatabaseConn.conn.createStatement();
											rs4=stmt4.executeQuery(sql4);
											
											while(rs4.next())
											{
												recipientId = Integer.parseInt(rs4.getString(1));
											}
										}
									}
									catch (SQLException | OutOfMemoryError e)
									{
										e.printStackTrace();
									}
									
									/*if(employeeId.contains(recipientId))
									{
										if(recipientId != employeeId.get(i))
										{
											System.out.println(employeeId.get(i)+"has"+recipientId);*/
											
											emailChainString = emailChainString + recipientId+",";
											
											messageBodies = messageBodies.replaceAll("[\r\n]+", "\n"); 
											messageBodies = messageBodies.replaceAll("\n","");
											messageBodies = messageBodies.replaceAll(",","");
											messageBodies=messageBodies.replaceAll("=20", "");
											messageBodies=messageBodies.replaceAll("20", "");
											messageBodies=messageBodies.replaceAll(">=20", "");
											messageBodies=messageBodies.replaceAll("=", "");
											messageBodies=messageBodies.replaceAll("-----Original Message-----", "");
											messageBodies=messageBodies.replaceAll("-","");
											messageBodies=messageBodies.replaceAll("=09","");
											messageBodies=messageBodies.replaceAll("09","");
											messageBodies=messageBodies.replaceAll("----- Forwarded by", "");
											
											
											emailChainString = emailChainString + messageBodies;
											
											emailChains.add(emailChainString);
											
											resultantOutput(lp, messageBodies, subject, employeeId.get(i));
										//}
									//}
							}
						}
							
							
							
					}
					catch (SQLException | OutOfMemoryError e)
					{
							e.printStackTrace();
					}
						
					//System.out.println(employeeId.get(i)+","+countMessage+",");	
						//Find Reply
						updateFeatureAttributes(employeeId.get(i));
			}
			else
			{
				ArrayList<String> userP1 = new ArrayList<String>();
				userP1.add(String.valueOf(-1));//store user ID
				userP1.add(String.valueOf(0));//store total no of P1
				featuresD.add(userP1);
				/*--------------------------Q---------------------------------------*/
				ArrayList<String> userP2 = new ArrayList<String>();
				userP2.add(String.valueOf(-1));//store user ID
				userP2.add(String.valueOf(0));//store total no of P1
				featuresQ.add(userP2);
				/*--------------------------P3---------------------------------------*/
				ArrayList<String> userP3 = new ArrayList<String>();
				userP3.add(String.valueOf(-1));//store user ID
				userP3.add(String.valueOf(0));//store total no of P1
				featuresI.add(userP3);
				/*--------------------------P1P2P3---------------------------------------*/
				ArrayList<String> userP1P2 = new ArrayList<String>();
				userP1P2.add(String.valueOf(-1));//store user ID
				userP1P2.add(String.valueOf(0));//store total no of P1
				featuresDQ.add(userP1P2);
//				/System.out.println(-1+","+0+",");
			}
		}
		 
		//output employeeId and size
		//System.out.println(countMessage);
		//System.out.println(replyMessage);
		//System.out.println(emailChains);
		
		try
		{
		    FileWriter writer = new FileWriter(ProjectConfig.employeeMessages);
		    for(int i=0; i<emailChains.size();i++)
		    {
		    	
		    		writer.append(emailChains.get(i));
		    		writer.append('\n');
		    }
		    writer.append('\n');
		    writer.flush();
		    writer.close();
			
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
		System.out.println(featuresD);
		System.out.println(featuresQ);
		System.out.println(featuresI);
		System.out.println(featuresDQ);
		
/*		generateCSVFileDouble(featuresD, ProjectConfig.featuresFileD);
		generateCSVFileDouble(featuresQ, ProjectConfig.featuresFileQ);
		generateCSVFileDouble(featuresI, ProjectConfig.featuresFileI);
		generateCSVFileDouble(featuresDQ, ProjectConfig.featuresFileDQ);
*/		
	}
}
