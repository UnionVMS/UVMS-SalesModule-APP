package eu.europa.ec.fisheries.uvms.sales.service.converter;

import eu.europa.ec.fisheries.schema.sales.FLUXLocationType;
import eu.europa.ec.fisheries.schema.sales.IDType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FLUXLocationsToListOfIdsConverterTest {

    private FLUXLocationsToListOfIdsConverter converter;

    @Before
    public void init() {
        converter = new FLUXLocationsToListOfIdsConverter();
    }

    @Test
    public void convertToWhenListOfFluxLocationsIsNull() throws Exception {
        assertEquals(new ArrayList<FLUXLocationType>(), converter.convertTo(null, null, null));
    }

    @Test
    public void convertToWhenListOfFluxLocationsIsEmpty() throws Exception {
        assertEquals(new ArrayList<FLUXLocationType>(), converter.convertTo(new ArrayList<>(), null, null));
    }

    @Test
    public void convertToWhenListOfFluxLocationsContainsFluxLocationsWithAIdThatIsNull() throws Exception {
        FLUXLocationType fluxLocation1 = new FLUXLocationType().withID(new IDType().withValue("1"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType().withID(null);
        FLUXLocationType fluxLocation3 = new FLUXLocationType().withID(new IDType().withValue("3"));
        List<FLUXLocationType> fluxLocations = Arrays.asList(fluxLocation1, fluxLocation2, fluxLocation3);

        assertEquals(Arrays.asList("1", "3"), converter.convertTo(fluxLocations, null, null));
    }

    @Test
    public void convertToWhenListOfFluxLocationsContainsFluxLocationsWithAIdThatHasANullValue() throws Exception {
        FLUXLocationType fluxLocation1 = new FLUXLocationType().withID(new IDType().withValue("1"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType().withID(new IDType().withValue(null));
        FLUXLocationType fluxLocation3 = new FLUXLocationType().withID(new IDType().withValue("3"));
        List<FLUXLocationType> fluxLocations = Arrays.asList(fluxLocation1, fluxLocation2, fluxLocation3);

        assertEquals(Arrays.asList("1", "3"), converter.convertTo(fluxLocations, null, null));
    }

    @Test
    public void convertToWhenListOfFluxLocationsContainsFluxLocationsWithAIdThatHasABlankValue() throws Exception {
        FLUXLocationType fluxLocation1 = new FLUXLocationType().withID(new IDType().withValue("1"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType().withID(new IDType().withValue(""));
        FLUXLocationType fluxLocation3 = new FLUXLocationType().withID(new IDType().withValue("3"));
        List<FLUXLocationType> fluxLocations = Arrays.asList(fluxLocation1, fluxLocation2, fluxLocation3);

        assertEquals(Arrays.asList("1", "3"), converter.convertTo(fluxLocations, null, null));
    }

    @Test
    public void convertToWhenEverythingIsGood() throws Exception {
        FLUXLocationType fluxLocation1 = new FLUXLocationType().withID(new IDType().withValue("1"));
        FLUXLocationType fluxLocation2 = new FLUXLocationType().withID(new IDType().withValue("2"));
        FLUXLocationType fluxLocation3 = new FLUXLocationType().withID(new IDType().withValue("3"));
        List<FLUXLocationType> fluxLocations = Arrays.asList(fluxLocation1, fluxLocation2, fluxLocation3);

        assertEquals(Arrays.asList("1", "2", "3"), converter.convertTo(fluxLocations, null, null));
    }

}