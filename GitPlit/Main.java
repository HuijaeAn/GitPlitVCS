package gitplit;

import java.io.IOException;
import java.util.*;


/* Executing GitPlit, a version control system that reproduces the features of Git. */
public class Main {
    
    /* Usage: java gitplit.Main <COMMAND> <OPERAND>... */
    public static void main(String[] args) throws IOException {
        
        /* COMMAND must be specified. */
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        /* No COMMAND can be executed without Base.GITPLIT - the root storage. */
        if (!args[0].equals("init") && !Base.GITPLIT.exists()) {
            System.out.println("Need an initialized GitPlit repository. Try 'java gitplit.Main init'.");
            return;
        }
        
        /* Executing COMMAND. */
        switch (args[0]) {
            case "init":
                Base.setUp();
                return;
            case "add":
                Base.updateAdditions(args[1]);
                return;
            case "commit":
                if (args.length == 1 || args[1].length() == 0) {
                    System.out.println("Please enter a commit message.");
                    return;
                } else if (Base.ADDITIONS.listFiles().length == 0 && Base.REMOVALS.listFiles().length == 0) {
                    System.out.println("No changes added to the commit.");
                    return;
                }
                Base.addCommit(new Commit(Utils.restOfArgs(args, 1), Utils.getCurrentTime()));
                return;
            case "log":
                Base.log();
                return;
            case "checkout":
                switch (args.length) {
                    case 2:
                        Base.checkoutBranch(args[1]);
                        return;
                    case 3:
                        if (!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            return;
                        }
                        Base.checkoutFile(args[2]);
                        return;
                    case 4: 
                        try { 
                            if (!args[2].equals("--")) {
                                System.out.println("Incorrect operands.");
                                return;
                            }
                            Base.checkoutFile(args[3], Base.fullCommitID(args[1]));
                            return;
                        } catch (IllegalArgumentException exc) {
                            System.out.println("The length of abbreviated commit ID must be at least 6.");
                            return;
                        }
                    default: 
                        System.out.println("Invalid number of arguments.");
                        return;
                }
            case "rm":
                Base.updateRemovals(args[1]);
                return;
            case "find":
                Base.find(Utils.restOfArgs(args, 1));
                return;
            case "status":
                Base.status();
                return;
            case "branch":
                switch (args.length) {
                    case 2:
                        Base.addBranch(args[1]);
                        Base.updateBranch(args[1]);
                        return;
                    case 3:
                        if (!args[1].equals("-d")) {
                            System.out.println("No such command exists.");
                            return;
                        }
                        Base.removeBranch(args[2]);
                        return;
                    default: 
                        System.out.println("Invalid number of arguments.");
                        return;
                }
            case "reset":
                try {
                    Base.reset(Base.fullCommitID(args[1]));
                    return;
                } catch (IllegalArgumentException exc) {
                    System.out.println("The length of abbreviated commit ID must be at least 6.");
                    return;
                }
            case "merge":
                Base.merge(args[1]);
                return;
            case "clone":
                switch (args.length) {
                    case 2:
                        Base.clone(args[1]);
                        return;
                    case 3:
                        Base.clone(args[1], args[2]);
                        return;
                    default: 
                        System.out.println("Invalid number of arguments.");
                        return;
                }
            default:
                System.out.println("No command with that name exists.");
                return;
        }
    }
    
}