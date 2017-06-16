package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import eu.europa.ec.fisheries.schema.sales.CodeType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxProcessType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PresentationConverterTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private PresentationConverter presentationConverter;


    @Before
    public void setUp() throws Exception {
        presentationConverter = new PresentationConverter();
    }

    @Test
    public void testConvertToWhenPresentationIsPresent() throws Exception {
        List<CodeType> processTypes = new ArrayList<>();
        processTypes.add(new CodeType().withListID(FluxProcessType.FISH_PRESENTATION.toString()).withValue("GUTTED"));
        processTypes.add(new CodeType().withListID(FluxProcessType.FISH_FRESHNESS.toString()).withValue("FRESH"));
        processTypes.add(new CodeType().withListID(FluxProcessType.FISH_PRESERVATION.toString()).withValue("SALTED"));

        String presentation = presentationConverter.convert(processTypes, null, null);

        assertEquals("GUTTED", presentation);
    }

    @Test
    public void testConvertToWhenPresentationIsMissing() throws Exception {
        List<CodeType> processTypes = Arrays.asList(
                new CodeType().withListID(FluxProcessType.FISH_FRESHNESS.toString()).withValue("ROTTEN"),
                new CodeType().withListID(FluxProcessType.FISH_PRESERVATION.toString()).withValue("SALTED"));

        String presentation = presentationConverter.convert(processTypes, null, null);

        assertEquals(null, presentation);
    }

    @Test
    public void testConvertToWhenInputIsInvalid() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No process type exists for string This is invalid");

        List<CodeType> processTypes = Arrays.asList(
                new CodeType().withListID(FluxProcessType.FISH_FRESHNESS.toString()).withListID("This is invalid"));

        presentationConverter.convert(processTypes, null, null);
    }
}