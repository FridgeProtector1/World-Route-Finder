package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class is the main entry point. */
public class MapEngine {
  private Map<Country, List<Country>> adjNodes;
  private Map<String, Country> countryMap;

  public MapEngine() {
    // add other code here if you wan
    this.adjNodes = new HashMap<>();
    this.countryMap = new HashMap<>();
    loadMap(); // keep this mehtod invocation
  }

  /** invoked one time only when constructing the MapEngine class. */
  private void loadMap() {

    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();

    for (String line : countries) {
      String[] countryParts = line.split(",");
      Country country = new Country(countryParts[0], countryParts[1], countryParts[2]);
      countryMap.put(countryParts[0], country);
      adjNodes.putIfAbsent(country, new ArrayList<>());
    }

    for (String line : adjacencies) {
      String[] adjParts = line.split(",");
      Country country = countryMap.get(adjParts[0]);

      for (int i = 1; i < adjParts.length; i++) {
        Country neighbour = countryMap.get(adjParts[i]);
        adjNodes.get(country).add(neighbour);
      }
    }
  }

  public Country getCountryByName(String countryName) throws InvalidCountryException {
    countryName = Utils.capitalizeFirstLetterOfEachWord(countryName);
    if (countryMap.containsKey(countryName)) {
      return countryMap.get(countryName);
    }
    throw new InvalidCountryException(countryName);
  }

  public Country validateCountry() {
    Country country;
    while (true) {
      String countryName = Utils.scanner.nextLine();
      try {
        country = getCountryByName(countryName);
        return country;
      } catch (InvalidCountryException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    MessageCli.INSERT_COUNTRY.printMessage();
    Country country = validateCountry();
    MessageCli.COUNTRY_INFO.printMessage(
        country.getName(),
        country.getContinent(),
        String.valueOf(country.getFuelCost()),
        adjNodes.get(country).toString());
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {
    MessageCli.INSERT_SOURCE.printMessage();
    Country root = validateCountry();
    MessageCli.INSERT_DESTINATION.printMessage();
    Country destination = validateCountry();
    if (root.equals(destination)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
    }
  }
}
