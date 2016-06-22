package info.doseamigos.meds;

import info.doseamigos.amigousers.AmigoUser;

/**
 * Mock service for the Meds logic.
 */
public class MockMedService implements MedService{
    @Override
    public Med addByName(String name) {
        return new Med(
            1,
            new AmigoUser(1, "test"),
            1,
            name
        );
    }
}
