import java.sql.*;
import java.util.*;
public class EsempioDB {
     public Vector takevalue()
    {
        Vector v = new Vector();
        persona p = new persona();
        //carico il driver
        System.setProperty("jdbc.drivers", "sun.jdbc.odbc.JdbcOdbcDriver");
        //nome e indirizzo database
        String URL_miodb = "jdbc:mysql://localhost:3306/esempiodb?serverTimezone=UTC&useLegacyDatetimeCode=false";
        //definizione delle query
        String query = "SELECT nome, cognome FROM persona";
        //stabilisco la connessione
        System.out.println("Connessione con: " + URL_miodb);
        Connection connessione = null;
        try
        {
            connessione = DriverManager.getConnection(URL_miodb, "root", "root");
        }
        catch(Exception e)
        {
            System.out.println("Errore nella connessione: " + e);
            System.exit(1);
        }
        try
        {
            //statement per database
            Statement statement = connessione.createStatement();
            //interrogo il database con una query
            ResultSet resultset = statement.executeQuery(query);
            while (resultset.next()) {
              for (int i = 1; i <= 2; i++){
                 v.add(resultset.getString(i));
              }
            }
        }
        catch(Exception e)
        {
            System.out.println("Errore: " + e);
            System.exit(1);
        }
        finally{
            if(connessione != null)
            {
                try
                {
                    connessione.close();  //chiusura connessione
                }
                catch(Exception e)
                {
                    System.out.println("Errore nella chiusura della connessione: " + e);
                }
            }
        }
       return v;
    }
}