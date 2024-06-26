package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ResultContainer;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.relationship.Relationship;
import seedu.address.model.person.relationship.RelationshipUtil;
import seedu.address.model.person.relationship.RoleBasedRelationship;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Relationship> filteredRelationships;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredRelationships = new FilteredList<>(this.addressBook.getRelationshipList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public boolean hasRelationship(Relationship target) {
        return addressBook.hasRelationship(target);
    }
    @Override
    public boolean hasRelationshipWithDescriptor(Relationship target) {
        return addressBook.hasRelationshipWithDescriptor(target);
    }
    @Override
    public void addRelationship(Relationship toAdd) {
        addressBook.addRelationship(toAdd);
    }
    @Override
    public void deleteRelationship(Relationship toDelete) {
        addressBook.deleteRelationship(toDelete);
    }

    public String showRelationshipTypes() {
        return Relationship.showRelationshipTypes();
    }

    public void deleteRelationType(String relationType) {
        addressBook.deleteRelationType(relationType);
    }

    public String getExistingRelationship(Relationship toGet) {
        return addressBook.getExistingRelationship(toGet);
    }

    @Override
    public void deleteRelationshipsOfPerson(UUID personUuid) {
        addressBook.deleteRelationshipsOfPerson(personUuid);
    }

    @Override
    public ResultContainer anySearch(UUID originUuid, UUID targetUuid) {
        return addressBook.anySearch(originUuid, targetUuid);
    }

    @Override
    public ResultContainer familySearch(UUID originUuid, UUID targetUuid) {
        return addressBook.familySearch(originUuid, targetUuid);
    }

    @Override
    public void addRolelessDescriptor(String newRelationshipDescriptor) {
        RelationshipUtil.addRolelessDescriptor(newRelationshipDescriptor);
    }

    @Override
    public void addRolebasedDescriptor(String newRelationshipDescriptor, String role1, String role2) {
        RelationshipUtil.addRoleBasedDescriptor(newRelationshipDescriptor, role1, role2);
    }

    @Override
    public void resetRelationshipDescriptors() {
        RelationshipUtil.resetRelationshipDescriptors();
    }

    @Override
    public boolean hasRelationshipWithRoles(RoleBasedRelationship relationship, UUID uuid, UUID uuid2) {
        return addressBook.hasRelationshipWithRoles(relationship, uuid, uuid2);
    }

    /**
     * Returns an unmodifiable view of the list of {@code Relationship} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Relationship> getFilteredRelationshipList() {
        return filteredRelationships;
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }
    @Override
    public void updateFilteredRelationshipList(Predicate<Relationship> predicate) {
        requireNonNull(predicate);
        filteredRelationships.setPredicate(predicate);
    }
    @Override
    public UUID getFullUuid(String digits) {
        return addressBook.getFullUuid(digits);
    }

    @Override
    public Person getPersonByUuid(UUID id) throws CommandException {
        return addressBook.getPersonByUuid(id);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

    @Override
    public void deleteAttribute(String uuid, String attributeName) {
        addressBook.deleteAttribute(uuid, attributeName);
    }

    @Override
    public boolean hasAttribute(String uuidString, String attributeName) {
        return addressBook.hasAttribute(uuidString, attributeName);
    }

    @Override
    public RoleBasedRelationship checkSiblingsSpousesGender(Model model, String originUuid,
                                                            String targetUuid, String rolePerson1, String rolePerson2,
                                                            Boolean isSiblings) throws CommandException {
        return addressBook.checkSiblingsSpousesGender(model, originUuid, targetUuid, rolePerson1,
                rolePerson2, isSiblings);
    }

    @Override
    public void genderCheck(UUID uuid, String gender) throws CommandException {
        addressBook.genderCheck(uuid, gender);
    }

    @Override
    public void genderMatch(String rolePerson1, String uuid, String uuidShort) {
        addressBook.genderMatch(rolePerson1, uuid, uuidShort);
    }

    @Override
    public RoleBasedRelationship getRelationshipRoleBased(UUID fullOriginUuid, UUID fullTargetUuid, Model model,
                                                          String originUuid, String targetUuid, String rolePerson1,
                                                          String rolePerson2,
                                                          String relationshipDescriptor) throws CommandException {
        return addressBook.getRelationshipRoleBased(fullOriginUuid, fullTargetUuid, model, originUuid, targetUuid,
                rolePerson1, rolePerson2, relationshipDescriptor);
    }

    @Override
    public void validateRoleBasedRelation(String rolePerson1, String rolePerson2,
                                          String relationshipDescriptor) throws CommandException {
        addressBook.validateRoleBasedRelation(rolePerson1, rolePerson2, relationshipDescriptor);
    }

    @Override
    public void validateRoleless(String role1, String role2, String newRelationshipDescriptor) throws CommandException {
        addressBook.validateRoleless(role1, role2, newRelationshipDescriptor);
    }

    @Override
    public void relationshipChecks(Relationship toEditIn, UUID fullOriginUuid, UUID fullTargetUuid, String originUuid,
                                   String targetUuid, String role1, String role2, Model model,
                                   String oldRelationshipDescriptor, String newRelationshipDescriptor,
                                   boolean b) throws CommandException {
        addressBook.relationshipChecks(toEditIn, fullOriginUuid, fullTargetUuid, originUuid, targetUuid, role1,
                role2, model, oldRelationshipDescriptor, newRelationshipDescriptor, b);
    }
}
