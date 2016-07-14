package airpygs.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by fabrizioscimia on 14/07/16.
 */
public class FileTypesFilter implements FileFilter {

    String[] types;

    public FileTypesFilter(String[] types) {

        this.types = types;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            if (f.getName().startsWith("."))
                return false;
            else
                return true;
        }
        for (String type : types) {
            if (f.getName().endsWith(type)) return true;
        }
        return false;
    }
}
