package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListExportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Class who's only purpose is to hide low-level logic from the ReportServiceBean, concerning the export functionality.
 * Should not be used by any other class!
 */
@Stateless
public class ReportServiceExportHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceExportHelper.class);

    /**
     * @param reportListExportDtos The dto's to be exported.
     * @return
     * @throws SalesServiceException
     */
    public List<List<String>> exportToList(List<ReportListExportDto> reportListExportDtos) {
        List<List<String>> lists = new ArrayList<>();

        for (ReportListExportDto reportListExportDto : reportListExportDtos) {
            List<String> values = new ArrayList<>();
            lists.add(values);

            for (Field field : reportListExportDto.getClass().getFields()) {
                values.add(getStringFromField(reportListExportDto, field));
            }
        }

        return lists;
    }

    private String getStringFromField(ReportListExportDto reportListExportDto, Field field) {
        try {
            Object object = field.get(reportListExportDto);

            if (object == null) {
                return "";
            }

            return object.toString();
        } catch (IllegalAccessException e) {
            // All fields should be public. See comments on DTO.
            LOG.error("Very weird bug. All fields should be public of ReportListExportDto!", e);
            return "";
        }
    }
}
