package gitplit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;


/* Base class, the den of methods created for the execution of COMMAND. */
@SuppressWarnings("unchecked")
public class Base {
    
    /* GitPlit repository - the main storage. Hidden by default. */
    static final File GITPLIT = new File(".gitplit_repository");
    /* Addition staging area. */
    static final File ADDITIONS = Utils.subFile(GITPLIT, "additions");
    /* Removal staging area. */
    static final File REMOVALS = Utils.subFile(GITPLIT, "removals");
    /* Storage for commits. */
    static final File COMMITS = Utils.subFile(GITPLIT, "COMMITS");
    /* Storage for the head commit information. */
    static final File HEAD_COMMIT = Utils.subFile(GITPLIT, "head_commit");
    /* Storage for branches. */
    static final File BRANCHES = Utils.subFile(GITPLIT, "branches");
    /* Storage for the current branch information. */
    static final File CURRENT_BRANCH = Utils.subFile(GITPLIT, "current_branch");
    /* Storage for remotes. */
    static final File REMOTE = Utils.subFile(GITPLIT, "remote");
    /* Current working directory. */
    static final File CWD = new File(".");
    /* Boolean used to check if there was a merge conflict. */
    private static boolean conflicted = false;
    
    /* Initializes a GitPlit repository and creates the very first commit. 
     * Exits if a repository already exists.*/
    public static void setUp() throws IOException {
        if (GITPLIT.exists()) {
            System.out.println("A GitPlit version control system already exists in the current directory.");
            return;
        }
        
        GITPLIT.mkdir();
        ADDITIONS.mkdir();
        REMOVALS.mkdir();
        COMMITS.mkdir();
        HEAD_COMMIT.createNewFile();
        BRANCHES.mkdir();
        CURRENT_BRANCH.createNewFile();
        REMOTE.mkdir();
        
        addBranch("master");
        updateCurrentBranch("master");
        addCommit(new Commit("initial commit", Utils.getCurrentTime()));
    }
    
