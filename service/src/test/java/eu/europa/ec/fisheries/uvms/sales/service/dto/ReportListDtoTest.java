package eu.europa.ec.fisheries.uvms.sales.service.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReportListDtoTest {

    @Test
    public void testThatBuyerAndSellerHaveADefaultValue() {
        ReportListDto reportListDto = new ReportListDto();
        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getSeller());
    }

    @Test
    public void testThatBuyerAndSellerHavaADefaultValueWhenYouTryToOverrideTheDefaultsWithNullWithSetters() {
        ReportListDto reportListDto = new ReportListDto();

        reportListDto.setSeller(null);
        reportListDto.setBuyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getSeller());
    }

    @Test
    public void testThatBuyerAndSellerHaveADefaultValueWhenYouTryToOverrideAnEarlierSetValueWithNullWithSetters() {
        ReportListDto reportListDto = new ReportListDto();
        reportListDto.setSeller("Stijn");
        reportListDto.setBuyer("Mathias");

        reportListDto.setSeller(null);
        reportListDto.setBuyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getSeller());
    }

    @Test
    public void testThatDefaultValuesForBuyerAndSellerCanBeOverwrittenWithSetters() {
        ReportListDto reportListDto = new ReportListDto();
        reportListDto.setSeller(null);
        reportListDto.setBuyer(null);

        reportListDto.setSeller("Stijn");
        reportListDto.setBuyer("Mathias");

        assertEquals("Stijn", reportListDto.getSeller());
        assertEquals("Mathias", reportListDto.getBuyer());
    }

    @Test
    public void testThatBuyerAndSellerHaveADefaultValueWhenYouTryToOverrideTheDefaultsWithNullWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .seller(null)
                .buyer(null);
        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getSeller());
    }

    @Test
    public void testThatBuyerAndSellerHaveADefaultValueWhenYouTryToOverrideAnEarlierSetValueWithNullWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .seller("Stijn")
                .buyer("Mathias")

                .seller(null)
                .buyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getSeller());
    }

    @Test
    public void testThatDefaultValuesForBuyerAndSellerCanBeOverwrittenWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .seller(null)
                .buyer(null)

                .seller("Stijn")
                .buyer("Mathias");

        assertEquals("Stijn", reportListDto.getSeller());
        assertEquals("Mathias", reportListDto.getBuyer());
    }
}