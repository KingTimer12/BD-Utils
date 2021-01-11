package br.com.timer.bdutils.types;

import br.com.timer.bdutils.ConnectionManager;
import br.com.timer.bdutils.models.Params;
import br.com.timer.bdutils.models.ResultSetContents;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AsyncConnectionBase {

	private ConnectionBase base;
	@SuppressWarnings("unused")
	private String table;

	public AsyncConnectionBase(ConnectionBase base) {
		this.table = ConnectionManager.getInstance().getTable();
		this.base = base;
	}

	public void insert(List<Params> insertValues, Consumer<Throwable> error) {
		CompletableFuture.runAsync(() -> base.insert(insertValues))
				.whenComplete((aVoid, throwable) -> error.accept(throwable));
	}
	
	public void delete(Params found, Consumer<Throwable> error) {
		CompletableFuture.runAsync(() -> base.delete(found))
				.whenComplete((aVoid, throwable) -> error.accept(throwable));
	}

	public void delete(List<Params> whereParams, Consumer<Throwable> error) {
		CompletableFuture.runAsync(() -> base.delete(whereParams))
				.whenComplete((aVoid, throwable) -> error.accept(throwable));
	}
	
	public void update(Params found, List<Params> whereParams, Consumer<Throwable> error) {
		CompletableFuture.runAsync(() -> base.update(found, whereParams))
				.whenComplete((aVoid, throwable) -> error.accept(throwable));
	}

	public void update(List<Params> paramsList, List<Params> whereParams, Consumer<Throwable> error) {
		CompletableFuture.runAsync(() -> base.update(paramsList, whereParams))
				.whenComplete((aVoid, throwable) -> error.accept(throwable));
	}

	public void select(List<Params> paramsList, Consumer<Optional<ResultSetContents>> consumer) {
		CompletableFuture.supplyAsync(() -> base.select(paramsList))
				.whenComplete((resultSetContents, throwable) -> consumer.accept(resultSetContents));
	}

}
