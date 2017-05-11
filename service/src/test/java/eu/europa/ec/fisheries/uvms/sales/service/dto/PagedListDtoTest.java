package eu.europa.ec.fisheries.uvms.sales.service.dto;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.sales.Paging;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class PagedListDtoTest {

    @Test
    public void testConstructorWithFields() {
        FluxReportDto fluxReportDto = new FluxReportDto();
        ArrayList<FluxReportDto> fluxReportDtos = Lists.newArrayList(fluxReportDto);
        PagedListDto<FluxReportDto> pagedListDto = new PagedListDto<>(2, 10, 15, fluxReportDtos);

        assertSame(fluxReportDtos, pagedListDto.getItems());
        assertEquals(2, pagedListDto.getCurrentPage());
        assertEquals(10, pagedListDto.getRowsPerPage());
        assertEquals(15, pagedListDto.getTotalNumberOfRecords());
        assertEquals(2, pagedListDto.getTotalNumberOfPages());
    }

    @Test
    public void testConstructorWithFluxQueryWithPaging() {
        Paging paging = new Paging()
                .withPage(2)
                .withItemsPerPage(10);
        ReportQuery reportQuery = new ReportQuery()
                .withPaging(paging);

        FluxReportDto fluxReportDto = new FluxReportDto();
        ArrayList<FluxReportDto> fluxReportDtos = Lists.newArrayList(fluxReportDto);

        PagedListDto<FluxReportDto> pagedListDto = new PagedListDto<>(reportQuery, 15, fluxReportDtos);

        assertSame(fluxReportDtos, pagedListDto.getItems());
        assertEquals(2, pagedListDto.getCurrentPage());
        assertEquals(10, pagedListDto.getRowsPerPage());
        assertEquals(15, pagedListDto.getTotalNumberOfRecords());
        assertEquals(2, pagedListDto.getTotalNumberOfPages());
    }

    @Test
    public void testConstructorWithFluxQueryWithoutPaging() {
        ReportQuery reportQuery = new ReportQuery();

        ArrayList<FluxReportDto> fluxReportDtos = Lists.newArrayList(new FluxReportDto(), new FluxReportDto(), new FluxReportDto());

        PagedListDto<FluxReportDto> pagedListDto = new PagedListDto<>(reportQuery, 15, fluxReportDtos);

        assertSame(fluxReportDtos, pagedListDto.getItems());
        assertEquals(1, pagedListDto.getCurrentPage());
        assertEquals(15, pagedListDto.getRowsPerPage());
        assertEquals(15, pagedListDto.getTotalNumberOfRecords());
        assertEquals(1, pagedListDto.getTotalNumberOfPages());
    }

}