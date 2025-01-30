package com.repnox.nineseventax.features.excel;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CsvTableExport implements TableExporter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvTableExport.class);

    private StringWriter stringWriter;

    private CSVWriter csvWriter;

    private List<String> currentLine;

    public CsvTableExport() {
        stringWriter = new StringWriter();
        csvWriter = new CSVWriter(stringWriter);
        currentLine = new ArrayList<>();
    }

    @Override
    public void skipCells(int numCells) {
        for (int i=0; i<numCells; i++) {
            currentLine.add("");
        }
    }

    @Override
    public void nextCell(String value) {
        currentLine.add(value);
    }

    @Override
    public void nextRow() {
        csvWriter.writeNext(currentLine.toArray(new String[currentLine.size()]));
        currentLine = new ArrayList<>();
    }

    @Override
    public void export(OutputStream outputStream) {
        if (currentLine.size() > 0) {
            nextRow();
        }
        try {
            outputStream.write(stringWriter.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            LOG.error("Unable to export CSV", e);
        } finally {
            try {
                csvWriter.close();
            } catch (IOException e) {
                LOG.error("Unable to close csv stream", e);
            }
        }
    }
}
