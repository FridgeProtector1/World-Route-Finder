# World Route Finder (University Project)

A graph-based Java program that simulates optimal international delivery routing using the map from the classic strategy board game Risk!.

## Features
- Models a simplified world map with 42 countries across 6 continents as graph nodes.
- Uses Breadth-First Search (BFS) to find the shortest path between two countries based on the number of countries visited.
- Calculates total fuel cost using only the intermediate countries in the path.
- Simulates real-world constraints for route optimization in logistics and delivery systems.

## How It Works
- The world map is stored as an undirected graph with countries as vertices and land/sea connections as edges.
- A start and destination country are selected by the user.
- The program returns the shortest path and the total fuel cost for that route.

## Technologies Used
- Java
- Graph data structures
- Breadth-First Search (BFS)

## Use Case
Designed as a university assignment to simulate real-world delivery routing problems using graph traversal and optimization logic.
