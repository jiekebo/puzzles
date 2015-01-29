package info.jiekebo.spotify;

import java.util.*;

/**
 * Attempt at implementing hopcroft-karp (from wikipedia pseudocode), using object references
 * instead of direct array indices.
 */
public class OldHopcroftKarp {
    private BiGraph graph;
    private final int INF = Integer.MAX_VALUE;
    private Map<Vertex, Vertex> utov = new HashMap<Vertex, Vertex>();
    private Map<Integer, Set<Vertex>> layer = new HashMap<Integer, Set<Vertex>>();

    public OldHopcroftKarp(BiGraph graph) {
        this.graph = graph;
    }

    // Uses breadth-first search to partition bipartite graph into layers
    private boolean makeLayers() {
        Queue<Vertex> queue = new LinkedList<Vertex>();
        for (int i = 0; i < graph.getVertices().size(); i++) {
            if (utov.get(graph.getVertices().get(i)) == null) {
                Set<Vertex> vertices = layer.get(i+1);
                if (vertices == null) {
                    vertices = new HashSet<Vertex>();
                    layer.put(i+1, vertices);
                }
                vertices.add(graph.getVertices().get(i));
                queue.add(graph.getVertices().get(i));
            } else {
                Set<Vertex> vertices = layer.get(INF);
                if (vertices == null) {
                    vertices = new HashSet<Vertex>();
                    layer.put(INF, vertices);
                }
                vertices.add(graph.getVertices().get(i));
            }
        }
        layer.put(0, new HashSet<Vertex>());

        while (!queue.isEmpty()) {
            Vertex v = queue.poll();
            if (findVertexLayer(v) < INF) {
                for (Vertex u : v.getAdjacent()) {
                    if (findVertexLayer(utov.get(u)) == 0) {
                        Set<Vertex> vertexes = layer.get(findVertexLayer(v) + 1);
                        if (vertexes == null) {
                            vertexes = new HashSet<Vertex>();
                            layer.put(findVertexLayer(v) + 1, vertexes);
                        }
                        vertexes.add(u);
                        queue.add(u);
                    }
                }
            }
        }
        Set<Vertex> vertexes = layer.get(0);
        return vertexes.size() != 0;
    }

    // Finds a vertex among the layers, otherwise returns -1
    private Integer findVertexLayer(Vertex v) {
        if(v == null) {
            return 0;
        }
        for (Map.Entry<Integer, Set<Vertex>> layerVertices : layer.entrySet()) {
            for (Vertex layerVertex : layerVertices.getValue()) {
                if(v.equals(layerVertex)) {
                    return layerVertices.getKey();
                }
            }
        }
        return -1;
    }

    // Performs a depth-first through alternating layers to find an augmenting path
    private boolean findPath(Vertex v) {
        if (v != null) {
            for (Vertex u : v.getAdjacent()) {
                if (findVertexLayer(utov.get(u)) == findVertexLayer(v) + 1) {
                    if (findPath(utov.get(u))) {
                        utov.put(u, v);
                        utov.put(v, u);
                        return true;
                    }
                }
            }
            Set<Vertex> vertexes = layer.get(INF);
            if(vertexes == null) {
                vertexes = new HashSet<Vertex>();
                layer.put(INF, vertexes);
            }
            vertexes.add(v);
            return false;
        }
        return true;
    }

    public int maximumMatching() {
        int matching = 0;
        while (makeLayers()) {
            /*for(Vertex v : graph.getU()) {
                if(utov.get(v) == null) {
                    if(findPath(v)) {
                        matching = matching + 1;
                    }
                }
            }*/
        }
        return matching;
    }

}