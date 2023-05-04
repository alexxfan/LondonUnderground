package com.example.londonunderground.models;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T>{ // vertex in a graph, taken from lecture notes: Graphs, slide 65

    public int node = Integer.MAX_VALUE;
    public T data;

    public List<Link> adjacencyList = new ArrayList<>();
    // represents where each vertex in the graph is associated with a list of adjacent vertices.

    public GraphNode(T data) {
        this.data=data;
    }

    public void connectToNodeDirected(GraphNode<T> destinationNode, int cost) {
        adjacencyList.add( new Link(destinationNode,cost) ); // adds a new link object to source adjacency list
    }
    public void connectToNodeUndirected(GraphNode<T> destinationNode, int cost) {
        adjacencyList.add( new Link(destinationNode,cost) ); //adds a new link object to source adjacency list
        destinationNode.adjacencyList.add( new Link(this,cost) ); //Add new link object to destination adjacency list
    }
}
