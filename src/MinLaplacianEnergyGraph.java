import java.util.*;

public class MinLaplacianEnergyGraph {

    public static void main(String... args) {
        int n = 6;
        int m = 6;
        if(args.length ==2){
            n = Integer.parseInt(args[0]);
            m = Integer.parseInt(args[1]);
        }
        List<int[]> res = getDegreePermutations(n, m);
        //System.out.println("n=" + n + ", m=" + m + ", Degree sequences (de-duped considering rotation):");
//        for (int[] r : res) {
//            System.out.println(Arrays.toString(r));
//        }
        Set<Graph> graphs = new TreeSet<>((a,b)->a.toString().compareTo(b.toString()));
        for (int[] degrees : res) {
            //System.out.println("For degree sequence:" + Arrays.toString(degrees));
            List<int[]> firstEdges = get_nCr(n, 2); //int array is always size 2, representing an edge i,j
            for (int[] edge : firstEdges) {
                int maxJ = n + 1 - degrees[edge[0] - 1]; //j's variation range max for current first edges
                if (edge[1] <= maxJ) {
                    Graph g = new Graph();
                    //g.add(edge[0], edge[1]);
                    Candidate candidate = new Candidate(degrees, g);
                    Candidate.processCandidate(graphs, candidate.add(edge[0], edge[1]));
                }
            }
            //System.out.println("Graphs generated: " + graphs.size());
            //Collections.sort(graphs, (a,b)->a.toString().compareTo(b.toString()));
        }

//        System.out.println("Graphs generated: " + graphs.size());
//        int i=1;
//        for (Graph g : graphs) {
//            System.out.println("Graph "+i+": "+g);
//            i++;
//        }
//        int i=1;
//        for (Graph graph: graphs){
//            System.out.println("Graph "+i+": [");
//            i++;
//            for(Edge edge: graph.edges){
//                System.out.println(edge.start+"  "+edge.end);
//            }
//            System.out.println("]");
//        }

        //Collections.sort(graphs, (a,b)->a.toString().compareTo(b.toString()));
        //System.out.println("Copy and save the following content into a python file and execute.");
        String pythoncode1= """
                import networkx as nx
                import matplotlib
                matplotlib.use('TkAgg')
                import matplotlib.pyplot as plt\s
                graphs= {}                                
                """;
//        System.out.println("import networkx as nx");
//        System.out.println("import matplotlib");
//        System.out.println("matplotlib.use('TkAgg')");
//        System.out.println("import matplotlib.pyplot as plt ");
//        System.out.println("graphs= {}");
        System.out.println(pythoncode1);
        for (Graph g : graphs) {
            System.out.println("G = nx.Graph"+g);
            System.out.println("graphs[nx.weisfeiler_lehman_graph_hash(G)]=G");
        }
        String pythoncode2= """
                if len(graphs) ==1:
                    for H in graphs.values():
                        nx.draw_networkx(H, pos=nx.circular_layout(H), font_color='white')
                        plt.title(H.edges)
                        plt.show()
                else:
                    index = 0
                    fig, all_axes = plt.subplots(1, len(graphs))
                    ax = all_axes.flat
                    for H in graphs.values():
                        ax[index].set_title(list(H.edges))
                        nx.draw_networkx(H, ax=ax[index], pos=nx.circular_layout(H), font_color='white')
                        index +=1
                    for a in ax:
                        a.margins(0.10)
                    fig.tight_layout()
                    plt.show()
                                
                """;
        System.out.println(pythoncode2);
//        System.out.println("if len(graphs) ==1:");
//        System.out.println("    for H in graphs.values():");
//        System.out.println("        nx.networkx(H)");
//        System.out.println("        plt.show()");
//        System.out.println("else:");
//        System.out.println("    index = 0");
//        System.out.println("    fig, all_axes = plt.subplots(1, len(graphs))");
//        System.out.println("    ax = all_axes.flat");
//        System.out.println("    for H in graphs.values():");
//        System.out.println("        ax[index].set_title(list(H.edges))");
//        System.out.println("        nx.draw_networkx(H, ax=ax[index])");
//        System.out.println("        index +=1");
//        System.out.println("    for a in ax:");
//        System.out.println("        a.margins(0.10)");
//        System.out.println("    fig.tight_layout()");
//        System.out.println("    plt.show()");
     }


    public static List<int[]> getDegreePermutations(int n, int m) {
        int k = 2 * m / n;
        int numK = n * (k + 1) - 2 * m;
        int numK1 = 2 * m - n * k;
        int smallGroupNum = k + 1;
        int bigGroupNum = k;
        if (numK <= numK1) {
            smallGroupNum = k;
            bigGroupNum = k + 1;
        }
        List<int[]> degrees = new ArrayList<>();

        int smallerNum = Math.min(numK, numK1);
        if (smallerNum <= 1) {
            int[] justOne = new int[n];
            Arrays.fill(justOne, bigGroupNum);
            if(smallerNum == 1) justOne[0] = smallGroupNum;
            degrees.add(justOne);
            return degrees;
        }
        //using 3 3 2 2 2 as example, to de-dup the permutations from rotation, we can fit the first 3 to be always
        //in the first place, and the rest permutations is about the second 3, which could be in position 2,3,4,5.
        //when it is in position 5, it could be further rotated, so skip those permutations where the second 3 is in the
        //last position.
        //also, to de-dup 3 2 3 2 2 and 3 2 2 3 2, we can simply double the second one,=> 3 2 2 3 2 3 2 2 3 2, and use
        //String.indexOf() method to check if 3 2 3 2 2 is part of that.
        List<int[]> indexPerms = get_nCr(n - 1, smallerNum - 1);
        for (int[] permutation : indexPerms) {
            if (permutation[permutation.length - 1] == n - 1) continue;
            int[] temp = new int[n];
            Arrays.fill(temp, bigGroupNum);
            temp[0] = smallGroupNum;
            for (int index : permutation) {
                temp[index] = smallGroupNum;
            }
            String check = String.join(",",Arrays.stream(temp).mapToObj(String::valueOf).toArray(s -> new String[s]));
            check = check+","+check;
            boolean duplicated = false;
            for(int[] degreeArray: degrees){
                String d = String.join(",", Arrays.stream(degreeArray).mapToObj(String::valueOf).toArray(s -> new String[s]));
                if(check.indexOf(d)>=0) {
                    duplicated = true;
                    break;
                }
            }
            if(duplicated) continue;
            degrees.add(temp);
        }
        return degrees;
    }


