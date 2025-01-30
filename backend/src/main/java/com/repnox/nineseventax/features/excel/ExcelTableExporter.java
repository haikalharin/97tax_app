package com.repnox.nineseventax.features.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class ExcelTableExporter implements TableExporter {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelTableExporter.class);

    private int rowNum = 0;
    private int cellNum = 0;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private Row currentRow;

    public ExcelTableExporter(String sheetName) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        currentRow = sheet.createRow(rowNum++);
    }

    @Override
    public void skipCells(int numCells) {
        cellNum += numCells;
    }

    @Override
    public void nextCell(String value) {
        currentRow.createCell(cellNum++).setCellValue(value);
    }

    @Override
    public void nextRow() {
        currentRow = sheet.createRow(rowNum++);
        cellNum = 0;
    }

    @Override
    public void export(OutputStream outputStream) {
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            LOG.error("Unable to export excel", e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                LOG.error("Unable to close workbook", e);
            }
        }
    }
}
