/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 *
 *
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_consoleArray = new ArrayList<>();
        m_error="";
        
        // You will need to put your Project Application in the below variable
        this.m_projectTeamApplication="Lab Technologist";
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)

        /* 1. View all clinical test */
        m_queryArray.add(new QueryData(
                "Query 1: View all clinical tests",
                "Select * from clinical_test;",
                null, null, null, false, false));
        m_consoleArray.add("Display all clinical tests: ");

        /* 2. Insert new clinical test. */
        m_queryArray.add(new QueryData(
                "Query 2: Insert new clinical test",
                "Insert into clinical_test (clinical_test_id, technician_id, patient_id, sop_id, equipment_id, test_type, clinical_test_date) values (?,?,?,?,?,?,?)",
                new String[] {"clinical_test_id","technician_id", "patient_id", "sop_id", "equipment_id", "test_type", "clinical_test_date"}, null,
                new boolean [] {false, false, false, false, false, false, false}, true, true));
        m_consoleArray.add("Insert new clinical test (For demonstration, enter clinical_id = 2105\n" +
                "              technician_id = 112, patient_id = 978166 sop_id = CT004, equipment_id = FC001\n" +
                "              test_type = HLA_typing, clinical_test_date = 2021-07-04 00:00:00): ");

        /*3. Insert new material for clinical test.*/
        m_queryArray.add(new QueryData(
                "Query 3: Add material usage",
                "Insert into clinical_test_uses_materials (material_id, clinical_test_id, clinical_tests_uses_materials_quantity_used) values (?,?,?)",
                new String[] {"material id", "clinical test id", "quantity used"}, null,
                new boolean [] {false, false, false}, true, true));
        m_consoleArray.add("Add material usage for clinical test (For demonstration, enter material_id = 5855\n " +
                "             clinical_test_id = 1096, quantity_used = 3): ");

        /* 4. view all elements of clinical test  clinical_test_id = '1044' */
        m_queryArray.add(new QueryData(
                "Query 4: View all elements of clinical test",
                "select c.clinical_test_id, technician_id, patient_id, sop_id, equipment_id, material_id, test_type, clinical_test_date \n" +
                "from clinical_test c join clinical_test_uses_materials m on c.clinical_test_id = m.clinical_test_id\n" +
                "where c.clinical_test_id = ?;\n",
                new String [] {"clinical_test_id"}, null,
                new boolean [] {false},  false, true));
        m_consoleArray.add("Display all elements of clinical test (For demonstration, enter clinical_test_id = 1044):  ");

        /* 5. Display equipment service history. c.clinical_test_id = '1044' */
        m_queryArray.add(new QueryData(
                "Query 5: Display equipment service history",
                "select s.equipment_id, equipment_purchase_date, equipment_service_interval, service_date, service_type, service_provider_id\n" +
                "from equipment e join service_history s on e.equipment_id = s.equipment_id\n" +
                "join clinical_test c on c.equipment_id = e.equipment_id\n" +
                "where c.clinical_test_id = ?;",
                new String[]{"clinical_test_id"}, null,
                new boolean[] {false}, false, true));
        m_consoleArray.add("Display equipment service history (For demonstration, enter clinical_test_id = 1044): ");

        /* 6. Like query: Find patient name that starts with. */
        m_queryArray.add(new QueryData(
                "Query 6: Find patient name that starts with.",
                "select patient_last_name, patient_first_name, patient_initial, patient_DOB,\n" +
                        "physician_last_name, physician_first_name, ph.physician_id, test_type\n" +
                        "from patient p join physician ph on p.physician_id = ph.physician_id\n" +
                        "join clinical_test t on p.patient_id = t.patient_id\n" +
                        "where patient_last_name like ?;",
                new String[]{"Last name starts with"}, "Fo",
                new boolean[] {true}, false, true));
        m_consoleArray.add("Find patient name that starts with: Fo");

        /* 7. Display all equipment due for service in an entered date range. */
        m_queryArray.add(new QueryData(
                "Query 7: Display all equipment due for service in an entered date range.",
                "select e.equipment_id, equipment_type, equipment_manufacturer, equipment_model,\n" +
                "p.service_provider_id, service_provider_phone_number, service_provider_email, service_due_date\n" +
                "from equipment e join service_history s on e.equipment_id = s.equipment_id\n" +
                "join service_provider p on s.service_provider_id = p.service_provider_id\n" +
                "where service_due_date between ? and ?;",
                new String [] {"Start of date range", "End of date range"}, "2021-05-01 00:00:00",
                new boolean[] {false, false}, false, true));
        m_consoleArray.add("Display all equipment due for service in an entered date range. (For demonstration, enter start of date range = 2021-05-01 00:00:00\n" +
                "              End of date range = 2021-08-31 00:00:00): ");

        /* 8. Verify materials used for clinical test were within the expiration date. */
        m_queryArray.add(new QueryData(
                "Query 8: Verify materials used for clinical test were within the expiration date.",
                "select c.clinical_test_id, m.material_id, material_manufacturer, material_description,\n" +
                "material_lot_number, material_expiration_date\n" +
                "from clinical_test_uses_materials c join materials m on c.material_id = m.material_id\n" +
                "where clinical_test_id = ?;",
                new String[] {"Clinical test id"}, null,  new boolean[] {false}, false, true));
        m_consoleArray.add("Verify materials used for clinical test were within the expiration date.\n" +
                "              (For demonstration, enter clinical_test_id = 1044):  ");

        /* 9. View total equipment purchases within the date range. */
        m_queryArray.add(new QueryData(
                "Query 9: View total equipment purchases within the date range.",
                "select round(sum(equipment_price), 2) as 'Total equipment purchases in time period'\n" +
                "from equipment where equipment_purchase_date between ? and ?;",
                new String[]{"Start date", "End date"}, null,
                new boolean[] {false, false}, false, true));
        m_consoleArray.add("Display total equipment purchases within the date range (For demonstration, enter Start date = 2020-01-01\n" +
                "              End date = 2020-12-31): ");

        /* 10. View all technician competent on certain test type */
        m_queryArray.add(new QueryData(
                "Query 10: View technicians competent on a certain test type.",
                "select competency_id, h.technician_id, technician_first_name, technician_last_name\n" +
                "from technician t join technician_has_competency h on t.technician_id = h.technician_id\n" +
                "where competency_id = ?;",
                new String[]{"competency_id"}, null,
                new boolean[] {false}, false, true));
        m_consoleArray.add("Display all technician competent on certain test type (For demonstration enter competency_id = Urinalysis): ");
    }


    public String GetConsoleText(int queryChoice) {
        return m_consoleArray.get(queryChoice);
    }

    public String GetQueryName(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryName();
    }

    public String GetQueryDefaultValue(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryDefaultValue();
    }

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }

    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {
        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;
    private ArrayList<String> m_consoleArray;
    private int m_updateAmount;

    public static String formatAsTable(String[][] Table)
    {
        int[] maxlengths = new int[Table[0].length]; // get max lengths of each column
        for (String[] row : Table) { // for each row in the table
            for (int i = 0; i < row.length; i ++)
            { // for each column
                maxlengths[i] = Math.max(maxlengths[i], row[i].length()); // take max between existing and string
            }
        }
        // build a string formatter
        StringBuilder formatBuilder = new StringBuilder();
        // for each column
        for (int mlength : maxlengths)
        {
            // format by longest length + 2 left aligned
            formatBuilder.append("%-").append(mlength + 2).append("s");
        }
        // convert format to string
        String rowformat = formatBuilder.toString();

        // build table result
        StringBuilder result = new StringBuilder();
        for (String[] row : Table)
        {
            // loop through rows and format them
            result.append(String.format(rowformat, row)).append("\n");
        }
        return result.toString(); // return table
    }

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals ("-console"))
            {
                // get information to connect to database
                Scanner in = new Scanner(System.in);
                System.out.println("Please enter hostname: ");
//                String host = in.nextLine();
                String host = "mysql-test01.ctldx44f89gq.us-east-1.rds.amazonaws.com";
                System.out.println("Please enter database name: ");
//                String databaseName = in.nextLine();
                String databaseName = "mm_cpsc502101team05";
                System.out.println("Please enter username: ");
//                String username = in.nextLine();
                String username = "admin";
                System.out.println("Please enter password: ");
                String password = in.nextLine();

                queryrunner.Connect(host, username, password, databaseName);

                // Check if the credentials are inputted correctly
                String error = queryrunner.GetError();
                if (!error.equals(""))
                {
                    // Exit
                    System.out.println(error);
                    return;
                }

                // Get queries' index number
                int n = queryrunner.GetTotalQueries();
                String[][] queries = new String[n + 1][2];
                queries[0][0] = "Query Number";
                queries[0][1] = "Query Information";

                // Create table for displaying query options
                for (int i = 1; i < n + 1; i++)
                {
                    queries[i][0] = Integer.toString(i);
                    queries[i][1] = queryrunner.GetConsoleText(i - 1);
                }
//                System.out.println(formatAsTable(queries));


                boolean executing = true;
                while (executing)
                {
                    System.out.println(formatAsTable(queries));
                    // ask for query or exit or help
                    System.out.println("Please choose a query to run, or enter exit to end the program!: ");
                    // getting input from user
                    String queryInput = in.nextLine();
                    int queryChoice = -1;
                    // if exit exit
                    if (queryInput.equals("exit")) {
                        executing = false;
                        System.out.println("Good Bye! ");
                        continue;
                    }
                    // if queries print table of queries
                    else if (queryInput.equals("queries")) {
                        System.out.println(formatAsTable(queries));
                        continue;
                    } else {
                        try {
                            // index number validation
                            queryChoice = Integer.parseInt(queryInput) - 1;
                            if (queryChoice < 0 || queryChoice > n - 1)
                            {
                                continue;
                            }
                        } catch (NumberFormatException nfe) {
                            continue; // executing until getting index number for query
                        }
                    }

                    // if query need params
                    ArrayList<String> paramlist = new ArrayList<String>();
                    String[] params = {};
                    int count = 0;
                    // if it does loop through and ask for them
                    if (queryrunner.isParameterQuery(queryChoice))
                    {
                        System.out.println(queryrunner.GetConsoleText(queryChoice) + "\n");
                        count = queryrunner.GetParameterAmtForQuery(queryChoice);
                        for (int j = 0; j < count; j++)
                        {
                            System.out.println(queryrunner.GetParamText(queryChoice, j) + ": ");
                            String param = in.nextLine();
                            paramlist.add(param);
                        }
                    }
                    if (queryrunner.isActionQuery(queryChoice))
                    {
                        queryrunner.ExecuteUpdate(queryChoice, paramlist.toArray(params));
                        int changed = queryrunner.GetUpdateAmount();
                        System.out.println(changed + " line(s) modified");
                    } else {
                        queryrunner.ExecuteQuery(queryChoice, paramlist.toArray(params));
                        String[] headers = queryrunner.GetQueryHeaders();
                        String[][] data = queryrunner.GetQueryData();
                        String[][] Table = new String[data.length + 1][data[0].length];
                        Table[0] = headers;
                        // column headers
                        for (int i = 0; i < data.length; i++)
                        {
                            Table[i + 1] = data[i];
                        }
                        System.out.println(formatAsTable(Table)); // print formatted table
                    }
                }
                queryrunner.Disconnect();
            }
        }
    }    
}