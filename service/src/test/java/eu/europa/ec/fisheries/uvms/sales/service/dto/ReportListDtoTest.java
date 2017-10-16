package eu.europa.ec.fisheries.uvms.sales.service.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReportListDtoTest {

    @Test
    public void testThatBuyerAndProviderHaveADefaultValue() {
        ReportListDto reportListDto = new ReportListDto();
        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getProvider());
    }

    @Test
    public void testThatBuyerAndProviderHavaADefaultValueWhenYouTryToOverrideTheDefaultsWithNullWithSetters() {
        ReportListDto reportListDto = new ReportListDto();

        reportListDto.setProvider(null);
        reportListDto.setBuyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getProvider());
    }

    @Test
    public void testThatBuyerAndProviderHaveADefaultValueWhenYouTryToOverrideAnEarlierSetValueWithNullWithSetters() {
        ReportListDto reportListDto = new ReportListDto();
        reportListDto.setProvider("Stijn");
        reportListDto.setBuyer("Mathias");

        reportListDto.setProvider(null);
        reportListDto.setBuyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getProvider());
    }

    @Test
    public void testThatDefaultValuesForBuyerAndProviderCanBeOverwrittenWithSetters() {
        ReportListDto reportListDto = new ReportListDto();
        reportListDto.setProvider(null);
        reportListDto.setBuyer(null);

        reportListDto.setProvider("Stijn");
        reportListDto.setBuyer("Mathias");

        assertEquals("Stijn", reportListDto.getProvider());
        assertEquals("Mathias", reportListDto.getBuyer());
    }

    @Test
    public void testThatBuyerAndProviderHaveADefaultValueWhenYouTryToOverrideTheDefaultsWithNullWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .provider(null)
                .buyer(null);
        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getProvider());
    }

    @Test
    public void testThatBuyerAndProviderHaveADefaultValueWhenYouTryToOverrideAnEarlierSetValueWithNullWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .provider("Stijn")
                .buyer("Mathias")

                .provider(null)
                .buyer(null);

        assertEquals("N/A", reportListDto.getBuyer());
        assertEquals("N/A", reportListDto.getProvider());
    }

    @Test
    public void testThatDefaultValuesForBuyerAndProviderCanBeOverwrittenWithFluentAPI() {
        ReportListDto reportListDto = new ReportListDto()
                .provider(null)
                .buyer(null)

                .provider("Stijn")
                .buyer("Mathias");

        assertEquals("Stijn", reportListDto.getProvider());
        assertEquals("Mathias", reportListDto.getBuyer());
    }
}