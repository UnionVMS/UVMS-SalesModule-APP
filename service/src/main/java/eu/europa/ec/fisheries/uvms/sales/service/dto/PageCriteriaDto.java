package eu.europa.ec.fisheries.uvms.sales.service.dto;

import eu.europa.ec.fisheries.schema.sales.ReportQuerySortField;
import eu.europa.ec.fisheries.schema.sales.SortDirection;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PageCriteriaDto<T> {

    private int pageIndex;
    private int pageSize;
    private ReportQuerySortField sortField;
    private SortDirection sortDirection;
    private T filters;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ReportQuerySortField getSortField() {
        return sortField;
    }

    public void setSortField(ReportQuerySortField sortField) {
        this.sortField = sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public T getFilters() {
        return filters;
    }

    public void setFilters(T filters) {
        this.filters = filters;
    }

    public PageCriteriaDto<T> pageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public PageCriteriaDto<T> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageCriteriaDto<T> sortField(ReportQuerySortField sortField) {
        this.sortField = sortField;
        return this;
    }

    public PageCriteriaDto<T> sortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
        return this;
    }

    public PageCriteriaDto<T> filters(T filters) {
        this.filters = filters;
        return this;
    }

}
