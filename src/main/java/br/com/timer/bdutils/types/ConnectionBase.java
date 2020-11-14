package br.com.timer.bdutils.types;

import br.com.timer.bdutils.ConnectionManager;
import br.com.timer.bdutils.models.Params;
import com.sun.istack.internal.Nullable;
import lombok.Getter;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ConnectionBase {

    private String table;
    private String[] columns;

    public ConnectionBase() {
        table = ConnectionManager.getInstance().getTable();
        columns = ConnectionManager.getInstance().getColumns();
    }

    @Getter
    Connection connection;

    abstract public void openConnection();

    abstract public void closeConnection();

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

    public void delete(List<Params> whereParams) {
        val paramsWhere = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());

        val stmt = "DELETE FROM " + table + " WHERE " + paramsWhere;
        executeAction(stmt, whereParams, null);
    }

    public void update(List<Params> paramsList, List<Params> whereParams) {
        val whereParamsKeys = whereParams.stream().map(Params::toStringEncoded).collect(Collectors.toList());
        val paramsListKeys = paramsList.stream().map(Params::toStringEncoded).collect(Collectors.toList());

        val paramsListSplit = String.join(",", paramsListKeys);
        val whereParamsSplit = String.join(",", whereParamsKeys);

        String stmt = "UPDATE " + table + " SET " + paramsListSplit + " WHERE " + whereParamsSplit;
        executeAction(stmt, paramsList, whereParams);
    }

    private void executeAction(String statment, List<Params> paramsList, @Nullable List<Params> conditions) {
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

    private void convertValue(PreparedStatement ps, List<Params> paramsList, @Nullable List<Params> conditions) {
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
