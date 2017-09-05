package ru.prolib.aquila.utils.experimental.swing_chart;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.RangeInfo;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by TiM on 13.06.2017.
 */
public class CoordConverterImplTest {
    private CoordConverter<Instant> converter;
    private List<Instant> categories;
    private Rectangle plotBounds;
    private Graphics2D graphics;

    @Before
    public void setUp() throws Exception {
        categories = new ArrayList<>();
        categories.add(Instant.parse("2017-06-13T06:00:00Z"));
        categories.add(Instant.parse("2017-06-13T06:01:00Z"));
        categories.add(Instant.parse("2017-06-13T06:02:00Z"));
        categories.add(Instant.parse("2017-06-13T06:03:00Z"));
        categories.add(Instant.parse("2017-06-13T06:04:00Z"));

        graphics = EasyMock.createMock(Graphics2D.class);
        plotBounds = new Rectangle(50, 50, 500, 300);
        converter = new CoordConverterImpl<Instant>(categories, graphics, plotBounds, new RangeInfo(50, 52, 10, 50, 52));
    }

    @Test
    public void testGetCategories() throws Exception {
        assertEquals(categories, converter.getCategories());
    }

    @Test
    public void testIsCategoryDisplayed() throws Exception {
        assertEquals(true, converter.isCategoryDisplayed(Instant.parse("2017-06-13T06:02:00Z")));
        assertEquals(false, converter.isCategoryDisplayed(Instant.parse("2017-06-13T06:12:00Z")));
    }

    @Test
    public void testGetX() throws Exception {
        assertEquals(133.33, converter.getX(categories.get(0)), 1e-2);
        assertEquals(466.67, converter.getX(categories.get(4)), 1e-2);
        assertEquals(null, converter.getX(Instant.parse("2017-06-13T06:12:00Z")));
    }

    @Test
    public void testGetY() throws Exception {
//        assertEquals(null, converter.getY(49d));
        assertEquals(350d, converter.getY(50d), 1e-2);
        assertEquals(200d, converter.getY(51d), 1e-2);
        assertEquals(50d, converter.getY(52d), 1e-2);
//        assertEquals(null, converter.getY(53d));
    }

    @Test
    public void testGetCategory() throws Exception {
        assertEquals(categories.get(0), converter.getCategory(133.3333));
        assertEquals(categories.get(4), converter.getCategory(466.67));
        assertEquals(null, converter.getCategory(550d));
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(50d, converter.getValue(350d), 1e-2);
        assertEquals(51d, converter.getValue(200d), 1e-2);
        assertEquals(52d, converter.getValue(50d), 1e-2);

    }

    @Test
    public void testGetGraphics() throws Exception {
        assertEquals(graphics, converter.getGraphics());
    }

    @Test
    public void testGetPlotBounds() throws Exception {
        assertEquals(plotBounds, converter.getPlotBounds());
    }

    @Test
    public void testGetStepX() throws Exception {
        assertEquals(83.33, converter.getStepX(), 1e-2);
    }
}