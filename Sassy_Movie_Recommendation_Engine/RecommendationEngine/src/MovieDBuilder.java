// Written by: Austin Green
// Date: 11/17/2018
// Course: Dutta Modeling

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.NoSuchElementException;


public class MovieDBuilder {
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/?useSSL=false&rewriteBatchedStatements=true";
	static final String USER = "root";
	static final String PASS = "root";
	static final String DB_NAME = "MovieDB";

	public static void buildDB() {
		
		System.out.println("Connecting to Server...");
		Connection conn = null;
		try {
			// Initialize Connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS); 
			System.out.println("Conected to Server!\n");
			String sql;
			
			Statement stmt = conn.createStatement();
			
			// drop table if it already exists
			sql = "DROP DATABASE IF EXISTS " + DB_NAME;
			stmt.executeUpdate(sql);
			
			//Create Database
			System.out.println("Creating Database...");
			sql = "CREATE DATABASE " + DB_NAME;
			stmt.executeUpdate(sql);
			System.out.println("Database created!\n");
			

			//Use Database
			sql = "USE " + DB_NAME;
			stmt.executeUpdate(sql);

			// Create Tables
			System.out.print("Creating tables: ");
			String tableName = "movies";
			System.out.print(tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "mID INTEGER,"
					+ "title VARCHAR(100),"
					// + "tagline VARCHAR(250),"
					+ "release_date DATE,"
					+ "popularity DECIMAL(15,10),"
					+ "vote_average DECIMAL(5,3),"
					+ "vote_count INTEGER,"
					+ "PRIMARY KEY(mID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "genres";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "gID INTEGER,"
					+ "gName VARCHAR(30),"
					+ "PRIMARY KEY(gID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "has_genre";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "mID INTEGER,"
					+ "gID INTEGER,"
					+ "PRIMARY KEY(mID, gID),"
					+ "FOREIGN KEY(mID) REFERENCES movies(mID),"
					+ "FOREIGN KEY(gID) REFERENCES genres(gID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "keywords";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "kID INTEGER,"
					+ "kName VARCHAR(50),"
					+ "PRIMARY KEY(kID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "has_keyword";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "mID INTEGER,"
					+ "kID INTEGER,"
					+ "PRIMARY KEY(mID, kID),"
					+ "FOREIGN KEY(mID) REFERENCES movies(mID),"
					+ "FOREIGN KEY(kID) REFERENCES keywords(kID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "actors";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "aID INTEGER,"
					+ "aName VARCHAR(30),"
					+ "PRIMARY KEY(aID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "has_actor";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "mID INTEGER,"
					+ "aID INTEGER,"
					+ "PRIMARY KEY(mID, aID),"
					+ "FOREIGN KEY(mID) REFERENCES movies(mID),"
					+ "FOREIGN KEY(aID) REFERENCES actors(aID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "directors";
			System.out.print(", " + tableName);
			sql = "CREATE TABLE " + tableName + "("
					+ "dID INTEGER,"
					+ "dName VARCHAR(50),"
					+ "PRIMARY KEY(dID)"
					+ ");";
			stmt.executeUpdate(sql);
			tableName = "has_director";
			System.out.print(", " + tableName + "...");
			sql = "CREATE TABLE " + tableName + "("
					+ "mID INTEGER,"
					+ "dID INTEGER,"
					+ "PRIMARY KEY(mID, dID),"
					+ "FOREIGN KEY(mID) REFERENCES movies(mID),"
					+ "FOREIGN KEY(dID) REFERENCES directors(dID)"
					+ ");";
			stmt.executeUpdate(sql);
			System.out.println("\n");
			
			// DATA INSERTION
			System.out.print("Inserting data: ");
			PreparedStatement pstmt;
			File dataFile;
			// Scanner scan;
			BufferedReader scan;
			String line;
			String[] values;
			int[] results;
			
			// movies
			tableName = "movies";
			System.out.print(tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("") && i != 2) {
						values[i] = "-1";
					} else if(values[i].equals("") && i == 2) {
						values[i] = "1-1-1";
					}
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// genres
			tableName = "genres";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// has_genre
			tableName = "has_genre";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// keywords
			tableName = "keywords";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// has_keyword
			tableName = "has_keyword";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// actors
			tableName = "actors";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// has_actor
			tableName = "has_actor";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// directors
			tableName = "directors";
			System.out.print(", " + tableName);
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			
			// has_director
			tableName = "has_director";
			System.out.print(", " + tableName + "...");
			sql = "INSERT INTO " + tableName + " VALUES(?, ?)";
			pstmt = conn.prepareStatement(sql);
			dataFile = new File("..\\DataParser\\" + tableName + ".csv");
			scan = new BufferedReader(new FileReader(dataFile));
			scan.readLine(); // skip header line
			while((line = scan.readLine()) != null) {
				pstmt.clearParameters();
				// System.out.println(line);
				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				/*for(int i = 1; i <= values.length; i++) {
					// values[i] = values[i].replaceAll("^\"|\"$", "");
					System.out.println("Field: " + i + ": " + values[i-1]);
				}*/
				for(int i = 0; i < values.length; i++) {
					if(values[i].equals("")) values[i] = "-1";
					values[i] = values[i].trim();
					try {
						pstmt.setInt(i+1, Integer.parseInt(values[i]));
					} catch(NumberFormatException nfe) {
						pstmt.setString(i+1, values[i]);
					}
				}
				pstmt.addBatch();
			}
			scan.close();
			pstmt.clearParameters();
			results = pstmt.executeBatch();
			pstmt.close();
			System.out.println("\n");
			
		} catch(FileNotFoundException fnfe) {
			System.out.println("File not found");
			fnfe.printStackTrace();
		} catch(NoSuchElementException nsee) {
			nsee.printStackTrace();
		} catch(BatchUpdateException bue) {
			System.out.println("\nBatch Update Exception");
			bue.printStackTrace();
		} catch(SQLSyntaxErrorException ssee) {
			System.out.println("SQL Syntax Error Exception.");
			ssee.printStackTrace();
		} catch(SQLNonTransientConnectionException sntce) {
			System.out.println("Connection Failed");
			sntce.printStackTrace();
		} catch(SQLException se) {
			System.out.println("SQL Exception.");
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn!=null) {
					System.out.println("Closing connection...");
					conn.close();
					System.out.println("Connection closed!\n");
				}
			} catch(SQLException se){
				System.out.println("Connection failed to close!\n");
				se.printStackTrace();
			} 
		} // end try/catch/finally
		
		// System.out.println("Exiting");

	}

}
