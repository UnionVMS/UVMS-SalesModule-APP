package eu.europa.ec.fisheries.uvms.sales.service.predicate;

import com.google.common.base.Predicate;
import org.joda.time.DateTime;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.annotation.Nullable;
import java.util.List;

//TODO
public class ObjectRepresentationDateFilter implements Predicate<ObjectRepresentation> {

    private DateTime dateToFilterOn;

    public ObjectRepresentationDateFilter(DateTime date) {
        this.dateToFilterOn = date;
    }

    @Override
    public boolean apply(@Nullable ObjectRepresentation objectRepresentation) {
        List<ColumnDataType> fields = objectRepresentation.getFields();
        if (fields == null) {
            return false;
        } else {
            for (ColumnDataType field : fields) {
                if ("validity".equals(field.getColumnName())) {
                    System.out.println(field.getColumnValue());
                }
            }
            return false;
        }
    }
}
