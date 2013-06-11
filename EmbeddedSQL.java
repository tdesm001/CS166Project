/*
 * MODIFIED BY JUSTIN KAHAL AND THOMAS DESMOND
 * SSID:860892022
 *
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */
 
 //FUCK THIS TESTING


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class EmbeddedSQL {

   
	public static String currentUser;	
	// reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of EmbeddedSQL
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public EmbeddedSQL (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end EmbeddedSQL

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   public String executeLoginQuery (String query) throws SQLException{
   		// creates a statement objectv
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      rs.next();
	  String resultset = rs.getString(numCol);
	  System.out.println(resultset);

	  stmt.close();
	  return resultset;
      // iterates through the result set and output them to standard out.
     /* boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
	  */
   }

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement objectv
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 4) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            EmbeddedSQL.class.getName () +
            " <dbname> <port> <user> <passwd>");
         return;
      }//end if
      
      Greeting();
      EmbeddedSQL esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the EmbeddedSQL object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         String passwd = args[3];
         esql = new EmbeddedSQL (dbname, dbport, user, passwd);

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("WELCOME! Are you an existing user?");
            System.out.println("---------");
            System.out.println("0. Yes I am!");
            System.out.println("1. No, I need to register!");
            System.out.println("9. < EXIT (Stop the program)");

            switch (readChoice()){
               case 0: LogInQuery(esql); break;
               case 1: RegisterQuery(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
   
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static void LogInQuery(EmbeddedSQL esql){
      try{
		System.out.println("You are trying to Login as an existing user, enter your Username (Make sure it is only 9 chars long)");
		while( true ){
			System.out.println("Please specify your username:");
			String input = in.readLine();
			String query = "SELECT COUNT(user_id) FROM users WHERE user_id='";
			query += input + "'";

         	String output = esql.executeLoginQuery (query);
		 	if( output != "1" ) {
				 	System.out.println("Sorry, that username isn't in our records.  Please try again.");
					continue;
			}
			else if( output == "1" ){
					currentUser = input;
					System.out.println("Please specify your password:");
					input = in.readLine();
					query = "SELECT COUNT(user_id) FROM users WHERE user_id='" + currentUser + "' AND password='" + input + "'";
					String output2 = esql.executeLoginQuery(query);
					if( output2 != "1" ){
							System.out.println("Sorry, the username and password don't match up.  Please try again.");
							continue;
					}
					else if( output2 == "1" ){
							System.out.println("Successfully logged in!");
							break;
					}
			}
		}


         //System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end QueryExample
   
   public static void RegisterQuery(EmbeddedSQL esql){
     	try{
         String query = "SELECT C.sid, COUNT(C.pid) FROM Suppliers S, Catalog C "
				+ "WHERE C.sid=S.sid GROUP BY C.sid;";
         
		 int rowCount = esql.executeQuery (query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
 		System.err.println (e.getMessage ());
	  }
   }//end Query1

   public static void Query2(EmbeddedSQL esql){
      try{
		String query = "SELECT C.sid, COUNT(C.pid) FROM Suppliers S, Catalog C "
				+ "WHERE C.sid=S.sid GROUP BY C.sid HAVING COUNT(C.pid)>=3;";
		
	  	int rowCount = esql.executeQuery (query);
        System.out.println ("total row(s): " + rowCount);
 
	  }catch(Exception e){
 		System.err.println (e.getMessage ());
	  }
   }//end Query2

   public static void Query3(EmbeddedSQL esql){
      try{
		String query = "SELECT sname, COUNT(C.pid) FROM Suppliers S, Catalog C "
				+ "WHERE pid IN (SELECT pid FROM Parts WHERE color='Green') AND S.sid=C.sid "
				+ "GROUP BY sname";
			
	  	int rowCount = esql.executeQuery (query);
        System.out.println ("total row(s): " + rowCount);
		
	  	}catch(Exception e){
 			System.err.println (e.getMessage ());
	  	}
   }//end Query3

   public static void Query4(EmbeddedSQL esql){
      try{
		String query = "SELECT P.pname, MAX(C.cost) FROM Parts P, Catalog C "
						+ "WHERE C.sid IN (SELECT temp.sid "
							+ "FROM (SELECT sid, pid FROM Catalog WHERE pid IN (SELECT pid FROM Parts WHERE color='Red')) as temp, "
							+ "(SELECT sid, pid FROM catalog WHERE PID IN (SELECT pid FROM Parts WHERE color='Green')) as temp2 "
							+ "WHERE temp.sid = temp2.sid) "
						+ "GROUP BY P.pname;";

		int rowCount = esql.executeQuery (query);
        System.out.println ("total row(s): " + rowCount);
		
	  }catch(Exception e){
 		System.err.println (e.getMessage ());
	  }
   }//end Query4

   public static void Query5(EmbeddedSQL esql){
     try{
		String query = "SELECT DISTINCT pname FROM Parts "
				+ "WHERE pid IN (SELECT pid FROM Catalog WHERE cost<";
	  	
        System.out.print("\tEnter cost: $");
        String input = in.readLine();
		query+=input;
		query += ");";

		int rowCount = esql.executeQuery (query);
        System.out.println ("total row(s): " + rowCount);
		
	 }catch(Exception e){
 		System.err.println (e.getMessage ());
	  }
   }//end Query5

   public static void Query6(EmbeddedSQL esql){
      try{
		String query = "SELECT address FROM Suppliers WHERE sid IN ("
						+ "SELECT sid FROM Catalog WHERE pid IN ("
						+ "SELECT pid FROM Parts WHERE pname='";

        System.out.print("\tEnter name of part: ");
		String input = in.readLine();
		query += input;
		query += "'));";

		int rowCount = esql.executeQuery (query);
        System.out.println ("total row(s): " + rowCount);
	  	
	  }catch(Exception e){
 		System.err.println (e.getMessage ());
	  }
   }//end Query6

}//end EmbeddedSQL
