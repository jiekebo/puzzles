package info.jiekebo.spotify;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CatsVSDogsTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void spotifyTest1() throws Exception {
        BiGraph graph = new BiGraph();

        Vote vote1 = new Vote(1, new Cat(1), new Dog(1));
        Vote vote2 = new Vote(2, new Dog(1), new Cat(1));

        graph.add(vote1);
        graph.add(vote2);

        HopcroftKarp hc = new HopcroftKarp();
        Map<Integer, Integer> maximumMatching = hc.findMaximumMatching(graph.convertToIndices(), false);

        assertEquals(1, 2 - maximumMatching.size());
    }

    @Test
    public void spotifyTest2() throws Exception {
        BiGraph graph = new BiGraph();

        Vote vote1 = new Vote(1, new Cat(1), new Dog(1));
        Vote vote2 = new Vote(2, new Cat(1), new Dog(1));
        Vote vote3 = new Vote(3, new Cat(1), new Dog(2));
        Vote vote4 = new Vote(4, new Dog(2), new Cat(1));

        graph.add(vote1);
        graph.add(vote2);
        graph.add(vote3);
        graph.add(vote4);

        HopcroftKarp hc = new HopcroftKarp();
        Map<Integer, Integer> maximumMatching = hc.findMaximumMatching(graph.convertToIndices(), false);

        assertEquals(3, 4 - maximumMatching.size());
    }

}
