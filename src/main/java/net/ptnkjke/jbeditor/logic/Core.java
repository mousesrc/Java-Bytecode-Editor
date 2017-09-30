package net.ptnkjke.jbeditor.logic;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.ptnkjke.jbeditor.Configutation;
import net.ptnkjke.jbeditor.utils.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lingala.zip4j.core.ZipFile;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * CORE
 */
public class Core {
    public static Core INSTANCE = new Core();

    /**
     * key= ClassName, value = byte class represent
     */
    private Map<String, byte[]> classMap = new HashMap<String, byte[]>();

    /**
     * is .jar file loaded or simple .class file?
     */
    private boolean isJarFileLoaded = false;
    /**
     * Original .jar byte-contant for saving change in new jar
     */
    private byte[] originalJar;

    /**
     * Read file
     *
     * @param inPath
     */
    public void read(String inPath) {
        // Clear Class Map
        classMap.clear();

        File file = new File(inPath);

        if (file.getName().contains(".jar")) {
            readJar(file);
            isJarFileLoaded = true;
        } else if (file.getName().contains(".class")) {
            readClassFile(file);
            isJarFileLoaded = false;
        }
    }

    /**
     * Read .jar file
     */
    private void readJar(File jarFile) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(jarFile);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        List<FileHeader> fileHeaderList = null;
        try {
            fileHeaderList = zipFile.getFileHeaders();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        for (FileHeader fh : fileHeaderList) {
            String fileName = fh.getFileName();

            if (fileName.contains(".class")) {
                JavaClass javaClass = null;
                try {
                    javaClass = new ClassParser(zipFile.getInputStream(fh), fileName).parse();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                classMap.put(fileName.replace("/", ".").replace(".class", ""), javaClass.getBytes());
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(jarFile);
            this.originalJar = Utils.readAllFromInputStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read .class file
     */
    private void readClassFile(File classFile) {
        try {
            JavaClass javaClass = new ClassParser(classFile.getAbsolutePath()).parse();
            classMap.put(javaClass.getClassName(), javaClass.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SaveAll
     */
    public void save() {
        if (isJarFileLoaded) {
            saveJar();
        } else {
            saveClassFile();
        }
    }

    /**
     * Save .jar file
     */
    private void saveJar() {
        File workDir = new File(Configutation.workDir, Utils.getRandomName());
        workDir.mkdirs();

        // CreateTmpFile
        File tFile = new File(workDir, Utils.getRandomName() + ".jar");

        try {
            tFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tFile);
            outputStream.write(originalJar);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // CreateZipFile
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(tFile);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        workDir = new File(workDir, "classes");
        // UpdateAllClassFile
        for (Map.Entry<String, byte[]> entry : classMap.entrySet()) {
            String className = entry.getKey();
            byte[] content = entry.getValue();

            className = className.replace(".", "/");

            String path = className + ".class";

            File f = new File(workDir, path);
            f.getParentFile().mkdirs();

            try {
                outputStream = new FileOutputStream(f);
                outputStream.write(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File[] allPackages = workDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        File[] allFiles = workDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        });

        for (File f : allPackages) {
            try {
                zipFile.addFolder(f, new ZipParameters());
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        for (File f : allFiles) {
            try {
                zipFile.addFile(f, new ZipParameters());
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save .class file
     */
    private void saveClassFile() {
        File workDir = new File(Configutation.workDir, Utils.getRandomName());

        for (Map.Entry<String, byte[]> entry : classMap.entrySet()) {
            String className = entry.getKey();
            byte[] content = entry.getValue();

            className = className.replace(".", "/");
            String path = className.replace(".", "/") + ".class";

            File f = new File(workDir, path);

            f.getParentFile().mkdirs();

            OutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(f);
                outputStream.write(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public byte[] getOriginalJar() {
        return originalJar;
    }

    public void setOriginalJar(byte[] originalJar) {
        this.originalJar = originalJar;
    }

    public boolean isJarFileLoaded() {
        return isJarFileLoaded;
    }

    public void setJarFileLoaded(boolean isJarFileLoaded) {
        this.isJarFileLoaded = isJarFileLoaded;
    }

    public Map<String, byte[]> getClassMap() {
        return classMap;
    }

    public void setClassMap(Map<String, byte[]> classMap) {
        this.classMap = classMap;
    }
}
