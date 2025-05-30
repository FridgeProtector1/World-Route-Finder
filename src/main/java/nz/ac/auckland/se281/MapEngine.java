package nz.ac.auckland.se281;

import java.util.*;

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
    Country source = validateCountry();
    MessageCli.INSERT_DESTINATION.printMessage();
    Country destination = validateCountry();
    if (source.equals(destination)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }
    List<Country> path = shortestPath(source, destination);
    Set<Continent> continentsVisitedSet = continentsVisited(path);
    MessageCli.ROUTE_INFO.printMessage(path.toString());
    MessageCli.FUEL_INFO.printMessage(String.valueOf(totalFuel(path)));
    MessageCli.CONTINENT_INFO.printMessage(continentsVisitedSet.toString());
    MessageCli.FUEL_CONTINENT_INFO.printMessage(mostFuelContinent(continentsVisitedSet).toString());
  }

  public List<Country> shortestPath(Country source, Country destination) {
    List<Country> visited = new ArrayList<>();
    Queue<Country> queue = new LinkedList<>();
    Map<Country, Country> previous = new HashMap<>();
    queue.add(source);
    visited.add(source);
    while (!queue.isEmpty()) {
      Country current = queue.poll();

      if (current.equals(destination)) {
        break;
      }
      for (Country adjacent : adjNodes.get(current)) {
        if (!visited.contains(adjacent)) {
          queue.add(adjacent);
          visited.add(adjacent);
          previous.put(adjacent, current);
        }
      }
    }

    List<Country> path = new ArrayList<>();
    Country step = destination;
    while (step != source) {
      path.add(step);
      step = previous.get(step);
    }
    path.add(source);
    Collections.reverse(path);
    return path;
  }

  public int totalFuel(List<Country> path) {
    int totalFuel = 0;
    for (int i = 1; i < path.size() - 1; i++) {
      totalFuel += path.get(i).getFuelCost();
    }
    return totalFuel;
  }

  public Set<Continent> continentsVisited(List<Country> path) {
    Set<Continent> visited = new LinkedHashSet<>();
    visited.add(new Continent(path.getFirst().getContinent()));

    for (int i = 1; i < path.size() - 1; i++) {
      Continent continent = new Continent(path.get(i).getContinent());
      visited.add(continent);
      if (visited.contains(continent)) {
        for (Continent vistedContinent : visited) {
          if (vistedContinent.equals(continent)) {
            vistedContinent.addFuel(path.get(i).getFuelCost());
          }
        }
      }
    }
    visited.add(new Continent(path.getLast().getContinent()));
    return visited;
  }

  public Continent mostFuelContinent(Set<Continent> continents) {
    int mostFuel = -1;
    Continent mostFuelContinent = null;
    for (Continent continent : continents) {
      if (mostFuel < continent.getFuelCost()) {
        mostFuel = continent.getFuelCost();
        mostFuelContinent = continent;
      }
    }
    return mostFuelContinent;
  }
}
