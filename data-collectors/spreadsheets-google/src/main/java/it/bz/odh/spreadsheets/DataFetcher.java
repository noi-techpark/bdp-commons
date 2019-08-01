package it.bz.odh.spreadsheets;

public interface DataFetcher {

	public abstract void authenticate();
	public abstract Object fetchSheet();
}
