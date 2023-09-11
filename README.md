# London Underground Route Finder

## Overview

This JavaFX application helps travelers find routes between stations on the London Underground. It offers various route-finding algorithms, allowing users to search for routes based on different criteria such as fewest stops, shortest distance and customized penalties for line changes.

## Features

- Find multiple route permutations using Depth-First Search (DFS).
- Discover the route with the fewest stations using Breadth-First Search (BFS).
- Calculate the shortest route based on distance using Dijkstra’s algorithm.
- Consider user-specified cost penalties for line/train changes.
- Geographical map representation of routes within Zone 1.
- User-friendly JavaFX GUI with readable route displays.

## Implementation Notes

- This is a team project with teams consisting of 3 students.
- Use custom graph-based data structures to represent stations and their connections.
- Utilize Dijkstra’s algorithm for route calculation based on distance.
- Allow users to set penalties for line/train changes.
- Implement a graphical user interface (JavaFX) for displaying routes.
- Include a geographical map for Zone 1 routes.
- Use latitude and longitude to calculate Euclidean distances between stations.
- Utilize CSV files: one for the map data and one for route information.

- ## Setting up

To run the London Underground Route Finder application:

   ```bash
   git clone https://github.com/alexxfan/LondonUnderground.git
   cd LondonUnderground
   java -jar target/LondonUnderground.jar
   

