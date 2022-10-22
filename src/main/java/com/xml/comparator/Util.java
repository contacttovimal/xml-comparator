package com.xml.comparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Util {

    public static boolean createEmployeDiffExcel(List<EmployeeDiff> diffs, String[] columns) throws IOException {
        if(CollectionUtils.isEmpty(diffs)){
            return false;
        }
        try (Workbook workbook = new XSSFWorkbook();) {
            CreationHelper createHelper = workbook.getCreationHelper();
            // Create a Sheet
            Sheet sheet = workbook.createSheet("Employee");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.RED.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Create cells
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Create Cell Style for formatting Date
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            // Create Other rows and cells with employees data
            int rowNum = 1;
            for (EmployeeDiff diff : diffs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0)
                        .setCellValue(diff.getEmployeeId());
                row.createCell(1)
                        .setCellValue(diff.getDiffTag());
                Cell dateOfBirthCell = row.createCell(2);
                dateOfBirthCell.setCellValue(diff.getSourceValue());
                dateOfBirthCell.setCellStyle(dateCellStyle);
                row.createCell(3)
                        .setCellValue(diff.getTargetValue());
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream("employeediff-report.xlsx");) {
                workbook.write(fileOut);
            }
            return true;
        }
    }

    public static employee toBean(Node beanNode) {
        employee bean = null;
        try {
            String className = beanNode.getNodeName();
            Class clazz = Class.forName("com.xml.comparator." + className);
            bean = (employee) clazz.newInstance();
            NodeList fieldNodeList = beanNode.getChildNodes();
            for (int i = 0; i < fieldNodeList.getLength(); i++) {
                Node fieldNode = fieldNodeList.item(i);
                if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                    String fieldName = fieldNode.getNodeName();
                    if (!fieldName.contains(".")) {
                        String getName = analyzeMethodName(fieldName, "get");
                        String setName = analyzeMethodName(fieldName, "set");
                        clazz.getMethod(setName,
                                        clazz.getMethod(getName).getReturnType())
                                .invoke(bean, fieldNode.getTextContent());
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException |
                InstantiationException | NoSuchMethodException |
                InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private static String analyzeMethodName(String fieldName,
                                            String methodType) {
        StringBuilder getName = new StringBuilder(methodType);
        return getName.append(
                String.valueOf(fieldName.charAt(0)).toUpperCase()
                        + fieldName.substring(1)).toString();
    }
}
