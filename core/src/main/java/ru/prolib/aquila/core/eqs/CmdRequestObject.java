package ru.prolib.aquila.core.eqs;

import java.util.concurrent.CompletableFuture;

public abstract class CmdRequestObject<ResultType> extends Cmd {
	protected final CompletableFuture<ResultType> result;

	public CmdRequestObject(CmdType type, CompletableFuture<ResultType> result) {
		super(type);
		this.result = result;
	}
	
	public CmdRequestObject(CmdType type) {
		this(type, new CompletableFuture<>());
	}
	
	public CompletableFuture<ResultType> getResult() {
		return result;
	}

}
