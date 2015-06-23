package csc.ncsu.edu.main;

import csc.ncsu.edu.cta.utility.DatabaseConn;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import csc.ncsu.edu.config.ProjectConfig;
import csc.ncsu.edu.cta.utility.Utility;

public class EnronFeatureOutput {

	public static ArrayList<String> employeelist=new ArrayList<String>();
	public static ArrayList<Integer> employeeId=new ArrayList<Integer>();
	public static ArrayList<String> emailChains=new ArrayList<String>();
	
	
	
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
		
		for(int i=0; i<employeeId.size();i++)
		{
			if(employeeId.get(i)!=-1)
			{
						/*-------------------------------------------QUERY 1------------------------------------------------------------------------*/
						sql2="select recipientid, subject, messageid, messagedt  from enron.edgemap where senderid="+employeeId.get(i)+";";
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
									recipientId = Integer.parseInt(rs2.getString(1));
									subject = rs2.getString(2);
									subject = subject.replaceAll("'","''");
									messageId = Integer.parseInt(rs2.getString(3));
									messagedt = rs2.getString(4);
									
									//First Message
									emailChainString = emailChainString + employeeId.get(i) + ",";
									emailChainString = emailChainString + recipientId+",";
									emailChainString = emailChainString + messagedt+",";
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
									
									
									//Extract replies
									if(!subject.contains("Re: "))
									{
										subject = "RE: "+subject;
									}
				/*--------------------------------------------------QUERY 3------------------------------------------------------------------------*/			
									sql4="select messageid, messagedt from enron.edgemap where senderid="+recipientId+" AND subject='"+subject+"'   ;";
									//sql1="select senderid from enron.messages where subject='"+subject+"';";
									try 
									{
										if(DatabaseConn.conn!=null)
										{
											stmt4=DatabaseConn.conn.createStatement();
											rs4=stmt4.executeQuery(sql4);
											
											while(rs4.next())
											{
												emailChainString ="";
												messageBodies="";
												
												messageId = Integer.parseInt(rs4.getString(1));
												messagedt = rs4.getString(2);
												//System.out.print(rs1.getString(1));
												//replyMessage++;
												// Second Message (Reply)
												emailChainString = emailChainString + recipientId+",";
												emailChainString = emailChainString + employeeId.get(i) + ",";
												emailChainString = emailChainString + messagedt+",";
												emailChainString = emailChainString + subject+",";
												
					/*-----------------------------------------------------QUERY 4------------------------------------------------------------------------*/						
												sql5="select body from enron.bodies where messageid="+messageId+";";
												//sql1="select senderid from enron.messages where subject='"+subject+"';";
												try 
												{
													if(DatabaseConn.conn!=null)
													{
														stmt5=DatabaseConn.conn.createStatement();
														rs5=stmt5.executeQuery(sql5);
														while(rs5.next())
														{
															messageBodies = rs5.getString(1);
															//System.out.print(rs1.getString(1));
															//replyMessage++;
														}
													}
												}
												catch (SQLException | OutOfMemoryError e)
												{
													e.printStackTrace();
												}
												
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
						
						
						//Find Reply
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
		
		
	}
}
