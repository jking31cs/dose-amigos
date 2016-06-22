package info.doseamigos.meds;

/**
 * Service for handling all business logic associated with Meds.
 */
public interface MedService {

    /**
     * Adds a new medication to the user's list by looking it up by name in the external api, then populating the
     * AmigoUser and rxcui afterward.
     * @param name The Name of the med to look up.
     * @return A new Med object for the user with the name.
     */
    Med addByName(String name);
}
