package eimock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReader {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:tcp://localhost/~/Desktop/wso2/packs/ei-560/wso2ei-6.5.0/wso2/analytics/wso2/worker/database/EI_ANALYTICS";

    //  Database credentials
    static final String USER = "wso2carbon";
    static final String PASS = "wso2carbon";

    public static List<MessageJump> getMessageJumps(String messageFlowId) {
        Connection conn = null;
        Statement stmt = null;

        List<MessageJump> messageJumps = new ArrayList<>();
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // STEP 3: Execute a query
            System.out.println("Connected database successfully...");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM ESBEVENTTABLE WHERE MESSAGEFLOWID='" + messageFlowId + "'";
            ResultSet rs = stmt.executeQuery(sql);

            // STEP 4: Extract data from result set
            while(rs.next()) {
                // Don't consider 'IgnoreElement's
                if (!(rs.getString("COMPONENTNAME")).equalsIgnoreCase("ignoreelement")) {
                    messageJumps.add(
                            new MessageJump(
                                    rs.getInt("COMPONENTINDEX"),
                                    rs.getInt("METATENANTID"),
                                    rs.getString("MESSAGEFLOWID"),
                                    rs.getString("HOST"),
                                    rs.getString("HASHCODE"),
                                    rs.getString("COMPONENTNAME"),
                                    rs.getString("COMPONENTTYPE"),
                                    rs.getInt("COMPONENTINDEX"),
                                    rs.getString("COMPONENTID"),
                                    String.valueOf(rs.getLong("STARTTIME")),
                                    String.valueOf(rs.getLong("ENDTIME")),
                                    rs.getLong("DURATION"),
                                    rs.getString("BEFOREPAYLOAD"),
                                    rs.getString("AFTERPAYLOAD"),
                                    rs.getString("CONTEXTPROPERTYMAP"),
                                    rs.getString("TRANSPORTPROPERTYMAP"),
                                    rs.getString("CHILDREN"),
                                    rs.getString("ENTRYPOINT"),
                                    rs.getString("ENTRYPOINTHASHCODE"),
                                    rs.getInt("FAULTCOUNT"),
                                    String.valueOf(rs.getLong("EVENTTIMESTAMP"))
                            )
                    );
                }
            }
            // STEP 5: Clean-up environment
            rs.close();
            return messageJumps;
        } catch(SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try
        return messageJumps;
    }
}