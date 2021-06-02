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
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";

        // TODO - You will need to change the queries below to match your queries.
        
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

        /* 2. Insert new clinical test. */
        m_queryArray.add(new QueryData(
                "Query 2: Add new clinical test",
                "Insert into clinical_test (clinical_test_id, technician_id, patient_id, sop_id, equipment_id, test_type, clinical_test_date) values (?,?,?,?,?,?,?)",
                new String[] {"clinical_test_id","technician_id", "patient_id", "sop_id", "equipment_id", "test_type", "clinical_test_date"}, null,
                new boolean [] {false, false, false, false, false, false, false}, true, true));

        /*3. Insert new material for clinical test.*/
        m_queryArray.add(new QueryData(
                "Query 3: Add material usage",
                "Insert into clinical_test_uses_materials (material_id, clinical_test_id, clinical_tests_uses_materials_quantity_used) values (?,?,?)",
                new String[] {"material id", "clinical test id", "quantity used"}, null,
                new boolean [] {false, false, false}, true, true));

        /* 4. view all elements of clinical test  clinical_test_id = '1044' */
        m_queryArray.add(new QueryData(
                "Query 4: View all elements of clinical test",
                "select c.clinical_test_id, technician_id, patient_id, sop_id, equipment_id, material_id, test_type, clinical_test_date \n" +
                "from clinical_test c join clinical_test_uses_materials m on c.clinical_test_id = m.clinical_test_id\n" +
                "where c.clinical_test_id = ?;\n",
                new String [] {"clinical_test_id"}, null,
                new boolean [] {false},  false, true));

        /* 5. Verify equipment history c.clinical_test_id = '1044' */
        m_queryArray.add(new QueryData(
                "Query 5: Verify equipment history",
                "select s.equipment_id, equipment_purchase_date, equipment_service_interval, service_date, service_type, service_provider_id\n" +
                "from equipment e join service_history s on e.equipment_id = s.equipment_id\n" +
                "join clinical_test c on c.equipment_id = e.equipment_id\n" +
                "where c.clinical_test_id = ?;",
                new String[]{"clinical_test_id"}, null,
                new boolean[] {false}, false, true));

        /* 6. Verify equipment history c.clinical_test_id = '1044' */
        m_queryArray.add(new QueryData(
                "Query 6: Verify equipment service history.",
                "select s.equipment_id, equipment_purchase_date, equipment_service_interval, service_date, service_type, service_provider_id\n" +
                "from equipment e join service_history s on e.equipment_id = s.equipment_id\n" +
                "join clinical_test c on c.equipment_id = e.equipment_id\n" +
                "where c.clinical_test_id = ?;",
                new String[]{"clinical_test_id"}, null,
                new boolean[] {false}, false, true));

        /* 7. View all patients */
        m_queryArray.add(new QueryData(
                "Query 7: Equipment due for service in date range.",
                "select e.equipment_id, equipment_type, equipment_manufacturer, equipment_model,\n" +
                "p.service_provider_id, service_provider_phone_number, service_provider_email, service_due_date\n" +
                "from equipment e join service_history s on e.equipment_id = s.equipment_id\n" +
                "join service_provider p on s.service_provider_id = p.service_provider_id\n" +
                "where service_due_date between ? and ?;",
                new String [] {"Start of date range", "End of date range"}, "2021-05-01 00:00:00",
                new boolean[] {false, false}, false, true));

        /* 8. Verify materials used were within the expiration date. */
        m_queryArray.add(new QueryData(
                "Query 8: Check material expiration dates.",
                "select c.clinical_test_id, m.material_id, material_manufacturer, material_description,\n" +
                "material_lot_number, material_expiration_date\n" +
                "from clinical_test_uses_materials c join materials m on c.material_id = m.material_id\n" +
                "where clinical_test_id = ?;",
                new String[] {"Clinical test id"}, null,  new boolean[] {false}, false, true));

        /* 9. View total equipment purchases */
        m_queryArray.add(new QueryData(
                "Query 9: View total equipment purchases",
                "select round(sum(equipment_price), 2) as 'Total equipment purchases in time period'\n" +
                "from equipment where equipment_purchase_date between ? and ?;",
                new String[]{"Start date", "End date"}, null,
                new boolean[] {false, false}, false, true));

        /* 10. View all technician competent on certain test type */
        m_queryArray.add(new QueryData(
                "Query 10: View technicians competent on a certain test type.",
                "select competency_id, h.technician_id, technician_first_name, technician_last_name\n" +
                "from technician t join technician_has_competency h on t.technician_id = h.technician_id\n" +
                "where competency_id = ?;",
                new String[]{"competency_id"}, null,
                new boolean[] {false}, false, true));
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
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        // TODO code application logic here

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
            	System.out.println("Nothing has been implemented yet. Please implement the necessary code");
               // TODO 
                // You should code the following functionality:

                //    You need to determine if it is a parameter query. If it is, then
                //    you will need to ask the user to put in the values for the Parameters in your query
                //    you will then call ExecuteQuery or ExecuteUpdate (depending on whether it is an action query or regular query)
                //    if it is a regular query, you should then get the data by calling GetQueryData. You should then display this
                //    output. 
                //    If it is an action query, you will tell how many row's were affected by it.
                // 
                //    This is Psuedo Code for the task:  
                //    Connect()
                //    n = GetTotalQueries()
                //    for (i=0;i < n; i++)
                //    {
                //       Is it a query that Has Parameters
                //       Then
                //           amt = find out how many parameters it has
                //           Create a paramter array of strings for that amount
                //           for (j=0; j< amt; j++)
                //              Get The Paramater Label for Query and print it to console. Ask the user to enter a value
                //              Take the value you got and put it into your parameter array
                //           If it is an Action Query then
                //              call ExecuteUpdate to run the Query
                //              call GetUpdateAmount to find out how many rows were affected, and print that value
                //           else
                //               call ExecuteQuery 
                //               call GetQueryData to get the results back
                //               print out all the results
                //           end if
                //      }
                //    Disconnect()


                // NOTE - IF THERE ARE ANY ERRORS, please print the Error output
                // NOTE - The QueryRunner functions call the various JDBC Functions that are in QueryJDBC. If you would rather code JDBC
                // functions directly, you can choose to do that. It will be harder, but that is your option.
                // NOTE - You can look at the QueryRunner API calls that are in QueryFrame.java for assistance. You should not have to 
                //    alter any code in QueryJDBC, QueryData, or QueryFrame to make this work.
//                System.out.println("Please write the non-gui functionality");
                
            }
        }
 
    }    
}
