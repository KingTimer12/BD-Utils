package br.com.timer.bdutils.types;

import br.com.timer.bdutils.BDMain;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends ConnectionBase {

    private int query = 0;

    @Override
    public void openConnection() {
        try {
            query++;
            if ((connection != null) && (!connection.isClosed()))
                return;

            Class.forName("org.sqlite.JDBC");
            File dataFolder = new File(BDMain.getInstance().getDataFolder(), "playerstorage.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch( SQLException | ClassNotFoundException e) {
            query--;
            e.getStackTrace();
            e.printStackTrace();
            System.out.println(
                    "Erro ao abrir conexao com SQLite!");
        }
    }

    @Override
    public void closeConnection() {
        query--;
        if (query <= 0) {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (Exception e) {
                System.out.println(
                        "Erro ao abrir conexao com SQLite!");
            }
        }
    }
}
