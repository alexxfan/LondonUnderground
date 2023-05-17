import com.example.londonunderground.models.Line;
import com.example.londonunderground.models.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LineTest {
    private Line line;
    private Station stationAlex;
    private Station stationKillian;
    private Station stationJordan;

    @Before
    public void setUp(){
        // setting up a sample line and 3 sample stations
        line = new Line("Line 1", "Magenta");
        stationAlex = new Station("Station Alex", 0.0, 0.0);
        stationKillian = new Station("Station Killian", 3.0, 4.0);
        stationJordan = new Station("Station Jordan", 6.0, 8.0);
    }

    @Test
    public void testGetters(){
        // testing for getters
        Assert.assertEquals("Line 1", line.getLineName());
        Assert.assertEquals("Magenta", line.getColor());
    }

    @Test
    public void testAddStation(){
        // add stations to the line
        line.addStation(stationAlex);
        line.addStation(stationKillian);
        line.addStation(stationJordan);

        List<Station> stations = line.getStations();

        // test to see if stations were added to the lines
        Assert.assertEquals(3, stations.size());
        Assert.assertTrue(stations.contains(stationAlex));
        Assert.assertTrue(stations.contains(stationKillian));
        Assert.assertTrue(stations.contains(stationJordan));

        // test the stations to see if they contain the same line
        Assert.assertTrue(stationAlex.getLines().contains(line));
        Assert.assertTrue(stationKillian.getLines().contains(line));
        Assert.assertTrue(stationJordan.getLines().contains(line));
    }
}
