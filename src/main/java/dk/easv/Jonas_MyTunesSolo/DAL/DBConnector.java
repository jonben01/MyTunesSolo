package dk.easv.Jonas_MyTunesSolo.DAL;
//LIBRARY IMPORTS
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
//JAVA IMPORTS
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DBConnector {

    private static final String PROPERTY_FILE = "config/database.settings";
    private SQLServerDataSource dataSource;

    public DBConnector() throws IOException {

        Properties dbProps = new Properties();
        dbProps.load(new FileInputStream(PROPERTY_FILE));

        dataSource = new SQLServerDataSource();
        dataSource.setServerName(dbProps.getProperty("Server"));
        dataSource.setDatabaseName(dbProps.getProperty("Database"));
        dataSource.setUser(dbProps.getProperty("User"));
        dataSource.setPassword(dbProps.getProperty("Password"));
        dataSource.setPortNumber(1433);
        dataSource.setTrustServerCertificate(true);

    }
    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }
}
