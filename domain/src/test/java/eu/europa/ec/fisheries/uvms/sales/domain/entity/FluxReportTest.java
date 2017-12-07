package eu.europa.ec.fisheries.uvms.sales.domain.entity;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class FluxReportTest {

    @Test
    public void isCorrectedWhenTrue() {
        FluxReport fluxReport = new FluxReport()
                .correction(new DateTime());
        assertTrue(fluxReport.isCorrected());
    }

    @Test
    public void isCorrectedWhenFalse() {
        FluxReport fluxReport = new FluxReport()
                .correction(null);
        assertFalse(fluxReport.isCorrected());
    }

    @Test
    public void isDeletedWhenTrue() {
        FluxReport fluxReport = new FluxReport()
                .deletion(new DateTime());
        assertTrue(fluxReport.isDeleted());
    }

    @Test
    public void isDeletedWhenFalse() {
        FluxReport fluxReport = new FluxReport()
                .deletion(null);
        assertFalse(fluxReport.isDeleted());
    }

}