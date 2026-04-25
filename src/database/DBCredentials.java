package database;

import java.io.Serializable;

public class DBCredentials implements Serializable {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;
    private final String serverAddress;

    public DBCredentials(String dbUrl, String dbUser, String dbPass, String serverAddress) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.serverAddress = serverAddress;
    }

    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPass() { return dbPass; }
    public String getServerAddress() { return serverAddress; }
}