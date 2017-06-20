package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.dto.ReportListExportDto;

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

    /**
     * @param reportListExportDtos The dto's to be exported.
     * @return
     * @throws ServiceException
     */
    public List<List<String>> exportToList(List<ReportListExportDto> reportListExportDtos) throws ServiceException {
        List<List<String>> lists = new ArrayList<>();

        for (ReportListExportDto reportListExportDto : reportListExportDtos) {
            List<String> values = new ArrayList<>();
            lists.add(values);

            for (Field field : reportListExportDto.getClass().getDeclaredFields()) {
                values.add(getValueFromField(reportListExportDto, field));
            }

        }

        return lists;
    }

    /**
     * Uses reflection to get the value of every property in the {@link ReportListExportDto}.
     * @param reportListExportDto
     * @param field
     * @return value of the field in the dto.
     */
    private String getValueFromField(ReportListExportDto reportListExportDto, Field field) {
        field.setAccessible(true);

        String stringFromField = getStringFromField(reportListExportDto, field);

        field.setAccessible(false);

        return stringFromField;
    }

    private String getStringFromField(ReportListExportDto reportListExportDto, Field field) {
        try {
            Object object = field.get(reportListExportDto);

            if (object == null) {
                return "";
            }

            return object.toString();
        } catch (IllegalAccessException e) {
            // This is never going to happen because the fields are made accessible before calling this method
            e.printStackTrace();
            return "";
        }
    }
}
