package br.com.timer.bdutils;

import br.com.timer.bdutils.types.ConnectionBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.sql.Statement;

@Getter
@AllArgsConstructor
public class ConnectionManager {

    @Getter
    private static ConnectionManager instance;

    public ConnectionManager() {
        this.instance = this;
    }

    private ConnectionBase connectionBase;

    private String table;
    private String[] columns;

    public void setup() {
        connectionBase = getMainConnection();

    }

    private ConnectionBase getMainConnection() {
        //Criar configurações e diferenças entre SQLite e MySQL
        return null;
    }

    private void createTable(String table, String columns) {
        connectionBase.openConnection();
        try {
            val connection = connectionBase.getConnection();
            if ((connectionBase.getConnection() != null) && (!connection.isClosed())) {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (" + String.join(",", columns) + ");");
            }
        } catch (SQLException exception) {
            System.out.println("Erro ao conectar com o banco MySQL");
        } finally {
            if (connectionBase.getConnection() != null)
                connectionBase.closeConnection();
        }
    }

}
