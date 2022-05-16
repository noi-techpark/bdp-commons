package info.datatellers.appatn.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 *         This helper class handles every csv-related action. Every parsing
 *         action is
 *         done dynamically, that is, should the number of lines inside the csv
 *         files
 *         change, it will be handled automatically.
 */
public class CSVHandler {
	private final ResourceBundle rb = ResourceBundle.getBundle("config");
	private static final Logger LOG = LoggerFactory.getLogger(CSVHandler.class.getName());

	public CSVHandler() {
	}

	CloseableHttpClient client = HttpClientBuilder.create().build();

	/**
	 * This method parses, splits, and structures the csv content regarding
	 * station metadata. This is done dynamically: should new stations be
	 * added to the said csv file, the mapping will be correctly handled.
	 *
	 * @return An HashMap, containing the stations
	 *         IDs as keys and an ArrayList containing every split String
	 *         parsed from the csv file, that is, the station metadata.
	 */
	public HashMap<String, ArrayList<String>> parseStationsCSV() {
		ArrayList<String> tokens = new ArrayList<>();
		InputStream stream = getResourceAsInputStream(rb.getString("odh.station.metadata.csv"));
		Scanner scanner = new Scanner(stream);
		LOG.info("Scanner initialized. Starting to scan provided CSV file containing stations metadata...");
		while (scanner != null && scanner.hasNext()) {
			LOG.debug("Scanning lines...");
			String scanned = scanner.nextLine();
			tokens.add((scanned.replace("[", "").replace("]", "")));
		}
		tokens.remove(0);
		LOG.debug("Scanning completed.");
		scanner.close();
		LOG.debug(
				"Pairing process between provided CSV containing stations metadata and retrieved JSON data started...");
		HashMap<String, ArrayList<String>> stationsMetadata = new HashMap<>();
		ArrayList<String> stationIdentifiers;
		for (String token : tokens) {
			String[] splitTokens = token.split(",");
			stationIdentifiers = new ArrayList<>(Arrays.asList(splitTokens));
			stationsMetadata.put(stationIdentifiers.get(0), stationIdentifiers);
		}
		LOG.debug("Pairing process completed.");
		return stationsMetadata;
	}

