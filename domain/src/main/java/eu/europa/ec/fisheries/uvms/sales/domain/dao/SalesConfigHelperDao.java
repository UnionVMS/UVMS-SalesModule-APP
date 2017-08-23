package eu.europa.ec.fisheries.uvms.sales.domain.dao;

import javax.persistence.EntityManager;

/**
 * In order to sync settings with the Config module, the SalesConfigHelper needs access to an entity manager.
 * This dao will expose the entity manager. Use with care.
 */
public interface SalesConfigHelperDao {
    EntityManager getEntityManager();
}
