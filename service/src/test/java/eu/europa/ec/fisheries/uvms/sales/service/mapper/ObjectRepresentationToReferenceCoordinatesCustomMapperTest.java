package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectRepresentationToReferenceCoordinatesCustomMapperTest {

    private ObjectRepresentationToReferenceCoordinatesCustomMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectRepresentationToReferenceCoordinatesCustomMapper();
    }

    @Test
    public void mapAtoBWhenAllFieldsAreFilledIn() throws Exception {
        String code = "code-sdfsdf";
        String latitude = "12.54";
        String longitude = "2.8975412";
        ColumnDataType codeColumn = new ColumnDataType("code", code,"java.lang.String");
        ColumnDataType latitudeColumn = new ColumnDataType("latitude", latitude,"java.lang.Double");
        ColumnDataType longitudeColumn = new ColumnDataType("longitude", longitude,"java.lang.Double");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, latitudeColumn, longitudeColumn));

        ReferenceCoordinates referenceCoordinates = new ReferenceCoordinates();

        mapper.mapAtoB(objectRepresentation, referenceCoordinates, null);

        assertEquals(code, referenceCoordinates.getLocationCode());
        assertEquals(Double.valueOf(latitude), referenceCoordinates.getLatitude());
        assertEquals(Double.valueOf(longitude), referenceCoordinates.getLongitude());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationsIsNull() throws Exception {
        ReferenceCoordinates referenceCoordinates = new ReferenceCoordinates();

        mapper.mapAtoB(null, referenceCoordinates, null);

        assertNull(referenceCoordinates.getLocationCode());
        assertNull(referenceCoordinates.getLatitude());
        assertNull(referenceCoordinates.getLongitude());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationHasNoFields() throws Exception {
        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        ReferenceCoordinates referenceCoordinates = new ReferenceCoordinates();

        mapper.mapAtoB(objectRepresentation, referenceCoordinates, null);

        assertNull(referenceCoordinates.getLocationCode());
        assertNull(referenceCoordinates.getLatitude());
        assertNull(referenceCoordinates.getLongitude());;
    }

    @Test
    public void mapAtoBWhenOneOfTheColumnNamesIsNull() throws Exception {
        String code = "code";
        String latitude = "12.54";
        String longitude = "2.8975412";
        ColumnDataType codeColumn = new ColumnDataType(null, code,"java.lang.String");
        ColumnDataType latitudeColumn = new ColumnDataType("latitude", latitude,"java.lang.Double");
        ColumnDataType longitudeColumn = new ColumnDataType("longitude", longitude,"java.lang.Double");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, latitudeColumn, longitudeColumn));

        ReferenceCoordinates referenceCoordinates = new ReferenceCoordinates();

        mapper.mapAtoB(objectRepresentation, referenceCoordinates, null);

        assertNull(referenceCoordinates.getLocationCode());
        assertEquals(Double.valueOf(latitude), referenceCoordinates.getLatitude());
        assertEquals(Double.valueOf(longitude), referenceCoordinates.getLongitude());
    }
}