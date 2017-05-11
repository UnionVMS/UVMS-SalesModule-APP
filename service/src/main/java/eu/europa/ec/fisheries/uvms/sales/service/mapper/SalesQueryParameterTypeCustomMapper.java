package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.schema.sales.*;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public class SalesQueryParameterTypeCustomMapper extends CustomMapper<FLUXSalesQueryMessage, ReportQuery> {

    @Override
    public void mapAtoB(FLUXSalesQueryMessage fluxSalesQueryMessage, ReportQuery fluxReportQuery, MappingContext context) {
        SalesQueryType source = fluxSalesQueryMessage.getSalesQuery();
        ReportQueryFilter target = fluxReportQuery.getFilters();

        for (SalesQueryParameterType parameter : source.getSimpleSalesQueryParameters()) {
            String type = parameter.getTypeCode().getValue();
            String value = getValue(parameter);
            switch (type) {
                case "VESSEL":
                    target.setVesselName(value);
                    break;
                case "FLAG":
                    target.setFlagState(value);
                    break;
                case "ROLE":
                    String country = source.getSubmitterFLUXParty().getIDS().get(0).getValue();
                    updateFiltersWithRole(target, country, value);
                    break;
                case "PLACE":
                    target.setSalesLocation(value);
                    break;
                case "SALES_ID":
                    target.withIncludeFluxReportIds(value);
                    break;
                case "TRIP_ID":
                    target.setTripId(value);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Cannot map query parameter %s", type));
            }
        }
    }

    private void updateFiltersWithRole(ReportQueryFilter filters, String country, String role) {
        switch (role) {
            case "FLAG": filters.setFlagState(country); break;
            case "LAND": filters.setLandingCountry(country); break;
            case "INT": break; //no filter needed, international organizations may see every report
            default:
                throw new IllegalArgumentException(String.format("Cannot map a query parameter of type \"ROLE\" and value \"%s\"", role));
        }
    }

    private String getValue(SalesQueryParameterType parameter) {
        if (parameter.getValueCode() != null) {
            return parameter.getValueCode().getValue();
        } else if (parameter.getValueID() != null) {
            return parameter.getValueID().getValue();
        } else {
            throw new IllegalArgumentException(String.format("Query parameter with type %s does not have a value.", parameter.getTypeCode().getValue()));
        }
    }
}