	/**
	 * This method parses, splits, and structures the csv content regarding
	 * polluters metadata. This is done dynamically: should new polluters be
	 * added to the said csv file, the mapping will be correctly handled.
	 *
	 * @return An HashMap, containing the polluters acronym as keys and
	 *         an ArrayList containing every split String parsed from the csv file,
	 *         that is, the polluter metadata.
	 */
	public ArrayList<String> parseTypesCSV() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = null;
		LOG.info("Scanner initialized. Starting to scan provided CSV file that contains types metadata.");
		InputStream stream = getResourceAsInputStream(rb.getString("odh.types.metadata.csv"));
		scanner = new Scanner(stream);
		scanner.nextLine();
		int pollutersIDsSize = getPollutersIDsSize();
		for (int index = 0; index < pollutersIDsSize; index++) {
			LOG.debug("Scanning lines...");
			String scanned = "";
			if (scanner != null) {
				scanned = scanner.nextLine();
			}
			tokens.add(scanned);
		}
		LOG.debug("Scanning completed. Returning as array...");
		scanner.close();
		return tokens;
	}

	/**
	 * This method dynamically parses the station-related csv file.
	 *
	 * @return An ArrayList containing only the stations IDs.
	 */
	public ArrayList<String> getStationsIDs() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = getStationsMetadataScanner();
		scanTokens(tokens, scanner);
		return tokens;
	}

	/**
	 * This method parses the station-related csv file, it is used to guarantee
	 * dynamic retrieval from the endpoint.
	 *
	 * @return An int containing only the size, that is the number of stations
	 *         described inside the said file.
	 */
	public int getStationsIDsSize() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = getStationsMetadataScanner();
		return getTokensSize(tokens, scanner);
	}

	/**
	 * Initialize a scanner and configures it for stations metadata reading.
	 *
	 * @return The said configured scanner.
	 */
	private Scanner getStationsMetadataScanner() {
		Scanner scanner = null;
		InputStream stream = getResourceAsInputStream(rb.getString("odh.station.metadata.csv"));
		scanner = new Scanner(stream);
		LOG.debug(
				"Scanner initialized. Starting to scan provided CSV file containing stations metadata inside getStationsIDsSize()");
		return scanner;
	}

	private InputStream getResourceAsInputStream(String path) {
		File resource = new File(path);
		try {
			return new FileInputStream(resource);
		} catch (FileNotFoundException e) {
			LOG.error("File not found: " + path);
		}
		return null;
	}

	/**
	 * Initialize a scanner and configures it for polluters metadata reading.
	 *
	 * @return The said configured scanner.
	 */
	private Scanner getPollutersMetadataScanner() {
		Scanner scanner = null;
		InputStream stream = getResourceAsInputStream(rb.getString("odh.types.metadata.csv"));
		scanner = new Scanner(stream);
		LOG.debug(
				"Scanner initialized. Starting to scan provided CSV file containing types metadata inside getPollutersIDsSize()");

		return scanner;
	}

	/**
	 * This method parses the station-related csv file, it is used to guarantee
	 * dynamic retrieval from the endpoint.
	 *
	 * @return An ArrayList<String> containing polluters names.
	 */
	public ArrayList<String> getPollutersNames() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = getPollutersMetadataScanner();

		while (scanner != null && scanner.hasNext()) {
			LOG.debug("Scanning lines...");
			String scanned = scanner.nextLine();
			tokens.add(Arrays.toString(scanned.split(",")).split(",")[1].replace("[", "").trim());
		}
		LOG.debug("Scanning completed. Returning as array...");
		tokens.remove(0);
		return tokens;
	}

	/**
	 * This method parses the station-related csv file, it is used to guarantee
	 * dynamic retrieval from the endpoint.
	 *
	 * @return An ArrayList<String> containing polluters acronyms.
	 */
	public ArrayList<String> getPollutersAcronyms() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = getPollutersMetadataScanner();

		while (scanner != null && scanner.hasNext()) {
			LOG.debug("Scanning lines...");
			String scanned = scanner.nextLine();
			tokens.add(Arrays.toString(scanned.split(",")).split(",")[2].replace("[", "").trim());
		}
		LOG.debug("Scanning completed. Returning as array...");
		tokens.remove(0);
		return tokens;
	}

	/**
	 * This method parses the polluters-related csv file, it is used to guarantee
	 * dynamic retrieval from the endpoint.
	 *
	 * @return An int containing only the size, that is the number of polluters
	 *         described inside the said file.
	 */
	private int getPollutersIDsSize() {
		ArrayList<String> tokens = new ArrayList<>();
		Scanner scanner = getPollutersMetadataScanner();
		return getTokensSize(tokens, scanner);
	}

	/**
	 * See documentation of the methods called inside this one.
	 *
	 * @param tokens  ArrayList of Strings.
	 * @param scanner A configured scanner.
	 * @return integer representing the number of polluters.
	 */
	private int getTokensSize(ArrayList<String> tokens, Scanner scanner) {
		scanTokens(tokens, scanner);
		return tokens.size();
	}

	/**
	 * Scans wanted data and puts them inside the inputted ArrayList.
	 *
	 * @param tokens  ArrayList of Strings.
	 * @param scanner A configured scanner.
	 */
	private void scanTokens(ArrayList<String> tokens, Scanner scanner) {
		while (scanner != null && scanner.hasNext()) {
			LOG.debug("Scanning lines...");
			String scanned = scanner.nextLine();
			tokens.add(Arrays.toString(scanned.split(",")).split(",")[0].replace("[", ""));
		}
		LOG.debug("Scanning completed. Returning as array...");
		tokens.remove(0);
	}
}
