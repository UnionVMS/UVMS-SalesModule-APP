package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocationObjectRepresentationToReferenceCodeCustomMapperTest {

    private LocationObjectRepresentationToReferenceCodeCustomMapper mapper;

    @Before
    public void init() {
        mapper = new LocationObjectRepresentationToReferenceCodeCustomMapper();
    }

    @Test
    public void mapAtoBWhenAllFieldsAreFilledIn() throws Exception {
        String code = "code-sdfsdf";
        String text = "text";
        ColumnDataType codeColumn = new ColumnDataType("unlo_code", code,"java.lang.String");
        ColumnDataType textColumn = new ColumnDataType("description", text,"java.lang.String");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, textColumn));

        ReferenceCode referenceCode = new ReferenceCode();

        mapper.mapAtoB(objectRepresentation, referenceCode, null);

        assertEquals(code, referenceCode.getCode());
        assertEquals(text, referenceCode.getText());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationsIsNull() throws Exception {
        ReferenceCode referenceCode = new ReferenceCode();

        mapper.mapAtoB(null, referenceCode, null);

        assertNull(referenceCode.getCode());
        assertNull(referenceCode.getText());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationHasNoFields() throws Exception {
        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        ReferenceCode referenceCode = new ReferenceCode();

        mapper.mapAtoB(objectRepresentation, referenceCode, null);

        assertNull(referenceCode.getCode());
        assertNull(referenceCode.getText());
    }

    @Test
    public void mapAtoBWhenOneOfTheColumnNamesIsNull() throws Exception {
        String code = "code";
        String text = "text";
        ColumnDataType codeColumn = new ColumnDataType(null, code,"java.lang.String");
        ColumnDataType textColumn = new ColumnDataType("description", text,"java.lang.String");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, textColumn));

        ReferenceCode referenceCode = new ReferenceCode();

        mapper.mapAtoB(objectRepresentation, referenceCode, null);

        assertNull(referenceCode.getCode());
        assertEquals(text, referenceCode.getText());
    }

    @Test
    public void mapAsList() throws Exception {
        String code1 = "code-sdfsdf";
        String text1 = "text";
        ColumnDataType codeColumn1 = new ColumnDataType("unlo_code", code1,"java.lang.String");
        ColumnDataType textColumn1 = new ColumnDataType("description", text1,"java.lang.String");
        ObjectRepresentation objectRepresentation1 = new ObjectRepresentation();
        objectRepresentation1.setFields(Lists.newArrayList(codeColumn1, textColumn1));

        String code2 = "code-kljlkjlk";
        String text2 = "lkjlk";
        ColumnDataType codeColumn2 = new ColumnDataType("unlo_code", code2,"java.lang.String");
        ColumnDataType textColumn2 = new ColumnDataType("description", text2,"java.lang.String");
        ObjectRepresentation objectRepresentation2 = new ObjectRepresentation();
        objectRepresentation2.setFields(Lists.newArrayList(codeColumn2, textColumn2));

        List<ReferenceCode> mappedLocations = mapper.mapAsList(Lists.newArrayList(objectRepresentation1, objectRepresentation2));

        assertEquals(2, mappedLocations.size());
        assertEquals(code1, mappedLocations.get(0).getCode());
        assertEquals(text1, mappedLocations.get(0).getText());

        assertEquals(code2, mappedLocations.get(1).getCode());
        assertEquals(text2, mappedLocations.get(1).getText());
    }
}