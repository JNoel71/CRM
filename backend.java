import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.FileOutputStream;
import java.io.IOException;
 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * @author Jordan Noel
 * Backend of CRM application 
 * 
 * Run With:
 * java -classpath ".:/Library/Java/Extensions/sqlite-jdbc-3.27.2.1.jar" backend
 */


public class backend extends frontend {

    public static void main(String[] args){
        createDB();
    }

    
    /**
     * Runs the SQL that creates the db and the table for clients and sales.
     */
    public static void createDB(){

        //Sql to create the client table
        String sql = "CREATE TABLE IF NOT EXISTS clients (\n"
                    + "sin text PRIMARY KEY,\n"
                    + "first text NOT NULL,\n"
                    + "last text NOT NULL,\n"
                    + "dob text,\n"
                    + "town text,\n"
                    + "province text,\n"
                    + "postal text,\n"
                    + "home text,\n"
                    + "mobile text\n"
                    + ");";
        
        //Sql to create the sales table
        String salesSQL = "CREATE TABLE IF NOT EXISTS sales (\n"
                    + "id text PRIMARY KEY,\n"
                    + "amount int,\n"
                    + "clientSin text,\n"
                    + "date text,\n"
                    + "type text\n"
                    + ");";
        
        //connect to the db and execute the two statements
        try ( Connection conn = DriverManager.getConnection("jdbc:sqlite:client.db")){
            if (conn != null){
                Statement stmt = conn.createStatement();
                Statement stmt2 = conn.createStatement();
                stmt.execute(sql);
                stmt2.execute(salesSQL);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }  
    }


    /**
     * Connects to the database, then returns the connection.
     * 
     * @return conn - the connection to the database
     */
    private Connection connect(){

        //connect to the db then return the connection
        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:client.db");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * Takes all the info for a client as a parameter and then adds them to the db.
     * 
     * @param sin - the sin for client.
     * @param first - the first name of the client.
     * @param last - the last name of the client.
     * @param dob - the date of birth of the client.
     * @param town - the town the client lives in.
     * @param province - the province the client lives in.
     * @param postal - the clients postal code.
     * @param home - the clients home phone number.
     * @param mobile - the clients mobile phone number.
     */
    public void addClientToDB(String sin, String first, String last, String dob, String town, String province, String postal, String home, String mobile){

        //Sql to insert into the table
        String sql = "INSERT INTO clients(sin,first,last,dob,town,province,postal,home,mobile) VALUES(?,?,?,?,?,?,?,?,?)";

        //connect the the db and execute a prepared statement
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
        		//turn ? into the strings handed to the method
                pstmt.setString(1,sin);
                pstmt.setString(2,first);
                pstmt.setString(3,last);
                pstmt.setString(4,dob);
                pstmt.setString(5,town);
                pstmt.setString(6,province);
                pstmt.setString(7,postal);
                pstmt.setString(8,home);
                pstmt.setString(9,mobile);
                pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Method that adds a sale to the DB.
     * 
     * @param amount - the amount from the sale
     * @param sin - the sin of the client attached to the sale
     * @param date - the date of the sale
     * @param type - the type of sale
     */
    public void addSaleToDB(String amount, String sin, String date, String type){

        //boolean used to determine if id is unique
        boolean unique = false;

        //the id for the sale
        String id = null;

        //create a new id until it is unique
        while(unique == false){
            id = createNewID();
            unique = this.checkID(id);
        }

        //Sql to insert into the table
        String sql = "INSERT INTO sales(id,amount,clientSin,date,type) VALUES(?,?,?,?,?)";

        //connect the the db and execute a prepared statement
        try (Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1,id);
                pstmt.setString(2,amount);
                pstmt.setString(3,sin);
                pstmt.setString(4,date);
                pstmt.setString(5,type);
                pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Creates a random ID for a sale.
     * 
     * @return newID - a new random ID for a sale
     */
    public static String createNewID(){

        //possible numbers and letters
        String nums = "0123456789";
        String letters = "abcdefghijklmnopqrstuvwxyz";
        
        //the newID string
        String newID = "";

        Random random = new Random();

        //iterate through loop and add 9 characters to the ID
        for(int i = 0; i <= 8; i++){
            //if i is even add a number
            if(i % 2 == 0){
                newID = newID + nums.charAt(random.nextInt(nums.length()));
            }
            //if i is odd add a letter
            else{
                newID = newID + letters.charAt(random.nextInt(letters.length()));
            }
        }

        return newID; 
    }


    /**
     * Method that checks in a sales ID is unique.
     * 
     * @param id - the id to be checked for uniqueness
     * @return true if the id is unique, false if it is not
     */
    public boolean checkID(String id){

        //arraylist of ids
        ArrayList<String> ids = new ArrayList<String>();

        try{
            //make a connection to the db and create the statement
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();

            //get all ids from sales
            ResultSet rs = stmt.executeQuery("SELECT id FROM sales");

            //add all the ids to the arraylist
            while (rs.next()){
                ids.add(rs.getString("id"));
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        //if the id is not unique return false, otherwise return true
        if(ids.contains(id)){
            return false;
        }
        else{
            return true;
        }
    }

    
    /**
     * Method that checks in a clients SIN is unique.
     * 
     * @param sin - the sin to be checked for uniqueness
     * @return true if the sin is unique, false if it is not
     */ 
    public boolean checkSin(String sin){

        //arraylist of sins
        ArrayList<String> sins = new ArrayList<String>();

        try{
            //make a connection to the db and create the statement
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();

            //get all sins from clients
            ResultSet rs = stmt.executeQuery("SELECT sin FROM clients");

            //add all the sins to the arraylist
            while (rs.next()){
                sins.add(rs.getString("sin"));
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        //if the sin is not unique return false, otherwise return true
        if(sins.contains(sin)){
            return false;
        }
        else{
            return true;
        }
    }
    

    /**
     * Retrieves the sin, first and last names of the clients.
     * 
     * @return clientList - all the information about each client in an ArrayList.
     */
    public ArrayList<ArrayList<String>> retrieveClients(){

        //arraylist to hold client info
        ArrayList<ArrayList<String>> clientList = new ArrayList<ArrayList<String>>();

        try{
            //connect to the db
            Connection conn = this.connect();

            //create a statement
            Statement stmt = conn.createStatement();

            //execute a query
            ResultSet rs = stmt.executeQuery("SELECT * FROM clients");

            while (rs.next()){
                //create a temporary list to add info for a individual client to
                ArrayList<String> temp = new ArrayList<String>();

                //add the sin and the name of a client to the list
                temp.add(rs.getString("SIN"));
                temp.add("" + rs.getString("last") + ", " + rs.getString("first"));
                temp.add(rs.getString("dob"));
                temp.add(rs.getString("town"));
                temp.add(rs.getString("province"));
                temp.add(rs.getString("postal"));
                temp.add(rs.getString("home"));
                temp.add(rs.getString("mobile"));

                //add the temporary list to the main clientList
                clientList.add(temp);
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return clientList;
    }

    
    /**
     * Retrieves the sales from the db.
     * 
     * @return salesList - an ArrayList of all the information about individual sales.
     */
    public ArrayList<ArrayList<String>> retrieveSales(){
        
        //the arraylist to hold the sales
        ArrayList<ArrayList<String>> saleList = new ArrayList<ArrayList<String>>();

        try{
            //connect to the db
            Connection conn = this.connect();

            //create a statement
            Statement stmt = conn.createStatement();

            //query all the sales
            ResultSet rs = stmt.executeQuery("SELECT * FROM sales");

            while (rs.next()){
                //create a temporary arraylist to add to the main arraylist
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(rs.getString("amount"));
                temp.add(rs.getString("type"));

                //get the client name from the sin stored in sale
                ArrayList<String> clientInfo = this.retrieveClientInfoBySIN(rs.getString("clientSin"));
                temp.add(clientInfo.get(0) + ", " + clientInfo.get(1));

                //add the transaction ID
                temp.add(rs.getString("id"));
                
                //add the clients SIN
                temp.add(rs.getString("clientSin"));
                
                //add the date of the sale
                temp.add(rs.getString("date"));

                //add the temporary list to the main list
                saleList.add(temp);
            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return saleList;
    }


    /**
     * Takes a parameter of a clients sin and returns a list of all information about the client.
     * 
     * @param sin - the sin for the client who's info is retrieved.
     * @return info - an ArrayList that holds all info about the client who's sin matched the parameter sin.
     */
    public ArrayList<String> retrieveClientInfoBySIN(String sin){
        
        //Arraylist to hold the information on the client
        ArrayList<String> info = new ArrayList<String>();

        try{
            //connect to the db and create a statement
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();

            //query the client with the matching sin
            ResultSet rs = stmt.executeQuery("SELECT * FROM clients WHERE sin = '" + sin +"'");

            //iterate through the result, even though there will only be one
            while (rs.next()){
                
                //add the client info to the arraylist
                info.add(rs.getString("last"));
                info.add(rs.getString("first"));
                info.add(rs.getString("sin"));
                info.add(rs.getString("dob"));
                info.add(rs.getString("town"));
                info.add(rs.getString("province"));
                info.add(rs.getString("postal"));
                info.add(rs.getString("home"));
                info.add(rs.getString("mobile"));
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return info;
    }


    /**
     * A method that retrieves the data on a sale given a unique id
     * 
     * @param id - the unique id of the transaction
     * @return info - the arraylist containing the info on the sale
     */
    public ArrayList<String> retrieveSaleInfoByID(String id){

        //the array to be returned with the info about the sale
        ArrayList<String> info = new ArrayList<String>();

        try{
            //connect to the db and create a statement
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();

            //query the sale with the matching id
            ResultSet rs = stmt.executeQuery("SELECT * FROM sales WHERE id = '" + id +"'");

            //iterate through the results, even though there will only be one
            while (rs.next()){

                //add the sales info to the arraylist
                info.add(rs.getString("id"));
                info.add(rs.getString("amount"));
                //retrieve the client name using the sin
                ArrayList<String> client = this.retrieveClientInfoBySIN(rs.getString("clientSin"));
                info.add(client.get(1) + " " + client.get(0));
                info.add(rs.getString("clientSin"));
                info.add(rs.getString("date"));
                info.add(rs.getString("type"));
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return info;

    }


    /**
     * Deletes a client from the db, given they have a sin matching the passed in string.
     * Also deletes any sales associated with the client.
     * 
     * @param sin - the sin of the client to be deleted.
     */
    public void deleteClient(String sin){

        //sql to delete a client with corresponding sin
        String clientSql = "DELETE FROM clients WHERE sin = '" + sin + "'";

        //sql to delete a sale with corresponding sin
        String salesSql = "DELETE FROM sales WHERE clientSin = '" + sin + "'";

        //connect and execute the sql
        try (Connection conn = this.connect();
             PreparedStatement clientpstmt = conn.prepareStatement(clientSql);
             PreparedStatement salespstmt = conn.prepareStatement(salesSql)){

                //delete the sales and client associated with a sin
                salespstmt.executeUpdate();
                clientpstmt.executeUpdate();

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * A method to delete a sale from the database given a sale id
     * 
     * @param id - the unique identifier for the transaction
     */
    public void deleteSale(String id){

        //sql to delete a sale given an id
        String sql = "DELETE FROM sales WHERE id = '" + id + "'";
        
        //connect to the db, create a prepared statement then execute the query
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

             pstmt.executeUpdate();
            
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }


    /**
     * Updates the info for an existing client using the passed in information.
     * 
     * @param sin - the sin of the client.
     * @param first - the first name of the client.
     * @param last - the last name of the client.
     * @param dob - the date of birth of the client.
     * @param town - the town the client lives in.
     * @param province - the province the client lives in.
     * @param postal - the clients postal code.
     * @param home - the clients home phone number.
     * @param mobile - the clients mobile phone number.
     */
    public void editClient(String sin, String first, String last, String dob, String town, String province, String postal, String home, String mobile){

        //create statement to update the client
        String sql = "UPDATE clients SET last = '" + last + "'," + 
                      "first = '" + first + "'," +
                      "dob = '" + dob + "'," +
                      "town = '" + town + "'," +
                      "province = '" + province + "'," +
                      "postal = '" + postal + "'," +
                      "home = '" + home + "'," +
                      "mobile = '" + mobile + "'," +
                      "sin = '" + sin + "'" +
                      "WHERE sin = '" + sin  + "'";
        
        //connect to the db, create a prepared statement then execute the query
        try ( Connection conn = this.connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)){
            
              pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }  
    

    /**
     * Method that updates a sale.
     * 
     * @param id - the unique id of the sale
     * @param amount - the amount for the sale (in dollars)
     * @param clientSin - the sin of the client attached to the sale
     * @param date - the date the sale took place
     * @param type - the type of sale
     */
    public void editSale(String id, String amount, String clientSin, String date, String type){

        //create statement to update the sale
        String sql = "UPDATE sales SET amount = '" + amount + "'," + 
                     "clientSin = '" + clientSin + "'," +
                     "date = '" + date + "'," +
                     "type = '" + type + "'" +
                     "WHERE id = '" + id  + "'";
        
        //connect to the database and execute the update
        try ( Connection conn = this.connect();
              PreparedStatement pstmt = conn.prepareStatement(sql)){
    
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }       
    }
    
    
    /**
     * A Method that creates an excel file with the entire client database inside. 
     */
    public void clientReport(){
    	
    	//create the workbook and the main sheet
    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Client listings");
        
        //create a tree map that 
        Map<String, Object[]> clients = new TreeMap<String, Object[]>();
        clients.put("1",new Object[] {"Name","SIN","D.O.B","Town","Province","Postal","Home","Mobile"});
        
        //get all the client info
        ArrayList<ArrayList<String>> info = this.retrieveClients();
        
        //counter to track the current place in map
        int counter = 2;
        
        //iterate through the client info
        for(int i=0; i < info.size(); i++) {
        	
        	//get the info for an individual client and put it into the map
    		ArrayList<String> temp = info.get(i);
    		clients.put(Integer.toString(counter),new Object[] {temp.get(1),temp.get(0),temp.get(2),temp.get(3),temp.get(4),temp.get(5),temp.get(6),temp.get(7)});
    		
    		//increase the counter
    		counter++;
        }
        
        //track the rows in the excel sheet
        int rowCount = 0;
        
        //retrieve the keys from the map
        Set<String> keySet = clients.keySet();
        
        //iterate through the map
        for (String key : keySet) {
        	
        	//create a row
            Row row = sheet.createRow(rowCount++);
            
            //get the data on the individual client using the key
            Object[] rowData = clients.get(key);
            
            //track the columns in the sheet
            int columnCount = 0;
            
            //iterate through the client information and add columns as needed
            for (Object field : rowData) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }    
        }
         
        //write the file, close the outputstream and workbook
        try (FileOutputStream outputStream = new FileOutputStream("Clients.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        }
        catch (IOException e) {
        	System.out.println(e.getMessage());
        }
    } 
    
    
    /**
     * A Method that creates an excel file with the entire sales database inside 
     */
    public void salesReport(){
    	
    	//create the workbook and the main sheet
    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sale listings");
        
        //create a tree map that holds all the sales from the database and info about them
        Map<String, Object[]> sales = new TreeMap<String, Object[]>();
        sales.put("1",new Object[] {"ID","Amount","Client Name","Client SIN","Date","Type"});
        
        //get all the sale info
        ArrayList<ArrayList<String>> info = this.retrieveSales();
        
        //counter to track the current place in map
        int counter = 2;
        
        //iterate through the sale info
        for(int i=0; i < info.size(); i++) {
        	
        	//get the info for an individual sale and put it into the map
    		ArrayList<String> temp = info.get(i);
    		sales.put(Integer.toString(counter),new Object[] {temp.get(3),temp.get(0),temp.get(2),temp.get(4),temp.get(5),temp.get(1)});
    		
    		//increase the counter
    		counter++;
        }
        
        //track the rows in the excel sheet
        int rowCount = 0;
        
        //retrieve the keys from the map
        Set<String> keySet = sales.keySet();
        
        //iterate through the map
        for (String key : keySet) {
        	
        	//create a row
            Row row = sheet.createRow(rowCount++);
            
            //get the data on the sale using the key
            Object[] rowData = sales.get(key);
            
            //track the columns in the sheet
            int columnCount = 0;
            
            //iterate through the sale information and add columns as needed
            for (Object field : rowData) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }    
        }
         
        //write the file, close the outputstream and workbook
        try (FileOutputStream outputStream = new FileOutputStream("Sales.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
        }
        catch (IOException e) {
        	System.out.println(e.getMessage());
        }
    }  
}