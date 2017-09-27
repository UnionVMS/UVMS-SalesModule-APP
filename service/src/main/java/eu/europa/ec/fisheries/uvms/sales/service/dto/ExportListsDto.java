package eu.europa.ec.fisheries.uvms.sales.service.dto;

import java.util.List;

public class ExportListsDto {
    private boolean exportAll;
    private List<String> ids;
    private PageCriteriaDto<ReportQueryFilterDto> criteria;

    public boolean isExportAll() {
        return exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public PageCriteriaDto<ReportQueryFilterDto> getCriteria() {
        return criteria;
    }

    public void setCriteria(PageCriteriaDto<ReportQueryFilterDto> criteria) {
        this.criteria = criteria;
    }

    public ExportListsDto exportAll(final boolean exportAll) {
        setExportAll(exportAll);
        return this;
    }

    public ExportListsDto ids(final List<String> ids) {
        setIds(ids);
        return this;
    }

    public ExportListsDto criteria(final PageCriteriaDto<ReportQueryFilterDto> criteria) {
        setCriteria(criteria);
        return this;
    }

}
