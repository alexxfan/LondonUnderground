package com.example.londonunderground.models;

public class Link { // edge in a graph

    public GraphNode<?> destinationNode; // represents the node that the edge is directed towards
    public int cost;

    public Link(GraphNode<?> destNode, int cost) {
        this.destinationNode=destNode;
        this.cost=cost;
    }
}
