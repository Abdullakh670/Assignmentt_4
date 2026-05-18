import java.util.*;

public class Main {


    static Map<String, List<int[]>> adj   = new LinkedHashMap<>();
    static Map<String, Integer>     index = new LinkedHashMap<>();
    static String[]                 label;          // index -> vertex name

    // ══════════════════════════════════════════════
    //  TASK 1 — Graph Representation
    // ══════════════════════════════════════════════

    static void add_vertex(String v) {                     //I implemented an adjacency list using a LinkedHashMap
        if (!adj.containsKey(v)) {
            index.put(v, index.size());
            adj.put(v, new ArrayList<>());
        }
    }

    static void add_edge(String v, String w, int weight) {         //It is highly memory-efficient for sparse graphs.
        add_vertex(v);                                            // To optimize performance, I mapped string vertex names to integer IDs. 
        add_vertex(w);
        adj.get(v).add(new int[]{ index.get(w), weight });
        adj.get(w).add(new int[]{ index.get(v), weight });
    }                                                 

    static void printAdjacencyList() {
        System.out.println("╔══════════════════════════════╗");               
        System.out.println("║  TASK 1 — Adjacency List     ║");
        System.out.println("╚══════════════════════════════╝");
        for (Map.Entry<String, List<int[]>> e : adj.entrySet()) {
            StringBuilder sb = new StringBuilder("  " + e.getKey() + " -> ");
            for (int[] edge : e.getValue())
                sb.append(label[edge[0]]).append("(").append(edge[1]).append(")  ");
            System.out.println(sb.toString().trim());
        }
        System.out.println();
    }                                //Instead of creating a separate class, edges are simply stored as int[] arrays containing the neighbor's ID and the edge weight, which saves memory.

    // ══════════════════════════════════════════════
    //  TASK 2 — DFS & BFS
    // ══════════════════════════════════════════════

    static void dfs(String start) {                                 //I implemented both traversals with an $O(V + E)$ time complexity.
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║  TASK 2 — DFS from " + start + "         ║");
        System.out.println("╚══════════════════════════════╝");
        Set<String> visited = new LinkedHashSet<>();
        dfsRecursive(start, visited);
        System.out.println("  Order: " + visited);
        System.out.println();
    }

    static void dfsRecursive(String node, Set<String> visited) {
        visited.add(node);
        for (int[] edge : adj.get(node)) {
            String neighbour = label[edge[0]];
            if (!visited.contains(neighbour))
                dfsRecursive(neighbour, visited);    //DFS is implemented recursively to explore branches deeply.
        }
    }                                                //BFS is implemented iteratively using a Queue for level-by-level exploration.

    static void bfs(String start) {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║  TASK 2 — BFS from " + start + "         ║");
        System.out.println("╚══════════════════════════════╝");
        Set<String>   visited = new LinkedHashSet<>();
        Queue<String> queue   = new LinkedList<>();
        visited.add(start);
        queue.offer(start);
        while (!queue.isEmpty()) {
            String node = queue.poll();
            for (int[] edge : adj.get(node)) {
                String neighbour = label[edge[0]];
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);            //BFS explores vertices level-by-level
                    queue.offer(neighbour);           //The first time it reaches the target vertex, it is guaranteed to be the shortest path.
                }
            }
        }                                                  
        System.out.println("  Order: " + visited);
        System.out.println();
    }                                       //For both, I used a LinkedHashSet to track visited nodes. It provides $O(1)$ lookups while strictly preserving the traversal order for printing. 

    // ══════════════════════════════════════════════
    //  TASK 3 — Dijkstra's Shortest Path
    // ══════════════════════════════════════════════

    static void dijkstra(String source) {                                //I used Dijkstra's algorithm to find the shortest paths.
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║  TASK 3 — Dijkstra from " + source + "       ║");
        System.out.println("╚══════════════════════════════════╝");

        Map<String, Integer> dist = new HashMap<>();                        //The core of my implementation is a Min-Heap
        Map<String, String>  prev = new HashMap<>();                         //which continuously extracts the closest node, ensuring an optimal time complexity.

        for (String v : adj.keySet()) {                   //I used a dist map to track minimum distances and a prev map to remember parent nodes.
            dist.put(v, Integer.MAX_VALUE);
            prev.put(v, null);
        }
        dist.put(source, 0);

        // min-heap: [distance, vertexIndex]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{ 0, index.get(source) });

        Set<String> settled = new HashSet<>();
                                                        //Finally, I used a Deque to backtrack from the target to the source, allowing me to build and print the exact route.
        while (!pq.isEmpty()) {
            int[]  cur     = pq.poll();
            String curNode = label[cur[1]];
            if (settled.contains(curNode)) continue;
            settled.add(curNode);

            for (int[] edge : adj.get(curNode)) {
                String nb     = label[edge[0]];
                int    newDist = cur[0] + edge[1];
                if (newDist < dist.get(nb)) {
                    dist.put(nb, newDist);
                    prev.put(nb, curNode);
                    pq.offer(new int[]{ newDist, edge[0] });
                }
            }
        }

        for (String v : adj.keySet()) {
            if (v.equals(source)) continue;
            String path = buildPath(prev, source, v);
            System.out.printf("  %s -> %-2s | dist: %-4s | path: %s%n",
                    source, v,
                    dist.get(v) == Integer.MAX_VALUE ? "∞" : dist.get(v),
                    path);
        }
        System.out.println();
    }

    static String buildPath(Map<String, String> prev, String source, String target) {
        Deque<String> path = new ArrayDeque<>();
        for (String at = target; at != null; at = prev.get(at))
            path.addFirst(at);
        return String.join(" -> ", path);
    }

    // ══════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════

    public static void main(String[] args) {

        // ── Task 1: build graph ───────────────────
        add_vertex("A"); add_vertex("B"); add_vertex("C");
        add_vertex("D"); add_vertex("E");

        add_edge("B", "A", 14);
        add_edge("C", "A", 11);
        add_edge("D", "A", 8);
        add_edge("E", "C", 1);
        add_edge("B", "D", 13);
        add_edge("D", "C", 5);

        // build reverse-index array once
        label = new String[index.size()];
        index.forEach((v, i) -> label[i] = v);

        printAdjacencyList();   // Task 1

        dfs("D");               // Task 2 — DFS  (start node = D)
        bfs("D");               // Task 2 — BFS  (start node = D)

        dijkstra("C");          // Task 3 — source = C
    }
}
