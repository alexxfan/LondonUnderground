import com.example.londonunderground.models.Line;
import com.example.londonunderground.models.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StationTest{
    private Station station;
    private Station neighbor1;
    private Station neighbor2;

    @Before
    public void setUp(){
        // set up 3 sample stations
        station = new Station("Station Alex", 0, 0);
        neighbor1 = new Station("Station Killian", 3, 4);
        neighbor2 = new Station("Station Jordan", 6, 8);
    }

    @Test
    public void testGettersAndSetters(){
        // test the getters
        Assert.assertEquals("Station Alex", station.getStationName());
        Assert.assertEquals(0, station.getX(), 0);
        Assert.assertEquals(0, station.getY(), 0);

        // test the setters
        station.setStationName("Updated Station Alex");
        station.setX(1);
        station.setY(2);

        Assert.assertEquals("Updated Station Alex", station.getStationName());
        Assert.assertEquals(1, station.getX(), 0);
        Assert.assertEquals(2, station.getY(), 0);
    }

    @Test
    public void testAddNeighbor(){
        // Add neighbors
        station.addNeighbor(neighbor1);
        station.addNeighbor(neighbor2);

        Map<Station, Double> expectedNeighbors = new HashMap<>();
        expectedNeighbors.put(neighbor1, 5.0); // distance between (0, 0) and (3, 4) is 5
        expectedNeighbors.put(neighbor2, 10.0); // distance between (0, 0) and (6, 8) is 10

        Assert.assertEquals(expectedNeighbors, station.getNeighbors());
    }

    @Test
    public void testCalculateDistanceTo(){
        // calculating the distance between stations
        double distance1 = station.calculateDistanceTo(neighbor1);
        double distance2 = station.calculateDistanceTo(neighbor2);

        Assert.assertEquals(5.0, distance1, 0);
        Assert.assertEquals(10.0, distance2, 0);
    }

    @Test
    public void testAddLine(){
        // Add lines
        Line lineA = new Line("Line A", "Magenta");
        Line lineB = new Line("Line B", "Red");

        station.addLine(lineA);
        station.addLine(lineB);

        Assert.assertEquals(2, station.getLines().size());
        Assert.assertTrue(station.getLines().contains(lineA));
        Assert.assertTrue(station.getLines().contains(lineB));
    }

    @Test
    public void testToString(){
        station.addNeighbor(neighbor1);
        station.addNeighbor(neighbor2);

        String expectedString = "Station: Name: Station Alex, xCoordinate: 0.0, yCoordinate: 0.0, neighboring stations: Station Killian, Station Jordan";
        Assert.assertEquals(expectedString, station.toString());
    }
}
