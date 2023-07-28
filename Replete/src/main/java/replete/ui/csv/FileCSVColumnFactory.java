package replete.ui.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class FileCSVColumnFactory implements CsvColumnFactory {

    private CSVReader reader;

    public FileCSVColumnFactory(File file) throws FileNotFoundException {
        reader = new CSVReader(new FileReader(file));
    }

    @Override
    public AbstractCommonCsvColumn createColumn(CsvColumnInfo info) {
        FileCsvColumn col = new FileCsvColumn(DefaultCsvColumnType.DEFAULT, info);
        return col;
    }

    @Override
    public List<AbstractCommonCsvColumn> createAllColumns() {
        List<AbstractCommonCsvColumn> result = new ArrayList<>();

        // get headers from CSVreader
        String[] headers = null;
        try {
            headers = reader.readNext();
            reader.close();
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (headers != null) {
            int index = 0;
            for (String header : headers) {
                result.add(createColumn(new FileCsvColumnInfo(header, "", index++)));
            }
        }
        return result;
    }

    @Override
    public List<CsvColumnType> getAvailableColumnTypes() {
        List<CsvColumnType> typeList = new ArrayList<>();
        typeList.add(DefaultCsvColumnType.DEFAULT);
        return typeList;
    }
}
