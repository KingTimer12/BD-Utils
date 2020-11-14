package br.com.timer.bdutils.types;

import br.com.timer.bdutils.ConnectionManager;
import br.com.timer.bdutils.models.Params;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AsyncConnectionBase {

    private ConnectionBase base;
    private String table;

    public AsyncConnectionBase(ConnectionBase base) {
        table = ConnectionManager.getInstance().getTable();
        this.base = base;
    }

    public void insert(List<Params> insertValues, Consumer<Throwable> error) {
        CompletableFuture.runAsync(() -> base.insert(insertValues)).whenComplete((aVoid, throwable) -> error.accept(throwable));
    }
    public void delete(List<Params> whereParams, Consumer<Throwable> error) {
        CompletableFuture.runAsync(() -> base.delete(whereParams)).whenComplete((aVoid, throwable) -> error.accept(throwable));
    }
    public void update(List<Params> paramsList, List<Params> whereParams, Consumer<Throwable> error) {
        CompletableFuture.runAsync(() -> base.update(paramsList,whereParams)).whenComplete((aVoid, throwable) -> error.accept(throwable));
    }


}
