package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.schema.sales.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SalesQueryParameterTypeCustomMapperTest {

    private SalesQueryParameterTypeCustomMapper customMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() {
        customMapper = new SalesQueryParameterTypeCustomMapper();
    }

    @Test
    public void mapAtoBWhenParameterWithTypeButNoValue() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("VESSEL"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        //mock
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Query parameter with type VESSEL does not have a value.");

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, new ReportQuery(), null);
    }

    @Test
    public void mapAtoBWhenInvalidParameter() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("INVALID"))
                .withValueCode(new CodeType().withValue("bla"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        //mock
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Cannot map query parameter INVALID");

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, new ReportQuery(), null);
    }

    @Test
    public void mapAtoBWhenParameterTypeIsVESSELAndValueIsACode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("VESSEL"))
                .withValueCode(new CodeType().withValue("TestVessel"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("TestVessel", reportQuery.getFilters().getVesselName());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsVESSELAndValueIsAnID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("VESSEL"))
                .withValueID(new IDType().withValue("TestVessel"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("TestVessel", reportQuery.getFilters().getVesselName());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsFLAGAndValueIsACode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("FLAG"))
                .withValueCode(new CodeType().withValue("BEL"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("BEL", reportQuery.getFilters().getFlagState());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsFLAGAndValueIsAnID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("FLAG"))
                .withValueID(new IDType().withValue("FRA"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("FRA", reportQuery.getFilters().getFlagState());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsACodeWithValueFLAG() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueCode(new CodeType().withValue("FLAG"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("BEL"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("BEL", reportQuery.getFilters().getFlagState());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsAnIDWithValueFLAG() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueID(new IDType().withValue("FLAG"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("FRA"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
            .withSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("FRA", reportQuery.getFilters().getFlagState());
    }


    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsACodeWithValueLAND() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueCode(new CodeType().withValue("LAND"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("BEL"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("BEL", reportQuery.getFilters().getLandingCountry());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsAnIDWithValueLAND() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueID(new IDType().withValue("LAND"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("FRA"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("FRA", reportQuery.getFilters().getLandingCountry());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsACodeWithValueINT() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueCode(new CodeType().withValue("INT"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("EU"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertNull(reportQuery.getFilters().getLandingCountry());
        assertNull(reportQuery.getFilters().getSalesLocation());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsAnIDWithValueINT() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueID(new IDType().withValue("INT"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("EU"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertNull(reportQuery.getFilters().getLandingCountry());
        assertNull(reportQuery.getFilters().getFlagState());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsAnInvalidCode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueCode(new CodeType().withValue("INVALID"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("EU"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //mock
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot map a query parameter of type \"ROLE\" and value \"INVALID\"");

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);
    }

    @Test
    public void mapAtoBWhenParameterTypeIsROLEAndValueIsAnInvalidID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("ROLE"))
                .withValueID(new IDType().withValue("INVALID"));

        FLUXPartyType submitter = new FLUXPartyType()
                .withIDS(new IDType().withValue("EU"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter)
                .withSubmitterFLUXParty(submitter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage()
                .withSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //mock
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot map a query parameter of type \"ROLE\" and value \"INVALID\"");

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);
    }

    @Test
    public void mapAtoBWhenParameterTypeIsPLACEAndValueIsACode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("PLACE"))
                .withValueCode(new CodeType().withValue("BEL"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("BEL", reportQuery.getFilters().getSalesLocation());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsPLACEAndValueIsAnID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("PLACE"))
                .withValueID(new IDType().withValue("FRA"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("FRA", reportQuery.getFilters().getSalesLocation());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsSALES_IDAndValueIsACode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("SALES_ID"))
                .withValueCode(new CodeType().withValue("abc"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals(1, reportQuery.getFilters().getIncludeFluxReportIds().size());
        assertEquals("abc", reportQuery.getFilters().getIncludeFluxReportIds().get(0));
    }

    @Test
    public void mapAtoBWhenParameterTypeIsSALES_IDAndValueIsAnID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("SALES_ID"))
                .withValueID(new IDType().withValue("abc"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals(1, reportQuery.getFilters().getIncludeFluxReportIds().size());
        assertEquals("abc", reportQuery.getFilters().getIncludeFluxReportIds().get(0));
    }

    @Test
    public void mapAtoBWhenParameterTypeIsTRIP_IDAndValueIsACode() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("TRIP_ID"))
                .withValueCode(new CodeType().withValue("abc"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("abc", reportQuery.getFilters().getTripId());
    }

    @Test
    public void mapAtoBWhenParameterTypeIsTRIP_IDAndValueIsAnID() throws Exception {
        //data set
        SalesQueryParameterType parameter = new SalesQueryParameterType()
                .withTypeCode(new CodeType().withValue("TRIP_ID"))
                .withValueID(new IDType().withValue("abc"));

        SalesQueryType salesQuery = new SalesQueryType()
                .withSimpleSalesQueryParameters(parameter);

        FLUXSalesQueryMessage fluxSalesQueryMessage = new FLUXSalesQueryMessage();
        fluxSalesQueryMessage.setSalesQuery(salesQuery);

        ReportQuery reportQuery = new ReportQuery()
                .withFilters(new ReportQueryFilter());

        //execute
        customMapper.mapAtoB(fluxSalesQueryMessage, reportQuery, null);

        //assert
        assertEquals("abc", reportQuery.getFilters().getTripId());
    }

}