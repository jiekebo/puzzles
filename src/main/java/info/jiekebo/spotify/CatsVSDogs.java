package info.jiekebo.spotify;


import java.util.*;


class BiGraph {
    private List<Vertex> vertices;
    private List<Vertex> u;
    private List<Vertex> v;

    public BiGraph() {
        this.vertices = new ArrayList<Vertex>();
        this.u = new ArrayList<Vertex>();
        this.v = new ArrayList<Vertex>();
    }

    public void add(Vertex newVertex) {
        for (Vertex vertex : this.vertices) {
            if (newVertex.opposing(vertex)) {
                newVertex.addAdjacent(vertex);
                vertex.addAdjacent(newVertex);
            }
        }
        if (newVertex.partition()) {
            u.add(newVertex);
        } else {
            v.add(newVertex);
        }
        this.vertices.add(newVertex);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public Map<Integer, List<Integer>> convertToIndices() {
        Map<Integer, List<Integer>> graphIndices = new HashMap<Integer, List<Integer>>();
        Vertex[] u = this.u.toArray(new Vertex[this.u.size()]);
        Vertex[] v = this.v.toArray(new Vertex[this.v.size()]);
        for (int i = 0; i < u.length; i++) {
            Vertex vertex = u[i];
            Vertex[] adjacent = vertex.getAdjacent().toArray(new Vertex[vertex.getAdjacent().size()]);
            List<Integer> adjacentIndices = new ArrayList<Integer>(adjacent.length);
            for (Vertex adjacentVertex : adjacent) {
                for (int j = 0; j < v.length; j++) {
                    if (adjacentVertex == v[j]) {
                        adjacentIndices.add(j);
                    }
                }
            }
            graphIndices.put(i, adjacentIndices);
        }
        return graphIndices;
    }
}


abstract class Vertex {
    private List<Vertex> adjacent;

    public Vertex() {
        this.adjacent = new ArrayList<Vertex>();
    }

    public void addAdjacent(Vertex vertex) {
        adjacent.add(vertex);
    }

    public List<Vertex> getAdjacent() {
        return adjacent;
    }

    public abstract boolean partition();

    public abstract boolean opposing(Vertex v);
}


class Vote extends Vertex {
    private Integer id;
    private Animal upvote;
    private Animal downvote;

    public Vote(Integer id, Animal upvote, Animal downvote) {
        this.id = id;
        this.upvote = upvote;
        this.downvote = downvote;
    }

    @Override
    public boolean partition() {
        return this.upvote instanceof Dog;
    }

    @Override
    public boolean opposing(Vertex v) {
        Vote vote = (Vote) v;
        return downvote.equals(vote.upvote) || upvote.equals(vote.downvote);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id.equals(vote.id);
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode();
    }
}


abstract class Animal {
    private Integer id;

