package com.xml.comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Collectors;

public class TradeEngineTest {

    private final String sourceFilePath = "sample-data-1.xml";
    private final String targetFilePath = "sample-data-2.xml";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {

    }

    @Test
    public void invalidInputFiles() throws Exception {
        XMLComparator xmlComparator = new XMLComparatorImpl();
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(String.format("Invalid source{} and target{} file","",""));
        List<EmployeeDiff> employeeDiffList = xmlComparator.compare("", "", false);
    }

    @Test
    public void SampleCaseDiffForXMLFileCompare() throws Exception {
        XMLComparator xmlComparator = new XMLComparatorImpl();
        List<EmployeeDiff> employeeDiffList = xmlComparator.compare(sourceFilePath, targetFilePath, false);
        Assert.assertEquals(3, employeeDiffList.size());
    }

    @Test
    public void veirfyAllMissingInSource() throws Exception {
        XMLComparator xmlComparator = new XMLComparatorImpl();
        List<EmployeeDiff> employeeDiffList = xmlComparator.compare("", targetFilePath, false);
        Assert.assertEquals(2, employeeDiffList.size());
        Assert.assertEquals(2, employeeDiffList.stream().filter(diff ->diff.getTargetValue().equals("MISSING")).collect(Collectors.toList()).size());
    }


}
