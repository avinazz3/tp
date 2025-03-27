package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Represents a command that edits the details of an existing group in the address book.
 */
public class EditGroupCommand extends Command {

    /**
     * The command word to trigger this command.
     */
    public static final String COMMAND_WORD = "edit-group";

    /**
     * Usage message for the command.
     */
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the group identified "
            + "by the index number used in the displayed group list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + "CS2103T T12-1 ";

    /**
     * Success message for editing a group.
     */
    public static final String MESSAGE_EDIT_GROUP_SUCCESS = "Edited Group: %1$s";

    /**
     * Error message if a duplicate group exists in the address book.
     */
    public static final String MESSAGE_DUPLICATE_GROUP = "This group already exists in the address book.";

    /**
     * Index of the group to be edited.
     */
    private final Index index;

    /**
     * New name for the group.
     */
    private final String newGroupName;

    private final Set<Tag> tags;

    /**
     * Creates an EditGroupCommand to update a group's name.
     *
     * @param index        The index of the group to be edited.
     * @param newGroupName The new name for the group.
     */
    public EditGroupCommand(Index index, String newGroupName, Collection<Tag> tags) {
        requireAllNonNull(index, newGroupName);

        this.index = index;
        this.newGroupName = newGroupName;
        this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
    }

    /**
     * Executes the command to edit a group.
     *
     * @param model The model in which the command should be executed.
     * @return A CommandResult indicating the outcome of the command execution.
     * @throws CommandException If the index is invalid or if the new group name already exists.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Group> lastShownList = model.getFilteredGroupList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException("Invalid Group");
        }

        Group groupToEdit = lastShownList.get(index.getZeroBased());
        Group editedGroup = createEditedGroup(groupToEdit, newGroupName);

        if (model.hasGroup(editedGroup)) {
            throw new CommandException(MESSAGE_DUPLICATE_GROUP);
        }

        model.setGroup(groupToEdit, editedGroup);
        return new CommandResult(String.format(MESSAGE_EDIT_GROUP_SUCCESS, newGroupName));
    }

    /**
     * Creates a new Group object with the updated name while retaining the group members.
     *
     * @param groupToEdit  The existing group to be edited.
     * @param newGroupName The new name for the group.
     * @return A new Group object with the updated name and existing members.
     */
    private static Group createEditedGroup(Group groupToEdit, String newGroupName) {
        assert groupToEdit != null;

        ArrayList<Person> list = groupToEdit.getGroupMembers();
        return new Group(newGroupName, list);
    }

    /**
     * Checks if this command is equal to another object.
     *
     * @param other The other object to compare to.
     * @return True if both objects are EditGroupCommand instances with the same index and new group name.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles null cases
        if (!(other instanceof EditGroupCommand otherCmd)) {
            return false;
        }

        return index.equals(otherCmd.index)
                && newGroupName.equals(otherCmd.newGroupName)
                && tags.equals(otherCmd.tags);
    }
}
