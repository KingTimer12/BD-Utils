package br.com.timer.bdutils;

import br.com.timer.bdutils.types.ConnectionBase;
import br.com.timer.bdutils.types.MariaDB;
import br.com.timer.bdutils.types.MongoDB;
import br.com.timer.bdutils.types.MySQL;
import br.com.timer.bdutils.types.SQLite;
import lombok.Getter;
import lombok.val;

import java.sql.SQLException;

@Getter
public class ConnectionManager {

    @Getter
    private static ConnectionManager instance = new ConnectionManager();

    private ConnectionBase connectionBase;

    private String table;
    private String[] columns;
    private DBType type;

    public void setup(DBType type, String table, String... columns) {
        this.type = type;
        this.table = table;
        this.columns = columns;
        connectionBase = getMainConnection();
        createTable(table, columns);
    }

    private ConnectionBase getMainConnection() {
        if (type.equals(DBType.MYSQL)) {
            return new MySQL("", 0000, "", "", "", 0);
        }
        if (type.equals(DBType.SQLITE)) {
            return new SQLite();
        }
        if (type.equals(DBType.MARIADB)) {
            //Host with port
            //Example: localhost:3306
            return new MariaDB("", "", "", "", 0);
        }
        if (type.equals(DBType.MONGODB)) {
            //URI, database, collectionName(tableName)
            return new MongoDB("", "", table);
        }

        return null;
    }

    private void createTable(String table, String... columns) {
        connectionBase.openConnection();
        try {
            val connection = connectionBase.getConnection();
            if ((connectionBase.getConnection() != null) && (!connection.isClosed())) {
                val statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (" + String.join(",", columns) + ");");
            }
        } catch (SQLException exception) {
            System.out.println("Erro ao conectar com o banco MySQL");
        } finally {
            if (connectionBase.getConnection() != null)
                connectionBase.closeConnection();
        }
    }

    public enum DBType {
        MYSQL, SQLITE, MARIADB, MONGODB
    }

}
