package br.com.timer.bdutils.types;

import lombok.AllArgsConstructor;
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.timer.bdutils.models.Params;
import br.com.timer.bdutils.models.ResultSetContents;

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
    
    @Override
    public void insert(List<Params> insertValues) {
    	//Mapeando as chaves(palavras-chaves) do parametro
        val paramKeys = insertValues.stream().map(Params::getKey).collect(Collectors.toList());

        //Mapear os valores do parametro
        //Se for nulo irá setar o valor como "=?"
        val paramValues = insertValues.stream().map(Params::getValue).map(t -> t = "=?").collect(Collectors.toList());

        //Separando as chaves e os valores
        val paramKeysSplit = String.join(",", paramKeys);
        val paramValuesSplit = String.join(",", String.valueOf(paramValues));

        val stmt = "INSERT INTO " + table + "" +
                " (" + paramKeysSplit + ") VALUES (" + paramValuesSplit + ")";
        executeAction(stmt, insertValues, null);
    }
    
    @Override
    public void delete(List<Params> whereParams) {
    	val paramsWhere = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());

        val stmt = "DELETE FROM " + table + " WHERE " + paramsWhere;
        executeAction(stmt, whereParams, null);
    }
    
    @Override
    public void update(List<Params> paramsList, List<Params> whereParams) {
    	val whereParamsKeys = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());
        val paramsListKeys = paramsList.stream().map(Params::toStringEncoded).collect(Collectors.toList());

        val paramsListSplit = String.join(",", paramsListKeys);
        val whereParamsSplit = String.join(",", whereParamsKeys);

        String stmt = "UPDATE " + table + " SET " + paramsListSplit + " WHERE " + whereParamsSplit;
        executeAction(stmt, paramsList, whereParams);
    }
    
    @Override
    public Optional<ResultSetContents> select(List<Params> paramsList) {
    	 val paramListKeys = paramsList.stream().map(Params::toStringEncoded).collect(Collectors.toList());
         val paramListKeysFiltered = String.join(",", paramListKeys);

         String statment = "SELECT * FROM " + table + " WHERE " + paramListKeysFiltered;
         return executeQuery(statment, paramsList);
    }
}
