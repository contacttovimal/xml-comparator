package com.xml.comparator;

import java.util.List;

public interface XMLComparator {

    public List compare(String sourceFilePath, String targetFilePath, boolean generateExcel) throws Exception;
}
