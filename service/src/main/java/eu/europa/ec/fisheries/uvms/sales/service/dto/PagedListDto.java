package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.Paging;
import eu.europa.ec.fisheries.schema.sales.ReportQuery;

import java.util.List;

public class PagedListDto<T> {

    private final int currentPage;
    private final int totalNumberOfPages;
    private final long rowsPerPage;
    private final long totalNumberOfRecords;
    private final List<T> items;

    public PagedListDto(int currentPage, int rowsPerPage, long totalNumberOfRecords, List<T> items) {
        this.currentPage = currentPage;
        this.rowsPerPage = rowsPerPage;
        this.totalNumberOfRecords = totalNumberOfRecords;
        this.items = items;
        this.totalNumberOfPages = calculateTotalNumberOfPages();
    }

    public PagedListDto(ReportQuery query, long totalNumberOfRecords, List<T> items) {
        Paging paging = query.getPaging();
        if (paging != null) {
            this.currentPage = paging.getPage();
            this.rowsPerPage = paging.getItemsPerPage();
        } else {
            this.currentPage = 1;
            this.rowsPerPage = totalNumberOfRecords;
        }

        this.totalNumberOfRecords = totalNumberOfRecords;
        this.items = items;
        this.totalNumberOfPages = calculateTotalNumberOfPages();
    }

    private int calculateTotalNumberOfPages() {
        return new Double(Math.ceil((double)totalNumberOfRecords / rowsPerPage)).intValue();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public long getRowsPerPage() {
        return rowsPerPage;
    }

    public long getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public List<T> getItems() {
        return items;
    }
}
