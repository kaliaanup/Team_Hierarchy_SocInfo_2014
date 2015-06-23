package csc.ncsu.edu.main;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import csc.ncsu.edu.config.ProjectConfig;
import csc.ncsu.edu.cta.utility.DatabaseConn;
import csc.ncsu.edu.cta.utility.Utility;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class MessageNetwork {

	public static ArrayList<String> employeelist=new ArrayList<String>();
	public static ArrayList<Integer> employeeId=new ArrayList<Integer>();
	public static ArrayList<Integer> emailCounts=new ArrayList<Integer>();
	public static ArrayList<Integer> emailSentenceCounts=new ArrayList<Integer>();
	public static int[][] adjacentM;
	public static int[][] adjacentWM;
	public static int noOfEmpoyees;
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
		Integer recipientId=0;
		
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
		
		//initializing matrix
		noOfEmpoyees=employeeId.size();
		adjacentM = new int[noOfEmpoyees][noOfEmpoyees];
		adjacentWM = new int[noOfEmpoyees][noOfEmpoyees];
		
		for(int i=0; i<noOfEmpoyees;i++)
		{
			for(int j=0; j<noOfEmpoyees;j++)
			{
				adjacentM[i][j]=0;
				adjacentWM[i][j]=0;
			}
		}
		
		for(int i=0; i<employeeId.size();i++)
		{
			
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
									//countMessage++;
									messageId = Integer.parseInt(rs2.getString(1));
									
									sql3="select personid from enron.recipients where messageid="+messageId+";";
									//sql1="select senderid from enron.messages where subject='"+subject+"';";
									try 
									{
										if(DatabaseConn.conn!=null)
										{
											stmt3=DatabaseConn.conn.createStatement();
											rs3=stmt3.executeQuery(sql3);
											
											while(rs3.next())
											{
												recipientId = Integer.parseInt(rs3.getString(1)); 
												if(employeeId.contains(recipientId))
												{
												
													adjacentWM[i][employeeId.indexOf(recipientId)]=adjacentWM[i][employeeId.indexOf(recipientId)] + 1;
													adjacentM[i][employeeId.indexOf(recipientId)]=1;
												}
										//		emailLength= emailLength + returnSentencesCount(email);
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
						
						
						
			}
			else{
				
			}
	}
		
		
		generateCSVFileMatrix(adjacentM, ProjectConfig.adjacentFileEnron, employeeId.size());
		generateCSVFileMatrix(adjacentWM, ProjectConfig.adjacentFileWEnron, employeeId.size());
		
	}
}


