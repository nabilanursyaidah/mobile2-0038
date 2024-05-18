package com.example.praktikum2;

import java.io.File;

public class DataFileGambar {
    private String pathFile;
    private File file;

    public DataFileGambar(String pathFile, File file) {
        this.pathFile = pathFile;
        this.file = file;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
