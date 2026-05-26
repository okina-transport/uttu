package no.entur.uttu.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileUtils {

    /**
     * Generate a zip file with all files
     *
     * @param exportDir   directory in which generated files are stored
     * @param zipFileName the name of the zip
     */
    public static void zipFilesInDirectory(String exportDir, String zipFileName) throws IOException {
        File[] values = new File(exportDir).listFiles();
        if (values != null) {
            Set<String> generatedFiles = Stream.of(values)
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toSet());

            try (final FileOutputStream fos = new FileOutputStream(exportDir + "/" + zipFileName)) {
                try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                    for (String srcFile : generatedFiles) {
                        File fileToZip = new File(exportDir + "/" + srcFile);
                        try (FileInputStream fis = new FileInputStream(fileToZip)) {
                            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                            zipOut.putNextEntry(zipEntry);

                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                        }
                    }

                }
            }
        }
    }

    public static void deleteFilesByExtension(String directory, String extension) {
        File[] files = new File(directory).listFiles();
        if (files != null) {
            Stream.of(files)
                    .filter(file -> !file.isDirectory() && file.getName().endsWith(extension))
                    .forEach(File::delete);
        }
    }

}
