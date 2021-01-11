package br.com.timer.bdutils.types;

import br.com.infernalia.flat.Flat;
import br.com.infernalia.flat.impl.FlatArrayList;
import br.com.timer.bdutils.ConnectionManager;
import br.com.timer.bdutils.models.Params;
import br.com.timer.bdutils.models.ResultSetContents;

import com.mongodb.MongoClient;
import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ConnectionBase {

    protected String table;
    private String[] columns;

    public ConnectionBase() {
        table = ConnectionManager.getInstance().getTable();
        columns = ConnectionManager.getInstance().getColumns();
    }

    @Getter
    Connection connection;
    MongoClient client;

    abstract public void openConnection();

    abstract public void closeConnection();
    
    public abstract void insert(List<Params> insertValues);
//        //Mapeando as chaves(palavras-chaves) do parametro
//        val paramKeys = insertValues.stream().map(Params::getKey).collect(Collectors.toList());
//
//        //Mapear os valores do parametro
//        //Se for nulo irá setar o valor como "=?"
//        val paramValues = insertValues.stream().map(Params::getValue).map(t -> t = "=?").collect(Collectors.toList());
//
//        //Separando as chaves e os valores
//        val paramKeysSplit = String.join(",", paramKeys);
//        val paramValuesSplit = String.join(",", String.valueOf(paramValues));
//
//        val stmt = "INSERT INTO " + table + "" +
//                " (" + paramKeysSplit + ") VALUES (" + paramValuesSplit + ")";
//        executeAction(stmt, insertValues, null);

    public void delete(List<Params> whereParams) {
    }
    public void delete(Params found) {
    }
//        val paramsWhere = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());
//
//        val stmt = "DELETE FROM " + table + " WHERE " + paramsWhere;
//        executeAction(stmt, whereParams, null);

    public void update(List<Params> paramsList, List<Params> whereParams) {
    }
    public void update(Params found, List<Params> updateValues) {
    }
//        val whereParamsKeys = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());
//        val paramsListKeys = paramsList.stream().map(Params::toStringEncoded).collect(Collectors.toList());
//
//        val paramsListSplit = String.join(",", paramsListKeys);
//        val whereParamsSplit = String.join(",", whereParamsKeys);
//
//        String stmt = "UPDATE " + table + " SET " + paramsListSplit + " WHERE " + whereParamsSplit;
//        executeAction(stmt, paramsList, whereParams);
    
    public abstract Optional<ResultSetContents> select(List<Params> paramsList);
//        val paramListKeys = paramsList.stream().map(Params::toStringEncoded).collect(Collectors.toList());
//        val paramListKeysFiltered = String.join(",", paramListKeys);
//
//        String statment = "SELECT * FROM " + table + " WHERE " + paramListKeysFiltered;
//        return executeQuery(statment, paramsList);

    protected void executeAction(String statment, List<Params> paramsList, @Nullable List<Params> conditions) {
        val mainConnection = ConnectionManager.getInstance().getConnectionBase();
        mainConnection.openConnection();
        try (PreparedStatement preparedStatement = mainConnection.getConnection().prepareStatement(statment)) {
            convertValue(preparedStatement, paramsList, conditions);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            mainConnection.closeConnection();
        }
    }
    
    protected Optional<ResultSetContents> executeQuery(String statment, List<Params> paramsList) {
        final val mainConnection = ConnectionManager.getInstance().getConnectionBase();
        mainConnection.openConnection();
        try (PreparedStatement ps = mainConnection.getConnection().prepareStatement(statment); val resultSet = ps.executeQuery();) {
            convertValue(ps, paramsList, null);
            if (resultSet.next()) {
                Flat<Params> dataParams = new FlatArrayList<>();
                for (String column : columns) {
                    String columnRefactored = column.split(" ")[0];
                    final val resultData = resultSet.getString(columnRefactored);
                    dataParams.add(new Params(columnRefactored, resultData));
                }
                return Optional.of(new ResultSetContents(dataParams, true));
            } else return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return Optional.empty();
    }

    protected void convertValue(PreparedStatement ps, List<Params> paramsList, @Nullable List<Params> conditions) {
        paramsList.forEach(params -> {
            val i = paramsList.indexOf(params) + 1;
            try {
                ps.setObject(i, params.getValue());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        if (conditions != null) {
            val lastValue = paramsList.size();
            conditions.forEach(conParams -> {
                val i = lastValue + 1;
                try {
                    ps.setObject(i, conParams.getValue());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        }
    }

    public AsyncConnectionBase async() {
        return new AsyncConnectionBase(this);
    }

}
