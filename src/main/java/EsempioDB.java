import java.sql.*;
public class EsempioDB {
     public static void main(String[] args)
    {
        persona p = new persona();
        //carico il driver
        String DRIVER = "com.mysql.jdbc.Driver";
        try
        {
            Class.forName(DRIVER);
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Driver non trovato" + e);
            System.exit(1);
        }
        //nome e indirizzo database
        String URL_miodb = "jdbc:mysql://localhost:3306/provejava";
        //definizione delle query
        String query = "SELECT nome, cognome FROM persona";
        //stabilisco la connessione
        System.out.println("Connessione con: " + URL_miodb);
        Connection connessione = null;
        try
        {
            connessione = DriverManager.getConnection(URL_miodb, "root", "");
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
            while(resultset.next())
            {
                p.nome = resultset.getString(1);
                p.cognome = resultset.getString(2);
                System.out.println("INFORMAZIONI");
                System.out.println("Cognome: " + p.getCognome());
                System.out.println("Nome :" + p.getNome());
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
    }
}