    /* Creates the branch BRANCHNAME. */
    public static void addBranch(String branchName) throws IOException {
        File newBranch = Utils.subFile(BRANCHES, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        newBranch.createNewFile();
    }
    
    /* Removes the branch BRANCHNAME. */
    public static void removeBranch(String branchName) {
        File toBeRemoved = Utils.subFile(BRANCHES, branchName);
        if (!toBeRemoved.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (Utils.readAsString(CURRENT_BRANCH).equals(branchName)){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        toBeRemoved.delete();
    }
    
    /* Updates the branch BRANCHNAME with the head commit ID. */
    public static void updateBranch(String branchName) {
        File branch = Utils.subFile(BRANCHES, branchName);
        Utils.writeContents(branch, Utils.readAsString(HEAD_COMMIT));
    }
    
    /* Updates the branch BRANCHNAME with the provided commit ID. */
    public static void updateBranch(String branchName, String commitID) {
        File branch = Utils.subFile(BRANCHES, branchName);
        Utils.writeContents(branch, commitID);
    }
    
    /* Updates CURRENT_BRANCH with BRANCHNAME. */
    public static void updateCurrentBranch(String branchName) {
        Utils.writeContents(CURRENT_BRANCH, branchName);
    }
    
    /* Creates a commit file containing the serialized information of the provided commit object NEWCOMMIT. */
    public static void addCommit(Commit newCommit) throws IOException {
        String commitID = Utils.sha3(Utils.serialize(newCommit));
        File newCommitFile = Utils.subFile(COMMITS, commitID);
        newCommitFile.createNewFile();
        Utils.writeObject(newCommitFile, newCommit);
        
        updateHead(commitID);
        updateBranch(Utils.readAsString(CURRENT_BRANCH));
        clearAdditions();
        clearRemovals();
    }
    
    /* Creates a commit file containing the serialized information of the provided commit object NEWCOMMIT. 
     * Updates NEWCOMMIT's secondParentID with SECONDPARENTCOMMITID. */
    public static void addCommit(Commit newCommit, String secondParentCommitID) throws IOException {
        newCommit.setSecondParent(secondParentCommitID);
        String commitID = Utils.sha3(Utils.serialize(newCommit));
        File newCommitFile = Utils.subFile(COMMITS, commitID);
        newCommitFile.createNewFile();
        Utils.writeObject(newCommitFile, newCommit);
        
        updateHead(commitID);
        updateBranch(Utils.readAsString(CURRENT_BRANCH));
        clearAdditions();
        clearRemovals();
    }
    
    /* Updates HEAD_COMMIT with COMMITID. */
    public static void updateHead(String commitID) {
        Utils.writeContents(HEAD_COMMIT, commitID);
    }
    
    /* Copies a file in CWD named FILENAME and puts it into the addition staging area. */
    public static void updateAdditions(String fileName) throws IOException {
        File fileInCWD = new File(fileName);
        if (!fileInCWD.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        
        File stagedForRemoval = Utils.subFile(REMOVALS, fileName);
        stagedForRemoval.delete();
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        
        if (trackingFilesOfHC.containsKey(fileName) 
            && Arrays.equals(trackingFilesOfHC.get(fileName), Utils.readAsBytes(fileInCWD))) { 
            System.out.println("The file is already tracked and has no changes.");
            return;
        }
        
        File stagedForAddition = Utils.subFile(ADDITIONS, fileName);
        stagedForAddition.createNewFile();
        copyContents(fileInCWD, stagedForAddition);
    }
    
    /* Copies a file in CWD named FILENAME and puts it into the removal staging area. 
     * Note that the copy will be an empty file.
     * Deletes the file in CWD afterwards. */
    public static void updateRemovals(String fileName) throws IOException {
        File stagedForAddition = Utils.subFile(ADDITIONS, fileName);
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        
        if (!stagedForAddition.exists() && !trackingFilesOfHC.containsKey(fileName)) {
            System.out.println("The file is neither staged nor tracked by the head commit.");
            return;
        }
        stagedForAddition.delete();
        
        if (trackingFilesOfHC.containsKey(fileName)) {
            File fileInCWD = new File(fileName);
            fileInCWD.delete();
            Utils.subFile(REMOVALS, fileName).createNewFile();
        }
    }
    
    /* Returns the head Commit object. */
    public static Commit headCommit() {
        File headCommitFile = Utils.subFile(COMMITS, Utils.readAsString(HEAD_COMMIT));
        return Utils.readObject(headCommitFile, Commit.class);
    }
    
    /* Copies the contents of FROM to TO. */
    public static void copyContents(File from, File to) {
        Utils.writeContents(to, Utils.readAsBytes(from));
    }
    
    /* Clears the addition staging area. */
    public static void clearAdditions() {
        for (File f : ADDITIONS.listFiles()) {
            f.delete();
        }
    }
    
    /* Clears the removal staging area. */
    public static void clearRemovals() {
        for (File f : REMOVALS.listFiles()) {
            f.delete();
        }
    }
    
    /* Returns the full ID of ABBREVIATEDCOMMITID. 
     * Throws IllegalArgumentException if the length of ABBREVIATEDCOMMITID is less than 6. */
    public static String fullCommitID(String abbreviatedCommitID) {
        if (abbreviatedCommitID.length() < 6) {
            throw new IllegalArgumentException("The length of abbreviated commit ID must be at least 6.");
        } else if (abbreviatedCommitID.length() != 64) {
            int length = abbreviatedCommitID.length();
            for (File potentialMatch : COMMITS.listFiles()) {
                String abbreviatedFileName = potentialMatch.getName().substring(0, length);
                if (abbreviatedCommitID.equals(abbreviatedFileName)) {
                    return potentialMatch.getName();
                }
            }
        }
        return abbreviatedCommitID;
    }
    
    /* Displays the commit tree (history) of the GitPlit repository. */
    public static void log() {
        Commit targetCommit = headCommit();
        String targetCommitID = Utils.readAsString(HEAD_COMMIT);
        while (true) {
            System.out.println("===");
            System.out.print("commit ");
            System.out.println(targetCommitID);
            if (targetCommit.hasSecondParent()) {
                System.out.println("Merge: " + targetCommit.parentID().substring(0, 6) + " " 
                                   + targetCommit.secondParentID().substring(0, 6));
            }
            System.out.println("Date: " + targetCommit.time());
            System.out.println(targetCommit.message());
            if (targetCommit.parentID() == null) {
                break;
            }
            System.out.println("");
            targetCommitID = targetCommit.parentID();
            targetCommit = Utils.readObject(Utils.subFile(COMMITS, targetCommitID), Commit.class);
        }
    }
    
    /* Takes the version of the file named FILENAME as it exists in the head commit and puts it in CWD.
     * Overwrites if it already exists.
     * This new/newer version of the file is not staged. */
    public static void checkoutFile(String fileName) throws IOException {
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        if (trackingFilesOfHC.get(fileName) == null) {
            System.out.println("File does not exist in the head commit.");
            return;
        }
        
        File checkedoutFile = new File(fileName);
        checkedoutFile.createNewFile();
        Utils.writeContents(checkedoutFile, trackingFilesOfHC.get(fileName));
    }
    
    /* Takes the version of the file named FILENAME as it exists in the commit with COMMITID and puts it in CWD.
     * Overwrites if it already exists.
     * This new/newer version of the file is not staged. 
     * COMMITID can be abbreviated (must be at least 6 letters). */
    public static void checkoutFile(String fileName, String commitID) throws IOException {
        File targetCommitFile = Utils.subFile(COMMITS, commitID);
        if (!targetCommitFile.exists()) {
            System.out.println("No commit with that ID exists.");
            return;
        }
        
        File checkedoutFile = new File(fileName);
        Commit targetCommit = Utils.readObject(targetCommitFile, Commit.class);
        HashMap<String, byte[]> trackingFilesOfTC = targetCommit.trackingFiles();
        if (!trackingFilesOfTC.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            checkedoutFile.createNewFile();
            Utils.writeContents(checkedoutFile, trackingFilesOfTC.get(fileName));
        }
    }
    
    /* Takes all files of the commit pointed by the branch BRANCHNAME and puts them in CWD.
     * Overwrites if they already exist.
     * The referenced branch will now be considered the current branch. */
    public static void checkoutBranch(String branchName) throws IOException {
        File targetBranchFile = Utils.subFile(BRANCHES, branchName);
        if (!targetBranchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (Utils.readAsString(CURRENT_BRANCH).equals(branchName)) {
            System.out.println("The system is already located at the current branch.");
            return;
        }
        
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        
        String targetCommitID = Utils.readAsString(targetBranchFile);
        Commit targetCommit = Utils.readObject(Utils.subFile(COMMITS, targetCommitID), Commit.class);
        HashMap<String, byte[]> trackingFilesOfTC = targetCommit.trackingFiles();
        
        File[] filesInCWD = CWD.listFiles();
        for (File f : filesInCWD) {
            if (!f.isDirectory() && !trackingFilesOfHC.containsKey(f.getName())
                && trackingFilesOfTC.containsKey(f.getName()) 
                && !Arrays.equals(Utils.readAsBytes(f), trackingFilesOfTC.get(f.getName()))) {
                    System.out.println("There is an untracked file in the way. " 
                                       + "Delete it, or add and commit it first.");
                }
        }
        for (HashMap.Entry<String, byte[]> ent : trackingFilesOfHC.entrySet()) {
            File deleteOrOverwrite = new File(ent.getKey());
            if (!trackingFilesOfTC.containsKey(ent.getKey())) {
                deleteOrOverwrite.delete();
            } else if (deleteOrOverwrite.exists()) {
                Utils.writeContents(deleteOrOverwrite, trackingFilesOfTC.get(ent.getKey()));
            }
        }
        for (HashMap.Entry<String, byte[]> ent : trackingFilesOfTC.entrySet()) {
            File addOrOverwrite = new File(ent.getKey());
            addOrOverwrite.createNewFile();
            Utils.writeContents(addOrOverwrite, ent.getValue());
        }
        
        updateHead(targetCommitID);
        updateCurrentBranch(branchName);
        clearAdditions();
        clearRemovals();
    }
    
    /* Fetches all commits with message MESSAGE and displays their ID.
     * Does not exist in real git. */
    public static void find(String message) {
        boolean found = false;
        for (File f : COMMITS.listFiles()) {
            Commit com = Utils.readObject(f, Commit.class);
            if (com.message().equals(message)) {
                System.out.println(f.getName());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }
    
    /* Displays the state of the working directory. */
    public static void status() {
        String[] branchNames = BRANCHES.list();
        Arrays.sort(branchNames);
        System.out.println("=== Branches ===");
        for (String branchName : branchNames) {
            if (branchName.equals(Utils.readAsString(CURRENT_BRANCH))) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println("");
        
        String[] stagedForAdditionFileNames = ADDITIONS.list();
        Arrays.sort(stagedForAdditionFileNames);
        System.out.println("=== Staged Files ===");
        for (String fileName : stagedForAdditionFileNames) {
            System.out.println(fileName);
        }
        System.out.println("");
        
        String[] stagedForRemovalFileNames = REMOVALS.list();
        Arrays.sort(stagedForRemovalFileNames);
        System.out.println("=== Removed Files ===");
        for (String fileName : stagedForRemovalFileNames) {
            System.out.println(fileName);
        }
        System.out.println("");
        
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        TreeMap<String, String> modifications = new TreeMap<>();
        for (File f : ADDITIONS.listFiles()) {
            File fileInCWD = new File(f.getName());
            if (fileInCWD.exists() && !Arrays.equals(Utils.readAsBytes(f), Utils.readAsBytes(fileInCWD))) {
                modifications.put(f.getName(), "(modified)");
            } else if (!fileInCWD.exists()) {
                modifications.put(f.getName(), "(deleted)");
            }
        }
        for (HashMap.Entry<String, byte[]> ent : trackingFilesOfHC.entrySet()) {
            File fileInCWD = new File(ent.getKey());
            File fileInAdditions = Utils.subFile(ADDITIONS, ent.getKey());
            File fileInRemovals = Utils.subFile(REMOVALS, ent.getKey());
            if (fileInCWD.exists() && !Arrays.equals(ent.getValue(), Utils.readAsBytes(fileInCWD))
                && !fileInAdditions.exists() && !fileInRemovals.exists()) {
                modifications.put(ent.getKey(), "(modified)");
            } else if (!fileInCWD.exists() && !fileInRemovals.exists()) {
                modifications.put(ent.getKey(), "(deleted)");
            }
        }
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (Map.Entry<String, String> ent : modifications.entrySet()) {
            System.out.println(ent.getKey() + " " + ent.getValue());
        }
        System.out.println("");
        
        String[] CWDFileNames = CWD.list();
        ArrayList<String> untrackedFileNames = new ArrayList<>();
        for (String fileName : CWDFileNames) {
            File fileInCWD = Utils.subFile(CWD, fileName);
            File fileInAdditions = Utils.subFile(ADDITIONS, fileName);
            if (!fileInCWD.isDirectory() && !fileInAdditions.exists() 
                && !trackingFilesOfHC.containsKey(fileName)) {
                untrackedFileNames.add(fileName);
            }
        }
        String[] untrackedList = untrackedFileNames.toArray(new String[0]);
        Arrays.sort(untrackedList);
        System.out.println("=== Untracked Files ===");
        for (String fileName : untrackedList) {
            System.out.println(fileName);
        }
    }
    
    /* Checks out all files tracked by commit with COMMITID.
     * Removes currently-tracked files that are not present in that commit.
     * The current branch will now point at the referenced commit. 
     * COMMITID can be abbreviated (must be at least 6 letters). */
    public static void reset(String commitID) throws IOException {
        File resettingCommitFile = Utils.subFile(COMMITS, commitID);
        if (!resettingCommitFile.exists()) {
            System.out.println("Found no commit with that ID.");
            return;
        }
        
        Commit headCommit = headCommit();
        HashMap<String, byte[]> trackingFilesOfHC = headCommit.trackingFiles();
        Commit resettingCommit = Utils.readObject(resettingCommitFile, Commit.class);
        HashMap<String, byte[]> trackingFilesOfRC = resettingCommit.trackingFiles();
        for (File fileInCWD : CWD.listFiles()) {
            if (!fileInCWD.isDirectory() && !trackingFilesOfHC.containsKey(fileInCWD.getName())
                && trackingFilesOfRC.containsKey(fileInCWD.getName())
                && !Arrays.equals(Utils.readAsBytes(fileInCWD), trackingFilesOfRC.get(fileInCWD.getName()))) {
                System.out.println("There is an untracked file in the way. " 
                                   + "Delete it, or add and commit it first.");
            }
        }
        for (HashMap.Entry<String, byte[]> ent : trackingFilesOfHC.entrySet()) {
            File mayRemove = new File(ent.getKey());
            if (!trackingFilesOfRC.containsKey(ent.getKey()) && mayRemove.exists()) {
                updateRemovals(ent.getKey());
            }
        }
        for (HashMap.Entry<String, byte[]> ent : trackingFilesOfRC.entrySet()) {
            checkoutFile(ent.getKey(), commitID);
        }
        
        updateHead(commitID);
        updateBranch(Utils.readAsString(CURRENT_BRANCH), commitID);
        clearAdditions();
        clearRemovals();
    }
    
    /* Returns the ID of the lowest common ancestor of two commits with COMMITID1 and COMMITID2. 
     * There are two helper functions designed to facilitate this process. */
    public static String lowestCommonAncestor(String commitID1, String commitID2) {
        ArrayList<ArrayList<String>> allCommitChains = new ArrayList<>();
        ArrayList<String> commitChainTBA = new ArrayList<>();
        lcaHelper_UpdateAllCommitChains(commitID1, commitChainTBA, allCommitChains);
        
        int counter = Integer.MAX_VALUE;
        String lowestCommonAncestorID = null;
        for (ArrayList<String> chain: allCommitChains) {
            ArrayList<String> possibleLCAs = new ArrayList<>();
            lcaHelper_UpdatePossibleLCAs(commitID2, chain, possibleLCAs);
            for (String ID : possibleLCAs) {
                if (chain.indexOf(ID) <= counter) {
                    counter = chain.indexOf(ID);
                    lowestCommonAncestorID = ID;
                }
            }
        }
        return lowestCommonAncestorID;
    }
    
    /* Helper function of lowestCommonAncestor().
     * Adds all possible commit chains of the commit with COMMITID1 to ALLCOMMITCHAINS. */
    public static void lcaHelper_UpdateAllCommitChains(String commitID1, ArrayList<String> commitChain, 
                                                        ArrayList<ArrayList<String>> allCommitChains) {
        Commit current;
        while (commitID1 != null) {
            commitChain.add(commitID1);
            current = Utils.readObject(Utils.subFile(COMMITS, commitID1), Commit.class);
            if (current.hasSecondParent()) {
                lcaHelper_UpdateAllCommitChains(current.secondParentID(), 
                                                 (ArrayList<String>) commitChain.clone(), allCommitChains);
            }
            commitID1 = current.parentID();
        }
        allCommitChains.add(commitChain);
    }
    
    /* Helper function of lowestCommonAncestor().
     * Adds the ID of all common ancestors located in CHAIN to POSSIBLELCAS. */
    public static void lcaHelper_UpdatePossibleLCAs(String commitID2, ArrayList<String> chain, 
                                                     ArrayList<String> possibleLCAs) {
        Commit current;
        while (!chain.contains(commitID2)) {
            current = Utils.readObject(Utils.subFile(COMMITS, commitID2), Commit.class);
            if (current.hasSecondParent()) {
                lcaHelper_UpdatePossibleLCAs(current.secondParentID(), chain, possibleLCAs);
            }
            commitID2 = current.parentID();
        }
        possibleLCAs.add(commitID2);
    }
    
    /* Merges files from the branch BRANCHNAME into the current branch.
     * If the file contents of BRANCHNAME and the current branch are different from each other, 
       calls mergeConflict() and updates the file to include the contents of both.
     * Unlike real git, merge conflicts do not have to be resolved: the changes will be 
       shown in the updated file. */
    public static void merge(String branchName) throws IOException {
        if (ADDITIONS.list().length != 0 || REMOVALS.list().length != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!Utils.subFile(BRANCHES, branchName).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (Utils.readAsString(CURRENT_BRANCH).equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String idOfCurrentBranch = Utils.readAsString(HEAD_COMMIT);
        String idOfProvidedBranch = Utils.readAsString(Utils.subFile(BRANCHES, branchName));
        String idOfLCA = lowestCommonAncestor(idOfCurrentBranch, idOfProvidedBranch);
        if (idOfProvidedBranch.equals(idOfLCA)) {
            System.out.println("Provided branch is an ancestor of the current branch.");
            return;
        } else if (idOfCurrentBranch.equals(idOfLCA)) {
            System.out.println("Current branch fast-forwarded.");
        }
        
        Commit commitOfCB = Utils.readObject(Utils.subFile(COMMITS, idOfCurrentBranch), Commit.class);
        Commit commitOfPB = Utils.readObject(Utils.subFile(COMMITS, idOfProvidedBranch), Commit.class);
        Commit lca = Utils.readObject(Utils.subFile(COMMITS, idOfLCA), Commit.class);
        HashMap<String, byte[]> tfOfCB = commitOfCB.trackingFiles();
        HashMap<String, byte[]> tfOfPB = commitOfPB.trackingFiles();
        HashMap<String, byte[]> tfOfLCA = lca.trackingFiles();
        for (String fileName : CWD.list()) {
            if (!tfOfCB.containsKey(fileName)
                    && tfOfPB.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way. "
                                   + "Delete it, or add and commit it first.");
                return;
            }
        }
        
        ArrayList<String> removeListTFCB = new ArrayList<String>();
        ArrayList<String> removeListTFPB = new ArrayList<String>();
        for (HashMap.Entry<String, byte[]> ent : tfOfCB.entrySet()) {
            if (tfOfPB.containsKey(ent.getKey()) && tfOfLCA.containsKey(ent.getKey())) {
                byte[] fileContentPB = tfOfPB.get(ent.getKey());
                byte[] fileContentLCA = tfOfLCA.get(ent.getKey());
                if (Arrays.equals(ent.getValue(), fileContentLCA) && !Arrays.equals(ent.getValue(), fileContentPB)) {
                    checkoutFile(ent.getKey(), idOfProvidedBranch);
                    updateAdditions(ent.getKey());
                    removeListTFCB.add(ent.getKey());
                    removeListTFPB.add(ent.getKey());
                } else if (!Arrays.equals(ent.getValue(), fileContentPB) 
                           && !Arrays.equals(ent.getValue(), fileContentLCA)
                           && !Arrays.equals(fileContentPB, fileContentLCA)) {
                    mergeConflict(ent.getKey(), Utils.byteToString(ent.getValue()), Utils.byteToString(fileContentPB));
                    updateAdditions(ent.getKey());
                    removeListTFCB.add(ent.getKey());
                    removeListTFPB.add(ent.getKey());
                }
            } else if (tfOfPB.containsKey(ent.getKey()) && !tfOfLCA.containsKey(ent.getKey())) {
                if (!Arrays.equals(ent.getValue(), tfOfPB.get(ent.getKey()))) {
                    mergeConflict(ent.getKey(), Utils.byteToString(ent.getValue()), 
                                  Utils.byteToString(tfOfPB.get(ent.getKey())));
                    File existing = new File(ent.getKey());
                    updateAdditions(ent.getKey());
                    removeListTFCB.add(ent.getKey());
                    removeListTFPB.add(ent.getKey());
                }
            } else if (!tfOfPB.containsKey(ent.getKey()) && tfOfLCA.containsKey(ent.getKey())) {
                if (Arrays.equals(ent.getValue(), tfOfLCA.get(ent.getKey()))) {
                    updateRemovals(ent.getKey());
                    removeListTFCB.add(ent.getKey());
                } else {
                    mergeConflict(ent.getKey(), Utils.byteToString(ent.getValue()), "");
                    updateAdditions(ent.getKey());
                    removeListTFCB.add(ent.getKey());
                }
            }
        }
        for (String fileName : removeListTFCB) {
            tfOfCB.remove(fileName);
        }
        for (String fileName : removeListTFPB) {
            tfOfPB.remove(fileName);
        }
        for (HashMap.Entry<String, byte[]> ent : tfOfPB.entrySet()) {
            if (!tfOfCB.containsKey(ent.getKey()) && tfOfLCA.containsKey(ent.getKey()) 
                && !Arrays.equals(ent.getValue(), tfOfLCA.get(ent.getKey()))) {
                mergeConflict(ent.getKey(), "", Utils.byteToString(tfOfPB.get(ent.getKey())));
                updateAdditions(ent.getKey());
            } else if (!tfOfCB.containsKey(ent.getKey()) && !tfOfLCA.containsKey(ent.getKey())) {
                checkoutFile(ent.getKey(), idOfProvidedBranch);
                updateAdditions(ent.getKey());
            }
        }
        
        String message = "Merged " + branchName + " into " + Utils.readAsString(CURRENT_BRANCH) + ".";
        addCommit(new Commit(message, Utils.getCurrentTime()), idOfProvidedBranch);
        if (conflicted == true) {
            System.out.println("Encountered a merge conflict.");
            conflicted = false;
        }
    }
    
    /* Method that addresses merge conflicts.
     * Updates the file with FILENAME to include its contents from both the branch BRANCHNAME 
       and the current branch. */
    public static void mergeConflict(String fileName, String fileContentsOfCB, 
                                     String fileContentsOfPB) throws IOException {
        File updatedFile = new File(fileName);
        updatedFile.createNewFile();
        if (!fileContentsOfCB.equals("")) {
            fileContentsOfCB = fileContentsOfCB.replaceAll("\\s+$", "") + "\r\n";
        } else if (!fileContentsOfPB.equals("")) {
            fileContentsOfPB = fileContentsOfPB.replaceAll("\\s+$", "") + "\r\n";
        }
        String infoToAdd = "<<<<<<< HEAD: " + fileName + "\r\n" + fileContentsOfCB + "======="
                           + "\r\n" + fileContentsOfPB + ">>>>>>>" + "\r\n";
        Utils.writeContents(updatedFile, infoToAdd);
        conflicted = true;
    }
    
    /* Copies the GitPlit repository in CWD and puts it into DIRPATH.
     * Overwrites if a GitPlit repository already exists in DIRPATH.
     * Note that this method will also create the GitPlit related java & class files in DirPath.
       This will allow the user to run GitPlit from DIRPATH without having to copy over the program files manually.
     * A repository must exist in CWD. Errors and quits otherwise. */
    public static void clone(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            System.out.println("Invalid path.");
            return;
        } else if (!Utils.subFile(CWD, ".gitplit_repository").exists()) {
            System.out.println("A GitPlit repository does not exist in the current working directory.");
            return;
        } else if (!dir.isDirectory()) {
            System.out.println("The provided directory path does not lead to a directory.");
            return;
        }
        Utils.subFile(dir, "Base.java").createNewFile();
        Utils.subFile(dir, "Commit.java").createNewFile();
        Utils.subFile(dir, "Main.java").createNewFile();
        Utils.subFile(dir, "Utils.java").createNewFile();
        Utils.subFile(dir, "gitplit").mkdir();
        Utils.subFile(dir, ".gitplit_repository").mkdir();
        copyContents(Utils.subFile(CWD, "Base.java"), Utils.subFile(dir, "Base.java"));
        copyContents(Utils.subFile(CWD, "Commit.java"), Utils.subFile(dir, "Commit.java"));
        copyContents(Utils.subFile(CWD, "Main.java"), Utils.subFile(dir, "Main.java"));
        copyContents(Utils.subFile(CWD, "Utils.java"), Utils.subFile(dir, "Utils.java"));
        Utils.copyFiles(Utils.subFile(CWD, "gitplit"), Utils.subFile(dir, "gitplit"));
        Utils.copyFiles(Utils.subFile(CWD, ".gitplit_repository"), Utils.subFile(dir, ".gitplit_repository"));
    }
    
    /* Copies the GitPlit repository in REPOPATH and puts it into DIRPATH.
     * Overwrites if a GitPlit repository already exists in DIRPATH.
     * Note that this method will also create the GitPlit related java & class files in DirPath.
       This will allow the user to run GitPlit from DIRPATH without having to copy over the program files manually.
     * A repository must exist in REPOPATH. Errors and quits otherwise. */
    public static void clone(String repoPath, String dirPath) throws IOException {
        File repo = new File(repoPath);
        File dir = new File(dirPath);
        if (!repo.exists() || !dir.exists()) {
            System.out.println("Invalid paths.");
            return;
        } else if (!Utils.subFile(repo, ".gitplit_repository").exists()) {
            System.out.println("A GitPlit repository does not exist in the provided repo path.");
            return;
        } else if (!dir.isDirectory()) {
            System.out.println("The provided directory path does not lead to a directory.");
            return;
        }
        Utils.subFile(dir, "Base.java").createNewFile();
        Utils.subFile(dir, "Commit.java").createNewFile();
        Utils.subFile(dir, "Main.java").createNewFile();
        Utils.subFile(dir, "Utils.java").createNewFile();
        Utils.subFile(dir, "gitplit").mkdir();
        Utils.subFile(dir, ".gitplit_repository").mkdir();
        copyContents(Utils.subFile(repo, "Base.java"), Utils.subFile(dir, "Base.java"));
        copyContents(Utils.subFile(repo, "Commit.java"), Utils.subFile(dir, "Commit.java"));
        copyContents(Utils.subFile(repo, "Main.java"), Utils.subFile(dir, "Main.java"));
        copyContents(Utils.subFile(repo, "Utils.java"), Utils.subFile(dir, "Utils.java"));
        Utils.copyFiles(Utils.subFile(repo, "gitplit"), Utils.subFile(dir, "gitplit"));
        Utils.copyFiles(Utils.subFile(repo, ".gitplit_repository"), Utils.subFile(dir, ".gitplit_repository"));
    }
    
}