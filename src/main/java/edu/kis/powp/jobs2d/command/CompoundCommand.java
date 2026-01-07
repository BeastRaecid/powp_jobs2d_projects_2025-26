package edu.kis.powp.jobs2d.command;

import edu.kis.powp.jobs2d.Job2dDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//Implementation of ICompoundCommand that allows executing multiple commands in sequence.
public class CompoundCommand implements ICompoundCommand {

    private final List<DriverCommand> commands;

    //Private constructor to ensure controlled creation.
    private CompoundCommand(List<DriverCommand> commands) {
        this.commands = Collections.unmodifiableList(new ArrayList<>(commands));
    }

    @Override
    public void execute(Job2dDriver driver) {
        for (DriverCommand command : commands) {
            command.execute(driver);
        }
    }

    @Override
    public Iterator<DriverCommand> iterator() {
        return commands.iterator();
    }

    public List<DriverCommand> getCommands() {
        return commands;
    }

    //Creates a new CompoundCommand from a list of commands.
    public static CompoundCommand fromListOfCommands(List<DriverCommand> commands) {
        return new CompoundCommand(commands);
    }

    //Creates a new empty CompoundCommand.
    public static CompoundCommand empty() {
        return new CompoundCommand(Collections.emptyList());
    }

    //Returns a new CompoundCommand with additional commands appended.
    //Original compound command remains unchanged.
    public CompoundCommand append(List<DriverCommand> commands) {
        List<DriverCommand> newCommands = new ArrayList<>(this.commands);
        newCommands.addAll(commands);
        return new CompoundCommand(newCommands);
    }

    public int size() {
        return commands.size();
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    //Builder class for flexible creation of CompoundCommand instances.
    public static class Builder {
        private final List<DriverCommand> commands = new ArrayList<>();

        //Adds multiple commands to the builder.
        public Builder add(List<DriverCommand> commands) {
            this.commands.addAll(commands);
            return this;
        }

        //Adds all commands from another compound command to the builder.
        public Builder add(CompoundCommand compoundCommand) {
            this.commands.addAll(compoundCommand.getCommands());
            return this;
        }

        public Builder addSetPosition(int x, int y) {
            this.commands.add(new SetPositionCommand(x, y));
            return this;
        }

        public Builder addOperateTo(int x, int y) {
            this.commands.add(new OperateToCommand(x, y));
            return this;
        }

        //Builds the CompoundCommand instance.
        public CompoundCommand build() {
            return new CompoundCommand(commands);
        }
    }

    //Creates a new Builder instance for flexible command construction.
    public static Builder builder() {
        return new Builder();
    }
}