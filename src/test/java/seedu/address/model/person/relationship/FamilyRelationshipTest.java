package seedu.address.model.person.relationship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class FamilyRelationshipTest {
    @Test
    public void getRelationshipType_validRelationshipType_returnCorrectType() {
        UUID person1 = UUID.randomUUID();
        UUID person2 = UUID.randomUUID();
        String relationshipType = "siblings";
        String role1 = "sibling";
        String role2 = "sibling";

        FamilyRelationship familyRelationship = new FamilyRelationship(person1, person2, relationshipType, role1, role2);

        assertEquals(relationshipType, familyRelationship.getRelationshipType());
    }
}
