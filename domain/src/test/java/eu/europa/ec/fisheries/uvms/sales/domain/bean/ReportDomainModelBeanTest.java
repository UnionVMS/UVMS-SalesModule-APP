package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.ReportMother;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportDomainModelBeanTest {

    @Mock
    private MapperFacade mapper;

    @Mock
    private FluxReportDao fluxReportDao;

    @Mock
    private ReportHelper reportHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private ReportDomainModelBean reportDomainModelBean;

    @Test
    public void testFindSalesDetailsWhenReport() {
        String extId = "extId";
        FluxReport fluxReportWithDetails = new FluxReport()
                .extId(extId)
                .purpose(Purpose.CORRECTION);
        Report expected = new Report()
            .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                    .withFLUXReportDocument(new FLUXReportDocumentType().withIDS(new IDType().withValue(extId))));

        when(fluxReportDao.findDetailsByExtId(extId)).thenReturn(fluxReportWithDetails);
        when(mapper.map(fluxReportWithDetails, Report.class)).thenReturn(expected);

        Report actual = reportDomainModelBean.findSalesDetails(extId);

        verify(fluxReportDao).findDetailsByExtId(extId);
        verify(mapper).map(fluxReportWithDetails, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertSame(expected, actual);
    }

    @Test
    public void testFindSalesDetailsWhenExtIdIsBlank() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("extId cannot be null or blank");

        reportDomainModelBean.findSalesDetails("");
    }

    @Test(expected = NullPointerException.class)
    public void testSearchWhenArgumentIsNull() {
        reportDomainModelBean.search(null, false);
    }

    @Test
    public void testSearchWhenSuccess() {
        ReportQuery reportQuery = new ReportQuery();
        List<FluxReport> fluxReports = new ArrayList<>();
        List<ReportSummary> expected = new ArrayList<>();

        when(fluxReportDao.search(reportQuery, false)).thenReturn(fluxReports);
        when(mapper.mapAsList(fluxReports, ReportSummary.class)).thenReturn(expected);

        List<ReportSummary> actual = reportDomainModelBean.search(reportQuery, false);

        verify(fluxReportDao).search(reportQuery, false);
        verify(mapper).mapAsList(fluxReports, ReportSummary.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertSame(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void searchIncludingDetailsWhenArgumentIsNull() {
        reportDomainModelBean.searchIncludingDetails(null);
    }

    @Test
    public void searchIncludingDetailsWhenSuccess() {
        ReportQuery reportQuery = new ReportQuery();
        List<FluxReport> fluxReports = new ArrayList<>();
        List<Report> expected = new ArrayList<>();

        when(fluxReportDao.search(reportQuery)).thenReturn(fluxReports);
        when(mapper.mapAsList(fluxReports, Report.class)).thenReturn(expected);

        List<Report> actual = reportDomainModelBean.searchIncludingDetails(reportQuery);

        verify(fluxReportDao).search(reportQuery);
        verify(mapper).mapAsList(fluxReports, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertSame(expected, actual);
    }

    @Test
    public void testFindByExtIdWhenSuccess() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport();
        Report report = new Report();

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);
        doReturn(report).when(mapper).map(fluxReport, Report.class);

        Optional<Report> result = reportDomainModelBean.findByExtId(extId);

        verify(fluxReportDao).findByExtId(extId);
        verify(mapper).map(fluxReport, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertTrue(result.isPresent());
        assertSame(report, result.get());
    }

    @Test
    public void testFindByExtIdWhenNothingFound() {
        String extId = "extId";

        doReturn(Optional.absent()).when(fluxReportDao).findByExtId(extId);

        Optional<Report> result = reportDomainModelBean.findByExtId(extId);

        verify(fluxReportDao).findByExtId(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByExtIdWhenFoundCorrectedReport() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport()
                .correction(new DateTime());

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);

        Optional<Report> result = reportDomainModelBean.findByExtId(extId);

        verify(fluxReportDao).findByExtId(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByExtIdWhenFoundDeletedReport() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport()
                .deletion(new DateTime());

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);

        Optional<Report> result = reportDomainModelBean.findByExtId(extId);

        verify(fluxReportDao).findByExtId(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertFalse(result.isPresent());
    }

    @Test
    public void findCorrectionOfWhenSomethingFound() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport();
        Report report = new Report();

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findCorrectionOf(extId);
        doReturn(report).when(mapper).map(fluxReport, Report.class);

        Optional<Report> result = reportDomainModelBean.findCorrectionOf(extId);

        verify(fluxReportDao).findCorrectionOf(extId);
        verify(mapper).map(fluxReport, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertEquals(Optional.of(report), result);
    }

    @Test
    public void findCorrectionOfWhenNothingFound() {
        String extId = "extId";

        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOf(extId);

        Optional<Report> result = reportDomainModelBean.findCorrectionOf(extId);

        verify(fluxReportDao).findCorrectionOf(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertEquals(Optional.<Report>absent(), result);
    }

    @Test
    public void findOlderVersionsOrderedByCreationDateDescending() {
        //data set
        DateTime creationReport1 = new DateTime(2017, 6, 8, 13, 45);
        DateTime creationReport2 = new DateTime(2017, 6, 9, 13, 45);

        FluxReport fluxReport2 = new FluxReport()
                .id(2)
                .extId("fluxReport2")
                .previousFluxReportExtId(null)
                .creation(creationReport2);
        FluxReport fluxReport1 = new FluxReport()
                .id(1)
                .extId("fluxReport1")
                .previousFluxReportExtId("fluxReport2")
                .creation(creationReport1);

        ReportSummary report1 = new ReportSummary()
                .withExtId("fluxReport1")
                .withReferencedId("fluxReport2")
                .withCreation(creationReport1);
        ReportSummary report2 = new ReportSummary()
                .withExtId("fluxReport2")
                .withReferencedId(null)
                .withCreation(creationReport2);

        //mock
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findByExtId("fluxReport1");
        doReturn(report1).when(mapper).map(fluxReport1, ReportSummary.class);

        doReturn(Optional.of(fluxReport2)).when(fluxReportDao).findByExtId("fluxReport2");
        doReturn(report2).when(mapper).map(fluxReport2, ReportSummary.class);

        //execute
        List<ReportSummary> allReferencedReports = reportDomainModelBean.findOlderVersionsOrderedByCreationDateDescending("fluxReport1");

        //verify and assert
        verify(fluxReportDao).findByExtId("fluxReport1");
        verify(mapper).map(fluxReport1, ReportSummary.class);

        verify(fluxReportDao).findByExtId("fluxReport2");
        verify(mapper).map(fluxReport2, ReportSummary.class);

        verifyNoMoreInteractions(fluxReportDao, reportHelper, mapper);

        assertEquals(2, allReferencedReports.size());
        assertSame(report2, allReferencedReports.get(0));
        assertSame(report1, allReferencedReports.get(1));
    }

    @Test
    public void findOlderVersionsOrderedByCreationDateDescendingIncludingDetailsWhenArgumentIsNull() {
        //data set
        DateTime creationReport1 = new DateTime(2017, 6, 8, 13, 45);
        DateTime creationReport2 = new DateTime(2017, 6, 9, 13, 45);

        FluxReport fluxReport2 = new FluxReport()
                .id(2)
                .extId("fluxReport2")
                .previousFluxReportExtId(null)
                .creation(creationReport2);
        FluxReport fluxReport1 = new FluxReport()
                .id(1)
                .extId("fluxReport1")
                .previousFluxReportExtId("fluxReport2")
                .creation(creationReport1);

        Report report1 = ReportMother.with("fluxReport1", "fluxReport2");
        Report report2 = ReportMother.withId("fluxReport2");

        //mock
        doReturn("fluxReport1").when(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);

        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findByExtId("fluxReport1");
        doReturn(report1).when(mapper).map(fluxReport1, Report.class);

        doReturn(Optional.of(fluxReport2)).when(fluxReportDao).findByExtId("fluxReport2");
        doReturn(report2).when(mapper).map(fluxReport2, Report.class);

        doReturn(creationReport1).when(reportHelper).getCreationDate(report1);
        doReturn(creationReport2).when(reportHelper).getCreationDate(report2);

        //execute
        List<Report> allReferencedReports = reportDomainModelBean.findOlderVersionsOrderedByCreationDateDescendingIncludingDetails(report1);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);

        verify(fluxReportDao).findByExtId("fluxReport1");
        verify(mapper).map(fluxReport1, Report.class);

        verify(fluxReportDao).findByExtId("fluxReport2");
        verify(mapper).map(fluxReport2, Report.class);
        verify(reportHelper).getCreationDate(report1);
        verify(reportHelper).getCreationDate(report2);

        verifyNoMoreInteractions(fluxReportDao, reportHelper, mapper);

        assertEquals(2, allReferencedReports.size());
        assertSame(report2, allReferencedReports.get(0));
        assertSame(report1, allReferencedReports.get(1));
    }

    @Test
    public void isLatestVersionWhenFalse() {
        Report report = new Report();
        String extId = "extId";

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.of(new FluxReport())).when(fluxReportDao).findCorrectionOf(extId);

        assertFalse(reportDomainModelBean.isLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao);
    }

    @Test
    public void isLatestVersionWhenTrue() {
        Report report = new Report();
        String extId = "extId";

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOf(extId);

        assertTrue(reportDomainModelBean.isLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao);
    }

    @Test
    public void findLatestVersionWhenProvidedReportIsLatest() {
        String extId = "extId";
        Report report = new Report();

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOf(extId);

        assertSame(report, reportDomainModelBean.findLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);
    }

    @Test
    public void findLatestVersionWhenProvidedReportHasANewerVersion() {
        String extId = "extId";
        Report report1 = ReportMother.withExtId("1");
        Report report2 = ReportMother.withExtId("2");
        FluxReport fluxReport1 = new FluxReport().id(1);
        FluxReport fluxReport2 = new FluxReport().id(2);

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report1);
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findCorrectionOf(extId);
        doReturn(fluxReport2).when(fluxReportDao).findLatestVersion(fluxReport1);
        doReturn(report2).when(mapper).map(fluxReport2, Report.class);

        assertSame(report2, reportDomainModelBean.findLatestVersion(report1));

        verify(reportHelper).getFLUXReportDocumentId(report1);
        verify(fluxReportDao).findCorrectionOf(extId);
        verify(fluxReportDao).findLatestVersion(fluxReport1);
        verify(mapper).map(fluxReport2, Report.class);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);
    }

    @Test
    public void testFindRelatedReportsOf() {
        //data set
        Report report = new Report();
        String extId = "extId";

        FluxReport relatedFluxReport1 = new FluxReport().extId("1");
        FluxReport relatedFluxReport2 = new FluxReport().extId("2");

        FluxReport fluxReport = new FluxReport().relatedSalesNotes(Arrays.asList(relatedFluxReport1))
                                                .relatedTakeOverDocuments(Arrays.asList(relatedFluxReport2));

        Report relatedReport1 = ReportMother.withExtId("1");
        Report relatedReport2 = ReportMother.withExtId("2");

        List<FluxReport> relatedFluxReports = Arrays.asList(relatedFluxReport1, relatedFluxReport2);
        List<Report> relatedReports = Arrays.asList(relatedReport1, relatedReport2);

        //mock
        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);
        doReturn(relatedReports).when(mapper).mapAsList(relatedFluxReports, Report.class);

        //execute
        List<Report> result = reportDomainModelBean.findRelatedReportsOf(report);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findByExtId(extId);
        verify(mapper).mapAsList(relatedFluxReports, Report.class);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);

        assertSame(relatedReports, result);
    }

}