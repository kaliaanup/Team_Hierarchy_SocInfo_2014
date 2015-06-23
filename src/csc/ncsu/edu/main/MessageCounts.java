package csc.ncsu.edu.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import csc.ncsu.edu.config.ProjectConfig;
import csc.ncsu.edu.cta.utility.DatabaseConn;
import csc.ncsu.edu.cta.utility.Utility;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class MessageCounts {

	public static ArrayList<String> employeelist=new ArrayList<String>();
	public static ArrayList<Integer> employeeId=new ArrayList<Integer>();
	public static ArrayList<Integer> emailCounts=new ArrayList<Integer>();
	public static ArrayList<Integer> emailSentenceCounts=new ArrayList<Integer>();
	
	
public static int returnSentencesCount(String email)
{
	int i=0;
		
	BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US); 
	iterator.setText(email); int start = iterator.first();
		
	int len=0;
	for (int end = iterator.next();end != BreakIterator.DONE; start = end, end = iterator.next())
	{
		len++;
	}
	return len;
}
	public static void main(String args[])
	{
		String sql1="";
		String sql2="";
		String sql3="";
		
		ResultSet rs1=null;
		ResultSet rs2=null;
		ResultSet rs3=null;
		
		Statement stmt1=null;
		Statement stmt2=null;
		Statement stmt3=null;
		
		LexicalizedParser lp = LexicalizedParser.loadModel(ProjectConfig.grammer);
		
		Utility.uploadVerbs(ProjectConfig.enronemployeelist, employeelist);
		
		DatabaseConn.openMySqlConnection();
		
		String email = "";
		Integer messageId=0;
		
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
		
		
		
		
		int countMessage=0;
		int emailLength=0;
		for(int i=0; i<employeeId.size();i++)
		{
			
			countMessage=0;
			emailLength=0;
			if(employeeId.get(i)!=-1)
			{
						/*-------------------------------------------QUERY 1------------------------------------------------------------------------*/
						sql2="select messageid from enron.messages where senderid="+employeeId.get(i)+" order by messagedt;";
						try 
						{
							if(DatabaseConn.conn!=null)
							{
								stmt2=DatabaseConn.conn.createStatement();
								rs2=stmt2.executeQuery(sql2);
								
								while(rs2.next())
								{
									countMessage++;
									messageId = Integer.parseInt(rs2.getString(1));
									
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
												email = rs3.getString(1); 
												emailLength= emailLength + returnSentencesCount(email);
											}
										}
									}
									catch (SQLException | OutOfMemoryError e)
									{
										e.printStackTrace();
									}
									
								}
							}
						}
						catch (SQLException | OutOfMemoryError e)
						{
								e.printStackTrace();
						}
						
						emailCounts.add(countMessage);
						emailSentenceCounts.add(emailLength);
						
			}
			else{
				emailCounts.add(0);
				emailSentenceCounts.add(0);
			}
		 }
		
		for(int i=0; i<employeeId.size();i++)
		{
			System.out.println(employeeId.get(i)+","+emailCounts.get(i)+","+emailSentenceCounts.get(i)+",");
		}
		
		
	}
}