    public static final List<int[]> get_nCr(final int n, final int r) {
        int[] res = new int[r];
        for (int i = 0; i < res.length; i++) {
            res[i] = i + 1;
        }
        boolean done = false;
        List<int[]> rv = new ArrayList<>();
        while (!done) {

            rv.add(Arrays.copyOf(res, r));
            //System.out.println(Arrays.toString(res));
            done = getNext(res, n, r);
        }
        return rv;
    }

    /////////

    public static final boolean getNext(final int[] num, final int n, final int r) {
        int target = r - 1;
        num[target]++;
        if (num[target] > ((n - (r - target)) + 1)) {
            // Carry the One
            while (num[target] > ((n - (r - target)))) {
                target--;
                if (target < 0) {
                    break;
                }
            }
            if (target < 0) {
                return true;
            }
            num[target]++;
            for (int i = target + 1; i < num.length; i++) {
                num[i] = num[i - 1] + 1;
            }
        }
        return false;
    }
}

class Candidate {
    int[] degrees;
    Graph preLevel;
    int preI = -1; //previous edge
    int preJ = -1;

    public Candidate(int[] degrees, Graph preLevel) {
        this.degrees = degrees;
        this.preLevel = preLevel;
    }

    public Candidate(int[] degrees, Graph preLevel, int preI, int preJ) {
        this.degrees = degrees;
        this.preLevel = preLevel;
        this.preI = preI;
        this.preJ = preJ;
    }

    //add an edge to this candidate graph, reducing the degree list
    //TODO change to Using Edge as parameter add(Edge e)
    public Candidate add(int start, int end) {
        Graph g = this.preLevel.clone();
        //g.edges = new HashSet<>(preLevel.edges);
        g.add(start, end);
        int[] newDegree = degrees.clone();
        newDegree[start - 1]--;
        newDegree[end - 1]--;
        return new Candidate(newDegree, g, start, end);
    }

    public Candidate clone() {
        return new Candidate(this.degrees, this.preLevel.clone(), this.preI, this.preJ);
    }

    public int getNextI() {
        for (int i = 0; i < degrees.length; i++) {
            if (degrees[i] > 0) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getMaxJ(int i) {
        int n = degrees.length;
        int d = degrees[i - 1];
        return n + 1 - d;
    }

    public static void processCandidate(Set<Graph> allGraph, Candidate seed) {
        int iCurrent = seed.getNextI();
        if (iCurrent < 0) {
            allGraph.add(seed.preLevel);
            return;
        }
        int jCurrent = iCurrent + 1;
        if (iCurrent == seed.preI) jCurrent = seed.preJ + 1;

        for (int j = jCurrent; j <= seed.getMaxJ(iCurrent); j++) {
            if (seed.degrees[j - 1] == 0) continue;
            if (seed.preLevel.edges.contains(new Edge(iCurrent, j))) continue;
            processCandidate(allGraph, seed.clone().add(iCurrent, j));
        }
    }
}

class Graph {
    List<Edge> edges;

    public Graph() {
        edges = new ArrayList<>();
    }

    public static void main(String ... args){
        Graph g = new Graph();
        g.add(2,4).add(2,6).add(1,3);
        g.add(1,5);
        System.out.println(g);
    }

    public Graph clone(){
        Graph g = new Graph();
        for(Edge edge:edges){
            g.add(edge.start, edge.end);
        }
        return g;
    }

    public Graph add(int start, int end) {
        Edge edge = new Edge(start, end);
        edges.add(edge);
        //System.out.println("Added "+ edge.toString()+" Graph:"+this.toString());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Graph) {
            Graph g = (Graph) obj;
            if (g.edges.size() != this.edges.size()) return false;
            for (Edge e : g.edges) {
                if (!this.edges.contains(e)) return false;
            }
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Collections.sort(this.edges, (a,b)->a.compareTo(b));
        sb.append("([");
        for (Edge e : edges) {
            sb.append("(").append(e.start).append(",").append(e.end).append(")").append(",");
        }
        if (edges.size() > 0) sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("])");
        return sb.toString();
    }
}

class Edge implements Comparable {
    int start;
    int end;

    public Edge(int start, int end) {
        this.start = start < end ? start : end;
        this.end = start < end ? end : start;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;
            return e.start == this.start && e.end == this.end;
        }
        return false;
    }

    @Override
    public int hashCode() {
        String stringValue = start + "," + end;
        return stringValue.hashCode();
    }

    @Override
    public String toString() {
        return start+"-"+end;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Edge){
            Edge other = (Edge) o;
            if(this.equals(other)) return 0;
            if(this.start < other.start) return -1;
            if(this.start == other.start && this.end <other.end) return 1;
        }
        return this.toString().compareTo(o.toString());
    }
}
