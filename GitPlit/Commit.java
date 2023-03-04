package gitplit;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;


/* Commit objects used to store individual commit information. */
@SuppressWarnings("unchecked")
public class Commit implements Serializable {

    /* Commit message. */
    private String message;
    /* Commit time. */
    private String time;
    /* Commit ID of its parent. */
    private String parentID;
    /* Commit ID of its optional second parent. Null by default. */
    private String secondParentID = null;
    /* Name and content of all files tracked by this commit. */
    private HashMap<String, byte[]> trackingFiles;
    
    public Commit(String m, String t) {
        message = m;
        time = t;
        setUp();
        addFiles();
        removeFiles();
    }
    
    public void setUp() {
        if (Utils.readAsString(Base.HEAD_COMMIT).isEmpty()) {
            parentID = null;
            trackingFiles = new HashMap<>();
            return;
        }
        parentID = Utils.readAsString(Base.HEAD_COMMIT);
        Commit parent = Utils.readObject(Utils.subFile(Base.COMMITS, parentID), Commit.class);
        trackingFiles = (HashMap<String, byte[]>) parent.trackingFiles().clone();
    }

    public void addFiles() {
        File[] listOfAddedFiles = Base.ADDITIONS.listFiles();
        for (File a : listOfAddedFiles) {
            String name = a.getName();
            byte[] contents = Utils.readAsBytes(a);
            if (!trackingFiles.containsKey(name)) {
                trackingFiles.put(name, contents);
            } else if (!Arrays.equals(trackingFiles.get(name), contents)) {
                trackingFiles.replace(name, contents);
            }
        }
    }
    
    public void removeFiles() {
        for (File toBeUntracked : Base.REMOVALS.listFiles()) {
            if (trackingFiles.containsKey(toBeUntracked.getName())) {
                trackingFiles.remove(toBeUntracked.getName());
            }
        }
    }

    public void setSecondParent(String secondParentID) {
        this.secondParentID = secondParentID;
    }

    public String message() {
        return message;
    }

    public String time() {
        return time;
    }
    
    public String parentID() {
        return parentID;
    }
    
    public String secondParentID() {
        return secondParentID;
    }

    public HashMap<String, byte[]> trackingFiles() {
        return trackingFiles;
    }

    public boolean hasSecondParent() {
        return secondParentID != null;
    }
    
}