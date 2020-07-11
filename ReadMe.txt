CRM Application:

Author: Jordan Noel
Language: Java
Java Files: frontend.java (handles the GUI and calls methods from the backend)
	    backend.java (handles the database and creation of reports)
Required External Libraries: Apache POI, JDBC SQLite

Description:
A CRM application with the ability to keep track of clients and sales. Has the functionality to add,
delete, view or edit a client or sale. Links clients and sales together such that each sale belongs 
to a certain client and when that client is deleted all the sales associated with the individual 
will also be deleted. Has the ability to run 2 different reports both that yield excel files, one 
retrieves all the clients and their information. While the other retrieves all the sales along with 
their information.
