package nz.ac.auckland.se281;

public class Country {
  private String name;
  private String continent;
  private int fuelCost;

  public Country(String name, String continent, String fuelCost) {
    this.name = name;
    this.continent = continent;
    this.fuelCost = Integer.parseInt(fuelCost);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    Country country = (Country) obj;
    return name.equals(country.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
