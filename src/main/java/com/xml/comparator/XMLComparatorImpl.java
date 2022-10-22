package com.xml.comparator;

import org.apache.poi.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

public class XMLComparatorImpl implements XMLComparator {
    private static String[] columns = {"employeeId", "diffTag", "sourceValue", "targetValue"};

    @Override
    public List compare(String sourceFilePath, String targetFilePath, boolean generateExcel) throws Exception {
        if (StringUtil.isBlank(sourceFilePath) && StringUtil.isBlank(targetFilePath)) {
            throw new RuntimeException(String.format("Invalid source{} and target{} file", sourceFilePath, targetFilePath));
        }
        List<EmployeeDiff> employeeDiffList = new ArrayList<>();
        try (InputStream sourceFileStream = getClass().getClassLoader().getResourceAsStream(sourceFilePath);
             InputStream targetFileStream = getClass().getClassLoader().getResourceAsStream(targetFilePath);
        ) {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            Document actualDocument = docBuilder.parse(new SequenceInputStream(
                    Collections.enumeration(Arrays.asList(
                            new InputStream[]{
                                    new ByteArrayInputStream("<dummy>".getBytes()),
                                    sourceFileStream,
                                    new ByteArrayInputStream("</dummy>".getBytes()),
                            }))
            ));
            Document expectedDocument = docBuilder.parse(new SequenceInputStream(
                    Collections.enumeration(Arrays.asList(
                            new InputStream[]{
                                    new ByteArrayInputStream("<dummy>".getBytes()),
                                    targetFileStream,
                                    new ByteArrayInputStream("</dummy>".getBytes()),
                            }))
            ));

            Diff differences = DiffBuilder.compare(actualDocument).withTest(expectedDocument)
                    .ignoreComments()
                    .ignoreWhitespace()
                    .normalizeWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(
                            ElementSelectors.byXPath("./employee/id", ElementSelectors.byNameAndText)))
                    .withNodeFilter(filter -> {
                        return !(filter.getNodeName().equals("id") || filter.getNodeName().equals("name")
                                || filter.getNodeName().equals("department") || filter.getNodeName().equals("phone"));
                    })
                    .build();
            Iterator<Difference> iterator = differences.getDifferences().iterator();

            while (iterator.hasNext()) {
                Difference next = iterator.next();
                EmployeeDiff diff = populateDifferences(next.getComparison());
                if (diff != null) {
                    employeeDiffList.add(diff);
                }
            }
            if (generateExcel) {
                Util.createEmployeDiffExcel(employeeDiffList, columns);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return employeeDiffList;
    }

    //populate employee diff bean from source vs target diff
    private EmployeeDiff populateDifferences(Comparison comparison) {
        employee sourceBean = getNodeName(comparison.getControlDetails().getTarget());
        employee targetBean = getNodeName(comparison.getTestDetails().getTarget());
        EmployeeDiff employeeDiff = null;
        if (sourceBean != null || targetBean != null) {
            employeeDiff = new EmployeeDiff(sourceBean, targetBean);
        }
        return employeeDiff;
    }

    private employee getNodeName(Node firstNode) {
        if (firstNode != null && firstNode.hasChildNodes()) {
            for (int i = 0; i < firstNode.getChildNodes().getLength(); i++) {
                Node child = firstNode.getChildNodes().item(i);
                if (child.getNodeName().equals("employee")) {
                    return Util.toBean(child);
                }
            }
        }
        return null;
    }


}
