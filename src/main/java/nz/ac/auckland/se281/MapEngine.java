package nz.ac.auckland.se281;

import java.util.*;

public class MapEngine {
  private Map<Country, List<Country>> adjNodes;
  private Map<String, Country> countryMap;

  public MapEngine() {
    this.adjNodes = new HashMap<>();
    this.countryMap = new HashMap<>();
    loadMap();
  }

  private void loadMap() {

    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();

    // reads information from provided document and creates country class using the provided country
    // and its details.
    for (String line : countries) {
      String[] countryParts = line.split(",");
      Country country = new Country(countryParts[0], countryParts[1], countryParts[2]);
      // creates a map to relate the countries name and its class for later use so that a countries
      // object can be identified using its String name.
      countryMap.put(countryParts[0], country);
      adjNodes.putIfAbsent(country, new ArrayList<>());
    }

    // Places a countries neighbouring country objects in a arrayList within the adjNodes hashmap,
    // using the country as the key.
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
    // reads the user input for country and continually prompts the user until a valid country is
    // provided.
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

  public void showInfoCountry() {
    // when a valid country is provided it prints the countries name, continent its in, fuel cost
    // and its neighbouring countries.
    MessageCli.INSERT_COUNTRY.printMessage();
    Country country = validateCountry();
    MessageCli.COUNTRY_INFO.printMessage(
        country.getName(),
        country.getContinent(),
        String.valueOf(country.getFuelCost()),
        adjNodes.get(country).toString());
  }

  public void showRoute() {
    // prints the route between the inputs root and destination
    MessageCli.INSERT_SOURCE.printMessage();
    Country source = validateCountry();
    MessageCli.INSERT_DESTINATION.printMessage();
    Country destination = validateCountry();
    // determines if root and destination are the same country.
    if (source.equals(destination)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }
    List<Country> path = findShortestPath(source, destination);
    Set<Continent> continentsVisitedSet = continentsVisited(path);
    MessageCli.ROUTE_INFO.printMessage(path.toString());
    MessageCli.FUEL_INFO.printMessage(String.valueOf(totalFuel(path)));
    MessageCli.CONTINENT_INFO.printMessage(continentsVisitedSet.toString());
    MessageCli.FUEL_CONTINENT_INFO.printMessage(mostFuelContinent(continentsVisitedSet).toString());
  }

  public List<Country> findShortestPath(Country source, Country destination) {
    List<Country> visited = new ArrayList<>();
    Queue<Country> queue = new LinkedList<>();
    Map<Country, Country> previous = new HashMap<>();
    // searches through graph using BFS
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
          // Everytime a new neighbouring node is found, it and its parent node (the current node)
          // is put into a hashmap to later find trace back the shortest path from the destination
          // node.
          previous.put(adjacent, current);
        }
      }
    }

    List<Country> path = new ArrayList<>();
    Country step = destination;
    // using the "previous" hashmap we can trace down the shortest path backwards starting from the
    // destination node. The using the destination node, it provides its parent and using this
    // parent in the next iteration we can find parents parent node, this continues until the root node is found.
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
    // adds and maintains the order of the continents that were traveled through in the journey,
    // while also calculating the total fuel cost.
    Set<Continent> visited = new LinkedHashSet<>();
    visited.add(new Continent(path.getFirst().getContinent()));

    for (int i = 1; i < path.size() - 1; i++) {
      Continent continent = new Continent(path.get(i).getContinent());
      visited.add(continent);

      // if country we are in is within a continent that we are or already have traveled through
      // then its fuel cost is added to its respective continents sum.
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

    // iterates through all the continents that have been traveled through and returns the continent
    // with the highest fuel cost.
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