    public Animal(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id.equals(animal.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


class Cat extends Animal {
    public Cat(Integer id) {
        super(id);
    }
}


class Dog extends Animal {
    public Dog(Integer id) {
        super(id);
    }
}


class HopcroftKarp {
    private static ArrayList<Integer> getValueOrDefault(HashMap<Integer, ArrayList<Integer>> map, Integer key) {
        ArrayList<Integer> val = map.get(key);
        if (val == null) {
            map.put(key, new ArrayList<Integer>());
        }
        return map.get(key);
    }

    public Map<Integer, Integer> findMaximumMatching(Map<Integer, List<Integer>> graph, boolean randomize) {
        HashMap<Integer, Integer> current_layer_u = new HashMap<Integer, Integer>(); // u --> v
        HashMap<Integer, ArrayList<Integer>> current_layer_v = new HashMap<Integer, ArrayList<Integer>>(); // v --> list of u
        HashMap<Integer, Integer> all_layers_u = new HashMap<Integer, Integer>(); // u --> v
        HashMap<Integer, ArrayList<Integer>> all_layers_v = new HashMap<Integer, ArrayList<Integer>>(); // v --> list of u
        HashMap<Integer, Integer> matched_v = new HashMap<Integer, Integer>(); // v --> u
        ArrayList<Integer> unmatched_v = new ArrayList<Integer>(); // list of v

        while (true) {
            int k = 0;
            all_layers_u.clear();
            current_layer_u.clear();
            for (Integer u : graph.keySet()) {
                if (!matched_v.containsValue(u)) {
                    current_layer_u.put(u, 0);
                    all_layers_u.put(u, 0);
                }
            }
            all_layers_v.clear();
            unmatched_v.clear();
            while (!current_layer_u.isEmpty() && unmatched_v.isEmpty()) {
                current_layer_v.clear();
                for (Integer u : current_layer_u.keySet()) {
                    for (Integer v : graph.get(u)) {
                        if (!all_layers_v.containsKey(v)) {
                            getValueOrDefault(current_layer_v, v).add(u);
                        }
                    }
                }
                k++;
                current_layer_u.clear();
                for (Integer v : current_layer_v.keySet()) {
                    all_layers_v.put(v, current_layer_v.get(v));
                    if (matched_v.containsKey(v)) {
                        Integer u = matched_v.get(v);
                        current_layer_u.put(u, v);
                        all_layers_u.put(u, v);
                    } else {
                        unmatched_v.add(v);
                    }
                }
            }
            if (!unmatched_v.isEmpty()) {
                if (randomize) {
                    Collections.shuffle(unmatched_v);
                }
                for (Integer v : unmatched_v) {
                    if (k >= 1) {
                        recFindAugmentingPath(v, all_layers_u, all_layers_v, matched_v, randomize, (k - 1)); // Ignore return status
                    } else {
                        throw new ArithmeticException("k should not be equal to zero here.");
                    }
                }
            } else {
                break;
            }
        }

        return matched_v;
    }

    private boolean recFindAugmentingPath(Integer v,
                                          HashMap<Integer, Integer> all_layers_u,
                                          HashMap<Integer, ArrayList<Integer>> all_layers_v,
                                          HashMap<Integer, Integer> matched_v,
                                          boolean randomize,
                                          int k) {
        if (all_layers_v.containsKey(v)) {
            ArrayList<Integer> list_u = all_layers_v.get(v);
            if (randomize) {
                Collections.shuffle(list_u);
            }
            for (Integer u : list_u) {
                if (all_layers_u.containsKey(u)) {
                    Integer prev_v = all_layers_u.get(u);
                    if (k == 0 || recFindAugmentingPath(prev_v, all_layers_u, all_layers_v, matched_v, randomize, (k - 1))) {
                        matched_v.put(v, u);
                        all_layers_v.remove(v);
                        all_layers_u.remove(u);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Map<Integer, Integer> removeDuplicateMatchings(HashMap<Integer, Integer> matchings) {
        Map<Integer, Integer> reverse_mapping = getReverseMapping(matchings);
        Iterator<Integer> iterator = matchings.keySet().iterator();
        while (iterator.hasNext()) {
            Integer v = iterator.next();
            if (matchings.containsValue(v)) {
                Integer u = reverse_mapping.get(v);
                if (matchings.containsKey(u)) {
                    iterator.remove();
                }
            }
        }
        return matchings;
    }

    private Map<Integer, Integer> getReverseMapping(HashMap<Integer, Integer> input_map) {
        Map<Integer, Integer> reversed_map = new HashMap<Integer, Integer>();
        for (Integer v : input_map.keySet()) {
            Integer u = input_map.get(v);
            reversed_map.put(u, v);
        }
        return reversed_map;
    }
}


class Hopcroft {
    private final int NIL = 0;
    private final int INF = Integer.MAX_VALUE;
    private ArrayList<Integer>[] Adj;
    private int[] Pair;
    private int[] Dist;
    private int cx, cy;

    /**
     * Function BFS *
     */
    public boolean BFS() {
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int v = 1; v <= cx; ++v)
            if (Pair[v] == NIL) {
                Dist[v] = 0;
                queue.add(v);
            } else
                Dist[v] = INF;

        Dist[NIL] = INF;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            if (Dist[v] < Dist[NIL])
                for (int u : Adj[v])
                    if (Dist[Pair[u]] == INF) {
                        Dist[Pair[u]] = Dist[v] + 1;
                        queue.add(Pair[u]);
                    }
        }
        return Dist[NIL] != INF;
    }

    /**
     * Function DFS *
     */
    public boolean DFS(int v) {
        if (v != NIL) {
            for (int u : Adj[v])
                if (Dist[Pair[u]] == Dist[v] + 1)
                    if (DFS(Pair[u])) {
                        Pair[u] = v;
                        Pair[v] = u;
                        return true;
                    }

            Dist[v] = INF;
            return false;
        }
        return true;
    }

    /**
     * Function to get maximum matching *
     */
    public int HopcroftKarp() {
        Pair = new int[cx + cy + 1];
        Dist = new int[cx + cy + 1];
        int matching = 0;
        while (BFS())
            for (int v = 1; v <= cx; ++v)
                if (Pair[v] == NIL)
                    if (DFS(v))
                        matching = matching + 1;
        return matching;
    }

    /**
     * Function to make graph with vertices x , y *
     */
    public void makeGraph(int[] x, int[] y, int E) {
        Adj = new ArrayList[cx + cy + 1];
        for (int i = 0; i < Adj.length; ++i)
            Adj[i] = new ArrayList<Integer>();
        /** adding edges **/
        for (int i = 0; i < E; ++i)
            addEdge(x[i] + 1, y[i] + 1);
    }

    /**
     * Function to add a edge *
     */
    public void addEdge(int u, int v) {
        Adj[u].add(cx + v);
        Adj[cx + v].add(u);
    }

    /**
     * Main Method *
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Hopcroft Algorithm Test\n");
        Hopcroft hc = new Hopcroft();
        System.out.println("Enter number of edges\n");
        int E = scan.nextInt();
        int[] x = new int[E];
        int[] y = new int[E];
        hc.cx = 0;
        hc.cy = 0;

        System.out.println("Enter " + E + " x, y coordinates ");
        for (int i = 0; i < E; i++) {
            x[i] = scan.nextInt();
            y[i] = scan.nextInt();
            hc.cx = Math.max(hc.cx, x[i]);
            hc.cy = Math.max(hc.cy, y[i]);
        }
        hc.cx += 1;
        hc.cy += 1;
        hc.makeGraph(x, y, E);

        System.out.println("\nMatches : " + hc.HopcroftKarp());
    }
}


public class CatsVSDogs {
    private static Vote createVote(int voteId, String u, String d) {
        Animal upvote;
        Animal downvote;

        if (u.charAt(0) == 'C') {
            upvote = new Cat(Character.getNumericValue(u.charAt(1)));
        } else {
            upvote = new Dog(Character.getNumericValue(u.charAt(1)));
        }

        if (d.charAt(0) == 'C') {
            downvote = new Cat(Character.getNumericValue(d.charAt(1)));
        } else {
            downvote = new Dog(Character.getNumericValue(d.charAt(1)));
        }
        return new Vote(voteId, upvote, downvote);
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int testCases = scan.nextInt();

        for (int i = 0; i < testCases; i++) {
            BiGraph graph = new BiGraph();

            int cats = scan.nextInt();
            int dogs = scan.nextInt();
            int voters = scan.nextInt();

            for (int j = 0; j < voters; j++) {
                String upvote = scan.next();
                String downvote = scan.next();
                Vote vote = createVote(j, upvote, downvote);
                graph.add(vote);
            }

            HopcroftKarp hc = new HopcroftKarp();
            Map<Integer, Integer> maximumMatching = hc.findMaximumMatching(graph.convertToIndices(), false);

            System.out.println(voters - maximumMatching.size());
        }
    }
}