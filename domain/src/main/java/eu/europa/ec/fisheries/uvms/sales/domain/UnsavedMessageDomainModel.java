package eu.europa.ec.fisheries.uvms.sales.domain;

public interface UnsavedMessageDomainModel {

    /**
     * Saves a record of the event that a message came
     * into sales which was deemed erroneous by a previous
     * module (like the Rules module), or was a 'delete' message.
     *
     * @param extId the id of the message
     */
    void save(String extId);

    /**
     * Does a record of an unsaved message with the given id exist?
     *
     * @param extId the id of the message
     */
    boolean exists(String extId);


}
