package it.bz.idm.bdp.spreadsheets;

public interface DataFetcher {

	public abstract void authenticate();
	public abstract Object fetchSheet();
}
