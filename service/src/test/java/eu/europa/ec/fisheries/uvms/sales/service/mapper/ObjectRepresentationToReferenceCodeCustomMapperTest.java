package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectRepresentationToReferenceCodeCustomMapperTest {

    private ObjectRepresentationToReferenceCodeCustomMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectRepresentationToReferenceCodeCustomMapper();
    }

    @Test
    public void mapAtoBWhenAllFieldsAreFilledIn() throws Exception {
        String code = "code-sdfsdf";
        String text = "text";
        ColumnDataType codeColumn = new ColumnDataType("code", code,"java.lang.String");
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
}