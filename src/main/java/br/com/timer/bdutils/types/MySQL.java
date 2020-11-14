package br.com.timer.bdutils.types;

import lombok.AllArgsConstructor;

import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class MySQL extends ConnectionBase {

    private String host;
    private int port;
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

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        } catch (ClassNotFoundException | SQLException exception) {
            query--;
            System.out.println("Erro ao abrir a conexao MySQL!");
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
                        "Erro ao fechar conexao MySQL!");
            }
        }
    }
}
