package nz.ac.auckland.se281;

public class Continent {
  private String name;
  private int fuelCost = 0;

  public Continent(String name) {
    this.name = name;
  }

  public void addFuel(int fuelCost) {
    this.fuelCost += fuelCost;
  }

  public int getFuelCost() {
    return this.fuelCost;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    Continent continent = (Continent) obj;
    return name.equals(continent.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name + " (" + fuelCost + ")";
  }
}
