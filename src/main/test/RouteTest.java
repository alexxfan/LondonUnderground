import com.example.londonunderground.models.Route;
import com.example.londonunderground.models.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RouteTest{
    private List<Station> path;
    private Station stationAlex;
    private Station stationKillian;
    private Station stationJordan;

    @Before
    public void setUp(){
        // set up some sample stations and a sample path
        path = new ArrayList<>();
        stationAlex = new Station("Station 1", 0.0, 0.0);
        stationKillian = new Station("Station 2", 3.0, 4.0);
        stationJordan = new Station("Station 3", 6.0, 8.0);
        path.add(stationAlex);
        path.add(stationKillian);
        path.add(stationKillian);
    }

    @Test
    public void testGetters(){
        // create a sample route with a few stops
        Route routeA = new Route(path, 5);

        // test the getters for the first route
        Assert.assertEquals(path, routeA.getPath());
        Assert.assertEquals(5, routeA.getStops());
        Assert.assertEquals(0, routeA.getDistance(), 0);
        Assert.assertEquals(0, routeA.getLineChanges());
        Assert.assertEquals(0, routeA.getLineChangePenalty(), 0);

        // create another route but with distance
        Route routeB = new Route(path, 10.0);

        // test the getters for second route
        Assert.assertEquals(path, routeB.getPath());
        Assert.assertEquals(0, routeB.getStops());
        Assert.assertEquals(10.0, routeB.getDistance(), 0);
        Assert.assertEquals(0, routeB.getLineChanges());
        Assert.assertEquals(0, routeB.getLineChangePenalty(), 0);

        // create another route but this time with line changes and line change penalty
        Route routeC = new Route(path, 2, 15.0, 4.0);

        // test the getters for third route
        Assert.assertEquals(path, routeC.getPath());
        Assert.assertEquals(2, routeC.getStops());
        Assert.assertEquals(15.0, routeC.getDistance(), 0);
        Assert.assertEquals(2, routeC.getLineChanges());
        Assert.assertEquals(4.0, routeC.getLineChangePenalty(), 0);
    }
}
