package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceTerritory;
import org.junit.Before;
import org.junit.Test;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectRepresentationToReferenceTerritoryCustomMapperTest {

    private ObjectRepresentationToReferenceTerritoryCustomMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectRepresentationToReferenceTerritoryCustomMapper();
    }

    @Test
    public void mapAtoBWhenAllFieldsAreFilledIn() throws Exception {
        String code = "code-sdfsdf";
        String englishName = "englishName";
        ColumnDataType codeColumn = new ColumnDataType("code", code,"java.lang.String");
        ColumnDataType textColumn = new ColumnDataType("enName", englishName,"java.lang.String");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, textColumn));

        ReferenceTerritory referenceTerritory = new ReferenceTerritory();

        mapper.mapAtoB(objectRepresentation, referenceTerritory, null);

        assertEquals(code, referenceTerritory.getCode());
        assertEquals(englishName, referenceTerritory.getEnglishName());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationsIsNull() throws Exception {
        ReferenceTerritory referenceTerritory = new ReferenceTerritory();

        mapper.mapAtoB(null, referenceTerritory, null);

        assertNull(referenceTerritory.getCode());
        assertNull(referenceTerritory.getEnglishName());
    }

    @Test
    public void mapAtoBWhenTheObjectRepresentationHasNoFields() throws Exception {
        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        ReferenceTerritory referenceTerritory = new ReferenceTerritory();

        mapper.mapAtoB(objectRepresentation, referenceTerritory, null);

        assertNull(referenceTerritory.getCode());
        assertNull(referenceTerritory.getEnglishName());
    }

    @Test
    public void mapAtoBWhenOneOfTheColumnNamesIsNull() throws Exception {
        String code = "code";
        String englishName = "BE";
        ColumnDataType codeColumn = new ColumnDataType(null, code,"java.lang.String");
        ColumnDataType textColumn = new ColumnDataType("enName", englishName,"java.lang.String");

        ObjectRepresentation objectRepresentation = new ObjectRepresentation();
        objectRepresentation.setFields(Lists.newArrayList(codeColumn, textColumn));

        ReferenceTerritory referenceTerritory = new ReferenceTerritory();

        mapper.mapAtoB(objectRepresentation, referenceTerritory, null);

        assertNull(referenceTerritory.getCode());
        assertEquals(englishName, referenceTerritory.getEnglishName());
    }
}