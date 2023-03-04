package gitplit;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.util.Arrays;


/* The utilities associated with hashing, serialization, file accessing, and commit creation. */
class Utils {
    
    /* Creates a path to the subfile named CHILD. 
     * Note that this itself does not create the file, only its designation. */
    static File subFile(File parent, String child) {
        return Paths.get(parent.getPath(), child).toFile();
    }
    
    /* Returns a SHA-3 hash representation of INPUT.
     * Relies on the SHA3-256 hashing algorithm to generate a unique, fixed-size 256-bit (32-byte) hash. 
     * Hashes are used to reference each commit and the files containing their information. 
     * In short, they serve as: 1. Commit's ID 2. Name of commit files.
     * Note that INPUT must be either a byte array or a String. 
     * Throws IllegalArgumentException if the system does not support SHA-3. */
    static String sha3(Object input) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA3-256");
            if (input instanceof byte[]) {
                digester.update((byte[]) input);
            } else if (input instanceof String) {
                digester.update(((String) input).getBytes(StandardCharsets.UTF_8));
            } else {
                throw new IllegalArgumentException("Input must be a byte array or a String.");
            }
            return sha3Helper(digester.digest()); 
        } catch (NoSuchAlgorithmException exc){
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Helper function of sha3(). 
     * Converts the byte array INPUT into a 64 digit hexadecimal String and returns it. */
    static String sha3Helper(byte[] input) {
        StringBuilder hexBuilder = new StringBuilder(2 * input.length);
        for (int i = 0; i < input.length; i++) {
            String hex = Integer.toHexString(0xff & input[i]);
            if (hex.length() == 1) {
                hexBuilder.append('0');
            }
            hexBuilder.append(hex);
        }
        return hexBuilder.toString();
    }
    
    /* Serializes OBJECT into a byte array and returns it.
     * Throws IllegalArgumentException if serializing creates problems. */
    static byte[] serialize(Serializable object) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(object);
            objectStream.close();
            return byteStream.toByteArray();
        } catch (IOException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Reads FILE and returns its contents as a byte array. 
     * FILE must be a normal file, not a directory. 
     * Throws IllegalArgumentException if accessing FILE creates problems. */
    static byte[] readAsBytes(File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Cannot read directories.");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException exc){
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Reads FILE and returns its contents as a String.
     * FILE must be a normal file, not a directory. 
     * Throws IllegalArgumentException if accessing FILE creates problems. */
    static String readAsString(File file) {
        return new String(readAsBytes(file), StandardCharsets.UTF_8);
    }
    
    /* Converts the file content in bytes INFO to String and returns it. 
     * Throws IllegalArgumentException if rewriting INFO creates problems. */
    static String byteToString(byte[] info) throws IOException {
        try {
            File temp = new File("temp");
            temp.createNewFile();
            Utils.writeContents(temp, info);
            String infoInString = Utils.readAsString(temp);
            temp.delete();
            return infoInString;
        } catch (IOException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Overwrites FILE with INFO.
     * FILE must be a normal file, not a directory.
     * INFO must be either a byte array or a String.
     * Throws IllegalArgumentException if accessing FILE or casting create problems. */
    static void writeContents(File file, Object info) {
        try {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("Cannot overwrite directories.");
            }
            BufferedOutputStream source = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            if (info instanceof byte[]) {
                source.write((byte[]) info);
            } else {
                source.write(((String) info).getBytes(StandardCharsets.UTF_8));
            }
            source.close();
        } catch (IOException | ClassCastException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Returns an object of type T read from FILE. 
     * The object read from FILE must be castable into OFFEREDCLASS. 
     * Throws IllegalArgumentException if accessing FILE or casting create problems. */
    static <T extends Serializable> T readObject(File file, Class<T> offeredClass) {
        try {
            ObjectInputStream source = new ObjectInputStream(Files.newInputStream(file.toPath()));
            T result = offeredClass.cast(source.readObject());
            source.close();
            return result;
        } catch (IOException | ClassCastException | ClassNotFoundException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Overwrites FILE with OBJECT.
     * OBJECT must be a Serializable. */
    static void writeObject(File file, Serializable object) {
        writeContents(file, serialize(object));
    }
    
    /* Copies all files of DIRFROM and puts them into DIRTO.
     * Overwrites if a file with the same name already exists in DIRTO. 
     * Throws IllegalArgumentException if accessing files of DIRFROM or file creation create problems. */
    static void copyFiles(File dirFrom, File dirTo) {
        try {
            for (File f : dirFrom.listFiles()) {
                if (f.isDirectory()) {
                    Utils.subFile(dirTo, f.getName()).mkdir();
                    copyFiles(Utils.subFile(dirFrom, f.getName()), Utils.subFile(dirTo, f.getName()));
                    continue;
                }
                Utils.subFile(dirTo, f.getName()).createNewFile();
                Base.copyContents(f, Utils.subFile(dirTo, f.getName()));
            }
        } catch (IOException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }
    
    /* Returns the string array ARGS sliced from the starting index INDEX to the end.
     * The returned object will be a String. */
    static String restOfArgs(String[] args, int index) {
        StringBuffer sb = new StringBuffer();
        for(int i = index; i < args.length; i++) {
            sb.append(args[i]);
        }
        return sb.toString();
    }
    
    /* Returns the current time in String. */
    static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }
    
}