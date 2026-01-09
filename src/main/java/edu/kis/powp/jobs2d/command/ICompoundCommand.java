package edu.kis.powp.jobs2d.command;
import edu.kis.powp.jobs2d.command.visitor.CommandVisitor;
/**
 * Interface extending Job2dDriverCommand to execute more than one command.
 */
public interface ICompoundCommand extends DriverCommand, Iterable<DriverCommand> {
    @Override
    default void accept(CommandVisitor visitor) {
        visitor.visit(this);  
        for (DriverCommand c : this) {
            c.accept(visitor); 
        }
    }
}