package utils;

import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesUtils {

    String zipName = "files.zip";

    public InputStream getFileFromZip(String fileToBeExtracted) throws Exception {
        ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(zipName)));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().contains(fileToBeExtracted)) {
                return zis;
            }
        }
        return InputStream.nullInputStream();
    }

    public String getZipName() {
        return zipName;
    }
}