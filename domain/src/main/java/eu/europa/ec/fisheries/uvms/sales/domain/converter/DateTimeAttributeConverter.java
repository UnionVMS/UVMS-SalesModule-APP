package eu.europa.ec.fisheries.uvms.sales.domain.converter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;

@Converter(autoApply = true)
public class DateTimeAttributeConverter implements AttributeConverter<DateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(DateTime dateTime) {
        return (dateTime == null ? null : new Timestamp(dateTime.withZone(DateTimeZone.UTC).getMillis()));
    }

    @Override
    public DateTime convertToEntityAttribute(Timestamp sqlDate) {
        return (sqlDate == null ? null : new DateTime(sqlDate, DateTimeZone.UTC));
    }

}
