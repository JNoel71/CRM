import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.*;
@SuppressWarnings("unchecked")

/*
 * @author Jordan Noel
 * Frontend of CRM application 
 * 
 * Run With:
 * java -classpath ".:/Library/Java/Extensions/sqlite-jdbc-3.27.2.1.jar" frontend
 */


public class frontend{

    public static JFrame frame; //the main frame
    public static JFrame secondFrame; //the frame for adding clients/sales
    public static JFrame editingFrame; //the frame for editing clients/sales
    public static JPanel clientPanel; //the panel with the client table and view/remove buttons
    public static JPanel salesPanel; //the panel with the sales table and view/remove buttons
    public static DefaultTableModel clientModel; //the model for the client table
    public static DefaultTableModel salesModel; //the model for the sales table

    public static void main(String[] args){
    	//create the db
    	backend.createDB();
    	
        //create the frame window
        frame = new JFrame("CRM");
        mainWindow();
    }

    
    /**
     * Creates the main window that you view on opening the program.
     * Contains a table of on current clients and sales with options to view them or remove them.
     */
    public static void mainWindow(){
        
        //make sure the frame is empty then repaint it and set the layout
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();

        //create the tabbed pane
        JTabbedPane jtp = new JTabbedPane();
        frame.getContentPane().add(jtp);

        //create the client tab
        clientPanel = new JPanel();
        constructClientTab();

        //create the sales tab
        salesPanel = new JPanel();
        constructSaleTab();

        //add the tabs to the window
        jtp.addTab("Client Info",clientPanel);
        jtp.addTab("Sales Info", salesPanel);

         //Create MenuBar
        JMenuBar bar = new JMenuBar();
        JMenu clients = new JMenu("Clients");
        JMenu sales = new JMenu("Sales");
        JMenu reports = new JMenu("Reports");
        bar.add(clients);
        bar.add(sales);
        bar.add(reports);
        frame.setJMenuBar(bar);

        //create ActionListener for add client
        JMenuItem addClient = new JMenuItem("Add");
        addClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                addingClient();
            }
        });

        //Include add clients in clients subMenu
        clients.add(addClient);

        //create menu item to add a sale and create a action listener for it
        JMenuItem addSale = new JMenuItem("Add");
        addSale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                addingSale();
            }
        });
        
        //Include add sale in the sales subMenu
        sales.add(addSale);
        
        //Create menu item to run a report on all the clients and create an action listener for it
        JMenuItem allClients = new JMenuItem("All Clients");
        allClients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	//confirm the user wants to save the file
            	if(JOptionPane.showConfirmDialog(frame,"The file will be saved under the same directory as this program. Would you like to proceed?","Continue?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            		//open the backend and run the client report
                    backend back = new backend();
                    back.clientReport();
            	}
            }
        });
        
        //Include all clients in the reports subMenu
        reports.add(allClients);
        
        //Create menu item to run a report on all the sales and create an action listener for it
        JMenuItem allSales = new JMenuItem("All Sales");
        allSales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
            	//confirm the user wants to save the file
            	if(JOptionPane.showConfirmDialog(frame,"The file will be saved under the same directory as this program. Would you like to proceed?","Continue?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            		//open the backend and run the sales report
                    backend back = new backend();
                    back.salesReport();
            	}
            }
        });
        
        //Include all sales in the reports subMenu
        reports.add(allSales);


        //occupy JTables with info from backend
        occupyTables();
        
        //Set the frame parameters
        frame.setSize(500,600);
        frame.setResizable(false);
        frame.setVisible(true);   
    }


    /**
     * Add all the information to the client panel
     */
    public static void constructClientTab(){

        //clear the client panel first
        clientPanel.removeAll();
        clientPanel.repaint();

        //set the layout for the client panel
        clientPanel.setLayout(new FlowLayout());

        //Add label for JTable
        JLabel clist = new JLabel("Client List");
        clientPanel.add(clist);

        //Create JTable from default model
        clientModel = new DefaultTableModel();
        JTable clientList = new JTable(clientModel);

        //Add columns to the model
        clientModel.addColumn("Name");
        clientModel.addColumn("SIN");

        //add the scroller to the pane
        JScrollPane listscroller = new JScrollPane();
        listscroller.setViewportView(clientList);

        //add the JTable to the frame
        clientPanel.add(listscroller);

        //Create the view button
        JButton viewClient = new JButton("View Info");
        
        //create the remove action listener
        viewClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //get selected row
                Vector<String> info = clientModel.getDataVector().get(clientList.getSelectedRow());

                //open the backend and retrieve the clients info using the sin
                backend back = new backend();
                ArrayList<String> individInfo = back.retrieveClientInfoBySIN((String)info.get(1));

                //call view client to display information
                viewClient(individInfo);
            }
        });

        //Create the remove button
        JButton removeClient = new JButton("Remove");

        //create the remove action Listener
        removeClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //get selected row
                Vector<String> info = clientModel.getDataVector().get(clientList.getSelectedRow());

                //open the backend and delete the client
                backend back = new backend();
                back.deleteClient((String)info.get(1)); 

                //reload the window
                mainWindow();     
            }
        });

        //Add view and remove button
        clientPanel.add(viewClient);
        clientPanel.add(removeClient);
    }


    /**
     * Add all the information to the sales panel
     */
    public static void constructSaleTab(){

        //clear the sales panel first
        salesPanel.removeAll();
        salesPanel.repaint();

        //set the layout of the sales panel
        salesPanel.setLayout(new FlowLayout());

        //Add the label for JTable
        JLabel slist = new JLabel("Sales List");
        salesPanel.add(slist);

        //Create the JTable from default model
        salesModel = new DefaultTableModel();
        JTable salesList = new JTable(salesModel);

        //Add columns to the model
        salesModel.addColumn("Client");
        salesModel.addColumn("Amount");
        salesModel.addColumn("Type");
        salesModel.addColumn("Transaction ID");

        //add the scroller to the pane
        JScrollPane salesScroller = new JScrollPane();
        salesScroller.setViewportView(salesList);
        salesPanel.add(salesScroller);

        //Create view button
        JButton viewSale = new JButton("View Info");
        
        //create an action listener for the view button
        viewSale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //get selected row
                Vector<String> info = salesModel.getDataVector().get(salesList.getSelectedRow());

                //open the backend and retrieve the sale info using the transaction id
                backend back = new backend();
                ArrayList<String> saleInfo = back.retrieveSaleInfoByID((String)info.get(3));

                //call viewing sale to display information
                viewingSale(saleInfo);
            }
        });

        //Create the remove button
        JButton removeSale = new JButton("Remove");

        //create the remove action Listener
        removeSale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //get selected row
                Vector<String> info = salesModel.getDataVector().get(salesList.getSelectedRow());

                //open the backend and delete the sale
                backend back = new backend();
                back.deleteSale((String)info.get(3)); 

                //reload the window
                mainWindow();     
            }
        });

        //add the buttons to the sales panel
        salesPanel.add(viewSale);
        salesPanel.add(removeSale);
    }


    /**
     * Creates the small pop-up frame used to add the client to the db.
     * Contains fields for all the parameters, sin, last name and first name are required.
     */
    public static void addingClient(){

        //Create the adding client frame
        secondFrame = new JFrame("Add Client");
        secondFrame.getContentPane().setLayout(new GridLayout(0,2));

        //first name
        JLabel fname = new JLabel("First Name:");
        JTextField firstName = new JTextField();
        firstName.setColumns(15);

        //last name
        JLabel lname = new JLabel("Last Name:");
        JTextField lastName = new JTextField();
        lastName.setColumns(15);

        //D.O.B
        JLabel date = new JLabel("D.O.B (DD/MM/YYYY):");
        DateFormat form = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField dob = new JFormattedTextField(form);
        dob.setColumns(15);

        //Town
        JLabel townLabel = new JLabel("Town:");
        JTextField town = new JTextField();
        town.setColumns(15);

        //Province
        JLabel provLabel = new JLabel("Province:");
        JTextField province = new JTextField();
        province.setColumns(15);

        //Postal Code
        JLabel postLabel = new JLabel("Postal Code:");
        JTextField postal = new JTextField();
        postal.setColumns(15);

        //Home Phone
        JLabel homeLabel = new JLabel("Home Phone:");
        JTextField homePhone = new JTextField();
        homePhone.setColumns(15);

        //Mobile Phone
        JLabel mobileLabel = new JLabel("Mobile Phone:");
        JTextField mobilePhone = new JTextField();
        mobilePhone.setColumns(15);

        //Sin
        JLabel sinLabel = new JLabel("SIN:");
        JTextField sin = new JTextField();
        sin.setColumns(15);

        //create the add button
        JButton add = new JButton("Add");

        //place an ActionListener on the add button
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //make sure required fields are filled
                if (firstName.getText().isEmpty() == false && lastName.getText().isEmpty() == false && sin.getText().isEmpty() == false){
                	
                	//open the backend
                    backend back = new backend();
                    
                    //check to make sure the given sin is not already in the db
                	if(back.checkSin(sin.getText())) {
                		
	                    //add the client to the db using the info in the textfields
	                    back.addClientToDB(sin.getText(),
	                                    firstName.getText(),
	                                    lastName.getText(),
	                                    dob.getText(),
	                                    town.getText(),
	                                    province.getText(),
	                                    postal.getText(),
	                                    homePhone.getText(),
	                                    mobilePhone.getText());
	
	                    //close the add client frame
	                    secondFrame.dispose();
	
	                    //reload the main frame
	                    occupyTables();
                	}
                	else {
                		JOptionPane.showMessageDialog(null, "The SIN you entered already exists in the database.", "Error", JOptionPane.INFORMATION_MESSAGE);	
                	}
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please fill in the required fields. (SIN, First Name, Last Name)", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //clear button with ActionListener, clears the textfields
        JButton clear = new JButton("Clear");
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                sin.setText("");
                firstName.setText("");
                lastName.setText("");
                dob.setText("");
                town.setText("");
                province.setText("");
                postal.setText("");
                homePhone.setText("");
                mobilePhone.setText("");

            }
        });

        //add all secondFrame components
        secondFrame.getContentPane().add(fname);
        secondFrame.getContentPane().add(firstName);
        secondFrame.getContentPane().add(lname);
        secondFrame.getContentPane().add(lastName);
        secondFrame.getContentPane().add(date);
        secondFrame.getContentPane().add(dob);
        secondFrame.getContentPane().add(townLabel);
        secondFrame.getContentPane().add(town);
        secondFrame.getContentPane().add(provLabel);
        secondFrame.getContentPane().add(province);
        secondFrame.getContentPane().add(postLabel);
        secondFrame.getContentPane().add(postal);
        secondFrame.getContentPane().add(homeLabel);
        secondFrame.getContentPane().add(homePhone);
        secondFrame.getContentPane().add(mobileLabel);
        secondFrame.getContentPane().add(mobilePhone);
        secondFrame.getContentPane().add(sinLabel);
        secondFrame.getContentPane().add(sin);
        secondFrame.getContentPane().add(add);
        secondFrame.getContentPane().add(clear);

        //Set size and stop resize
        secondFrame.setSize(300,300);
        secondFrame.setResizable(false);
        secondFrame.setVisible(true);
    }


    /**
     * Method that is used for adding a sale. Creates a small window for adding the sale.
     * Amount is required and a client must be selected from the table to link to the sale.
     */
    public static void addingSale(){

        //Create the adding sale frame
        secondFrame = new JFrame("Add Sale");
        secondFrame.getContentPane().setLayout(new GridLayout(0,2));

        //amount
        JLabel amountLabel = new JLabel("Amount ($):");
        JTextField amountField = new JTextField();
        amountField.setColumns(15);

        //date
        JLabel dateLabel = new JLabel("Date (DD/MM/YYYY):");
        DateFormat form = new SimpleDateFormat("dd/MM/yyyy");
        JFormattedTextField dateField = new JFormattedTextField(form);
        dateField.setColumns(15);

        //type
        JLabel typeLabel = new JLabel("Type:");
        JTextField typeField = new JTextField();
        typeField.setColumns(15);

        //select client
        JLabel clientLabel = new JLabel("Select a Client:");

        //Create JTable from default model
        DefaultTableModel cmodel = new DefaultTableModel();
        JTable clientList = new JTable(clientModel);

        //Add columns to the model
        cmodel.addColumn("Name");
        cmodel.addColumn("SIN");

        //add the scroller to the pane
        JScrollPane listscroller = new JScrollPane();
        listscroller.setViewportView(clientList);
        
        //open the backend and retrieve the client information
        backend back = new backend();
        ArrayList<ArrayList<String>> clientInfo = back.retrieveClients();

        //add the info retrieved to the client table
        for (int i=0; i<clientInfo.size(); i++){
            cmodel.addRow(new Object[]{clientInfo.get(i).get(1), clientInfo.get(i).get(0)});
        }

        //create the add button
        JButton add = new JButton("Add");

        //place an ActionListener on the add button
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //make sure required fields are filled
                if (amountField.getText().isEmpty() == false && clientList.getSelectionModel().isSelectionEmpty() == false){
                    
                    //collect info from selected client
                    Vector<String> info = cmodel.getDataVector().get(clientList.getSelectedRow());

                    //open the backend and add the sale to the db
                    backend back = new backend();
                    back.addSaleToDB(amountField.getText(),
                                    (String)info.get(1),
                                    dateField.getText(),
                                    typeField.getText());

                    //close the adding sale frame
                    secondFrame.dispose();
                    
                    //occupy the jtables again 
                    occupyTables();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please fill in the required fields (Amount). And select a client from the table.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //create a clear button
        JButton clear = new JButton("Clear");

        //add an actionlistener to clear, clears the textfields
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                amountField.setText("");
                dateField.setText("");
                typeField.setText("");
                clientList.getSelectionModel().clearSelection();
            }
        });

        //add all the content to the frame
        secondFrame.getContentPane().add(amountLabel);
        secondFrame.getContentPane().add(amountField);
        secondFrame.getContentPane().add(dateLabel);
        secondFrame.getContentPane().add(dateField);
        secondFrame.getContentPane().add(typeLabel);
        secondFrame.getContentPane().add(typeField);
        secondFrame.getContentPane().add(clientLabel);
        secondFrame.getContentPane().add(listscroller);
        secondFrame.getContentPane().add(add);
        secondFrame.getContentPane().add(clear);

        //Set size and stop resize
        secondFrame.setSize(400,400);
        secondFrame.setResizable(false);
        secondFrame.setVisible(true);
    }

    
    /**
     * Method that removes all information from the table created in mainWindow().
     */
    public static void clearTables(){

        //remove the rows of the client table
        while(clientModel.getRowCount() > 0){
            clientModel.removeRow(0);
        }

        //remove the rows of the sales table
        while(salesModel.getRowCount() > 0){
            salesModel.removeRow(0);
        }
    }

    
    /**
     * Method the occupies the tables with information about all the clients and sales.
     */
    public static void occupyTables(){

        //Clear the table first
        clearTables();

        //Open up the backend and retrieve the client and sales info
        backend back = new backend();
        ArrayList<ArrayList<String>> clientInfo = back.retrieveClients();
        ArrayList<ArrayList<String>> saleInfo = back.retrieveSales();

        //add the client info retrieved to the table
        for (int i=0; i<clientInfo.size(); i++){
            clientModel.addRow(new Object[]{clientInfo.get(i).get(1), clientInfo.get(i).get(0)});
        }

        //add the sale info retrieved to the table
        for (int i=0; i<saleInfo.size(); i++){
            salesModel.addRow(new Object[]{saleInfo.get(i).get(2), "$" + saleInfo.get(i).get(0), saleInfo.get(i).get(1), saleInfo.get(i).get(3)});
        }
    } 

    
    /**
     * Creates the client viewing frame, inside the client panel.
     * Clears the panel then inserts all the info for a client, as well as a return and edit button.
     * 
     * @param lst - a list of all information on an individual client.
     */
    public static void viewClient(ArrayList<String> lst){
    	
        //clear the panel
        clientPanel.removeAll();
        clientPanel.repaint();
        clientPanel.setLayout(new GridLayout(0,2));

        //last name
        JLabel lastNameLabel = new JLabel("Last Name: ");
        lastNameLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel lastNameInfo = new JLabel(lst.get(0));
        lastNameInfo.setFont(new Font("", Font.PLAIN, 18));

        //first name
        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel firstNameInfo = new JLabel(lst.get(1));
        firstNameInfo.setFont(new Font("", Font.PLAIN, 18));

        //SIN
        JLabel sinLabel = new JLabel("SIN: ");
        sinLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel sinInfo = new JLabel(lst.get(2));
        sinInfo.setFont(new Font("", Font.PLAIN, 18));

        //dob
        JLabel dobLabel = new JLabel("Date of Birth: ");
        dobLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel dobInfo = new JLabel(lst.get(3));
        dobInfo.setFont(new Font("", Font.PLAIN, 18));

        //town
        JLabel townLabel = new JLabel("Town: ");
        townLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel townInfo = new JLabel(lst.get(4));
        townInfo.setFont(new Font("", Font.PLAIN, 18));

        //province
        JLabel provinceLabel = new JLabel("Province: ");
        provinceLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel provinceInfo = new JLabel(lst.get(5));
        provinceInfo.setFont(new Font("", Font.PLAIN, 18));

        //postal code
        JLabel postalLabel = new JLabel("Postal Code: ");
        postalLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel postalInfo = new JLabel(lst.get(6));
        postalInfo.setFont(new Font("", Font.PLAIN, 18));

        //home phone
        JLabel homeLabel = new JLabel("Home Phone: ");
        homeLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel homeInfo = new JLabel(lst.get(7));
        homeInfo.setFont(new Font("", Font.PLAIN, 18));

        //mobile phone
        JLabel mobileLabel = new JLabel("Mobile Phone: ");
        mobileLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel mobileInfo = new JLabel(lst.get(8));
        mobileInfo.setFont(new Font("", Font.PLAIN, 18));

        //edit button
        JButton edit = new JButton("Edit");

        //add an actionlistener to edit, that opens editing window
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                editingClient(lst.get(2));
            }
        });

        //return button
        JButton returnToPrevious = new JButton("< Return");

        //add an actionlistener to returnToPrevious that reloads the client tab and occupies the table
        returnToPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                constructClientTab();
                occupyTables();
            }
        });

        //Insert all the labels/Info and buttons
        clientPanel.add(lastNameLabel);
        clientPanel.add(lastNameInfo);
        clientPanel.add(firstNameLabel);
        clientPanel.add(firstNameInfo);
        clientPanel.add(sinLabel);
        clientPanel.add(sinInfo);
        clientPanel.add(dobLabel);
        clientPanel.add(dobInfo);
        clientPanel.add(townLabel);
        clientPanel.add(townInfo);
        clientPanel.add(provinceLabel);
        clientPanel.add(provinceInfo);
        clientPanel.add(postalLabel);
        clientPanel.add(postalInfo);
        clientPanel.add(homeLabel);
        clientPanel.add(homeInfo);
        clientPanel.add(mobileLabel);
        clientPanel.add(mobileInfo);
        clientPanel.add(returnToPrevious);
        clientPanel.add(edit);
        frame.setVisible(true);
    }


    /**
     * Creates the sales viewing frame, inside the sales panel.
     * Clears the panel then inserts all the info for a sale, as well as a return and edit button.
     * 
     * @param lst - a list of all information on a sale.
     */
    public static void viewingSale(ArrayList<String> lst){

        //clear the panel
        salesPanel.removeAll();
        salesPanel.repaint();
        salesPanel.setLayout(new GridLayout(0,2));

        //transaction id
        JLabel idLabel = new JLabel("Transaction ID: ");
        idLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel idInfo = new JLabel(lst.get(0));
        idInfo.setFont(new Font("", Font.PLAIN, 18));

        //amount
        JLabel amountLabel = new JLabel("Amount: ");
        amountLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel amountInfo = new JLabel(lst.get(1));
        amountInfo.setFont(new Font("", Font.PLAIN, 18));

        //client name
        JLabel nameLabel = new JLabel("Client Name: ");
        nameLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel nameInfo = new JLabel(lst.get(2));
        nameInfo.setFont(new Font("", Font.PLAIN, 18));

        //client sin
        JLabel sinLabel = new JLabel("Client SIN: ");
        sinLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel sinInfo = new JLabel(lst.get(3));
        sinInfo.setFont(new Font("", Font.PLAIN, 18));

        //date
        JLabel dateLabel = new JLabel("Date: ");
        dateLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel dateInfo = new JLabel(lst.get(4));
        dateInfo.setFont(new Font("", Font.PLAIN, 18));

        //type
        JLabel typeLabel = new JLabel("Type of Sale: ");
        typeLabel.setFont(new Font("", Font.PLAIN, 18));
        JLabel typeInfo = new JLabel(lst.get(5));
        typeInfo.setFont(new Font("", Font.PLAIN, 18));

        //edit button
        JButton edit = new JButton("Edit");

        //add an actionlistener to edit the sale
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                editingSale(lst.get(0));
            }
        });

        //return button
        JButton returnToPrevious = new JButton("< Return");

        //add an actionlistener to returnToPrevious that reloads the sales tab and occupies the tables
        returnToPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                constructSaleTab();
                occupyTables();
            }
        });

        //Insert all the labels/Info and buttons
        salesPanel.add(idLabel);
        salesPanel.add(idInfo);
        salesPanel.add(amountLabel);
        salesPanel.add(amountInfo);
        salesPanel.add(nameLabel);
        salesPanel.add(nameInfo);
        salesPanel.add(sinLabel);
        salesPanel.add(sinInfo);
        salesPanel.add(dateLabel);
        salesPanel.add(dateInfo);
        salesPanel.add(typeLabel);
        salesPanel.add(typeInfo);
        salesPanel.add(returnToPrevious);
        salesPanel.add(edit);
        frame.setVisible(true);
    }


    /**
     * Creates a frame for editing a client using the same JFrame using when adding a client/sale.
     * Occupies the text fields using info retrieved from the backend about the individual client.
     * 
     * @param sin - the sin for the client to be edited, used to retrieve all the rest of the info.
     */
    public static void editingClient(String sin){

        //Create the editing frame
        secondFrame = new JFrame("Edit Client");
        secondFrame.getContentPane().setLayout(new GridLayout(0,2));

        //Retrieve info
        backend back = new backend();
        ArrayList<String> lst = back.retrieveClientInfoBySIN(sin);

        //first name
        JLabel fname = new JLabel("First Name:");
        JTextField firstName = new JTextField(lst.get(1));
        firstName.setColumns(15);

        //last name
        JLabel lname = new JLabel("Last Name:");
        JTextField lastName = new JTextField(lst.get(0));
        lastName.setColumns(15);

        //D.O.B
        JLabel date = new JLabel("D.O.B (DD/MM/YYYY):");
        JTextField dob = new JTextField(lst.get(3));
        dob.setColumns(15);

        //Town
        JLabel townLabel = new JLabel("Town:");
        JTextField town = new JTextField(lst.get(4));
        town.setColumns(15);

        //Province
        JLabel provLabel = new JLabel("Province:");
        JTextField province = new JTextField(lst.get(5));
        province.setColumns(15);

        //Postal Code
        JLabel postLabel = new JLabel("Postal Code:");
        JTextField postal = new JTextField(lst.get(6));
        postal.setColumns(15);

        //Home Phone
        JLabel homeLabel = new JLabel("Home Phone:");
        JTextField homePhone = new JTextField(lst.get(7));
        homePhone.setColumns(15);

        //Mobile Phone
        JLabel mobileLabel = new JLabel("Mobile Phone:");
        JTextField mobilePhone = new JTextField(lst.get(8));
        mobilePhone.setColumns(15);

        //Sin
        JLabel sinLabel = new JLabel("SIN:");
        JLabel sinInfo = new JLabel(lst.get(2));

        //create the update button
        JButton update = new JButton("Update");

        //place an ActionListener on the update button
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //make sure required fields are filled
                if (firstName.getText().isEmpty() == false && lastName.getText().isEmpty() == false){
                    
                    //open the backend and edit the clients info
                    backend back = new backend();
                    back.editClient(lst.get(2),
                                    firstName.getText(),
                                    lastName.getText(),
                                    dob.getText(),
                                    town.getText(),
                                    province.getText(),
                                    postal.getText(),
                                    homePhone.getText(),
                                    mobilePhone.getText());

                    //close the edit client frame
                    secondFrame.dispose();
                    
                    //reload the main window
                    mainWindow();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please fill in the required fields. (SIN, First Name, Last Name)", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //create a clear button
        JButton clear = new JButton("Clear");
        
        //add an action listener to clear that clears the text fields
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                firstName.setText("");
                lastName.setText("");
                dob.setText("");
                town.setText("");
                province.setText("");
                postal.setText("");
                homePhone.setText("");
                mobilePhone.setText("");

            }
        });

        //add all secondFrame components
        secondFrame.getContentPane().add(fname);
        secondFrame.getContentPane().add(firstName);
        secondFrame.getContentPane().add(lname);
        secondFrame.getContentPane().add(lastName);
        secondFrame.getContentPane().add(date);
        secondFrame.getContentPane().add(dob);
        secondFrame.getContentPane().add(townLabel);
        secondFrame.getContentPane().add(town);
        secondFrame.getContentPane().add(provLabel);
        secondFrame.getContentPane().add(province);
        secondFrame.getContentPane().add(postLabel);
        secondFrame.getContentPane().add(postal);
        secondFrame.getContentPane().add(homeLabel);
        secondFrame.getContentPane().add(homePhone);
        secondFrame.getContentPane().add(mobileLabel);
        secondFrame.getContentPane().add(mobilePhone);
        secondFrame.getContentPane().add(sinLabel);
        secondFrame.getContentPane().add(sinInfo);
        secondFrame.getContentPane().add(update);
        secondFrame.getContentPane().add(clear);

        //Set size and stop resize
        secondFrame.setSize(300,300);
        secondFrame.setResizable(false);
        secondFrame.setVisible(true);
    } 
    
    
    /**
     * Creates a frame for editing a sale using the same JFrame used when adding a client/sale.
     * Occupies the text fields using info retrieved from the backend about the sale.
     * 
     * @param id - the transaction id for the sale to be edited, used to retrieve the rest of the info on the sale.
     */
    public static void editingSale(String id){

        //Create editing frame
        secondFrame = new JFrame("Edit Sale");
        secondFrame.getContentPane().setLayout(new GridLayout(0,2));

        //Retrieve info
        backend back = new backend();
        ArrayList<String> lst = back.retrieveSaleInfoByID(id);

        //id
        JLabel idLabel = new JLabel("Transaction ID:");
        JLabel idInfo = new JLabel(lst.get(0));

        //amount
        JLabel amountLabel = new JLabel("Amount ($):");
        JTextField amountField = new JTextField(lst.get(1));
        amountField.setColumns(15);

        //date
        JLabel dateLabel = new JLabel("Date (DD/MM/YYYY):");
        JTextField dateField = new JFormattedTextField(lst.get(4));
        dateField.setColumns(15);

        //type
        JLabel typeLabel = new JLabel("Type:");
        JTextField typeField = new JTextField(lst.get(5));
        typeField.setColumns(15);

        //select client
        JLabel clientLabel = new JLabel("Select a Client:");

        //Create JTable from default model
        DefaultTableModel cmodel = new DefaultTableModel();
        JTable clientList = new JTable(clientModel);

        //Add columns to the model
        cmodel.addColumn("Name");
        cmodel.addColumn("SIN");

        //add the scroller to the pane
        JScrollPane listscroller = new JScrollPane();
        listscroller.setViewportView(clientList);
        
        //open a backend and retrieve the client information
        ArrayList<ArrayList<String>> clientInfo = back.retrieveClients();

        //add the info retrieved to the client table
        for (int i=0; i<clientInfo.size(); i++){
            cmodel.addRow(new Object[]{clientInfo.get(i).get(1), clientInfo.get(i).get(0)});
        }

        //this keeps track of the row in clientList that holds the client this sale is attached to.
        Integer row = null;

        //iterate through the second column/sin column to find the client this sale is attached to
        for (int i = cmodel.getRowCount() - 1; i >= 0; --i){
            if (cmodel.getValueAt(i,1).equals(lst.get(3))){
                row = i;
            }
        }

        //select the row with that connected client
        clientList.setRowSelectionInterval(row, row);

        //create the update button
        JButton update = new JButton("Update");

        //place an ActionListener on the add button
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                //make sure required fields are filled
                if (amountField.getText().isEmpty() == false && clientList.getSelectionModel().isSelectionEmpty() == false){
                    
                    //collect info from selected client
                    Vector<String> info = cmodel.getDataVector().get(clientList.getSelectedRow());

                    //open the backend and update the sale in db using the info in the text fields
                    backend back = new backend();
                    back.editSale(id,
                                  amountField.getText(),
                                  (String)info.get(1),
                                  dateField.getText(),
                                  typeField.getText());

                    //close the update sale frame
                    secondFrame.dispose();
                    
                    //reload the main window
                    mainWindow();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please fill in the required fields (Amount). And select a client from the table. ", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        //create a clear button
        JButton clear = new JButton("Clear");

        //add an actionlistener to clear
        clear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                amountField.setText("");
                dateField.setText("");
                typeField.setText("");
                clientList.getSelectionModel().clearSelection();
            }
        });

        //add all the content to the frame
        secondFrame.getContentPane().add(idLabel);
        secondFrame.getContentPane().add(idInfo);
        secondFrame.getContentPane().add(amountLabel);
        secondFrame.getContentPane().add(amountField);
        secondFrame.getContentPane().add(dateLabel);
        secondFrame.getContentPane().add(dateField);
        secondFrame.getContentPane().add(typeLabel);
        secondFrame.getContentPane().add(typeField);
        secondFrame.getContentPane().add(clientLabel);
        secondFrame.getContentPane().add(listscroller);
        secondFrame.getContentPane().add(update);
        secondFrame.getContentPane().add(clear);

        //Set size and stop resize
        secondFrame.setSize(400,400);
        secondFrame.setResizable(false);
        secondFrame.setVisible(true);
    }  
}