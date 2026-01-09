package edu.kis.powp.jobs2d.command.visitor;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public final class CommandCounterVisitor {

    private CommandCounterVisitor() {}

    public static int countAll(DriverCommand root) {
        CommandCounter v = new CommandCounter();
        root.accept(v);
        return v.count;
    }

    public static int countOperateTo(DriverCommand root) {
        CommandCounter v = new CommandCounter();
        root.accept(v);
        return v.operateToCount;
    }

    public static int countSetPosition(DriverCommand root) {
        CommandCounter v = new CommandCounter();
        root.accept(v);
        return v.setPositionCount;
    }

    public static int countCompound(DriverCommand root) {
        CommandCounter v = new CommandCounter();
        root.accept(v);
        return v.compoundCount;
    }

    private static final class CommandCounter implements CommandVisitor {
        private int count = 0;
        private int operateToCount = 0;
        private int setPositionCount = 0;
        private int compoundCount = 0;

        @Override
        public void visit(SetPositionCommand command) {
            count++;
            setPositionCount++;
        }

        @Override
        public void visit(OperateToCommand command) {
            count++;
            operateToCount++;
        }

        @Override
        public void visit(ICompoundCommand command) {
            count++;
            compoundCount++;
            for (DriverCommand nested : command) {
                nested.accept(this);
            }
        }
    }
}
