package com.repnox.nineseventax.features.excel;

import java.io.OutputStream;

public interface TableExporter {

    void skipCells(int numCells);

    void nextCell(String value);

    void nextRow();

    void export(OutputStream outputStream);
}
