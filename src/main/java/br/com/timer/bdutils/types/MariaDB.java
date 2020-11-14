package br.com.timer.bdutils.types;

import lombok.AllArgsConstructor;

import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class MariaDB extends ConnectionBase {

    private String host;
    private String username;
    private String password;
    private String database;

    private int query = 0;

    @Override
    public void openConnection() {
        try {
            query++;
            if ((connection != null) && (!connection.isClosed()))
                return;

            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://"+host+"/" + database, username, password);
        } catch (ClassNotFoundException | SQLException exception) {
            query--;
            System.out.println("Erro ao abrir a conexao com MariaDB!");
            exception.printStackTrace();
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
