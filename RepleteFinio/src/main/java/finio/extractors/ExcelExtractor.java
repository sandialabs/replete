package finio.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import finio.core.NonTerminal;

public class ExcelExtractor extends NonTerminalExtractor {


    ///////////
    // FIELD //
    ///////////

    private File file;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExcelExtractor(File file) {
        this.file = file;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        NonTerminal M = createBlankNonTerminal();
        try {
            InputStream inputStream = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(inputStream);
            for(int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                NonTerminal Msheet = createBlankNonTerminal();
                M.put(sheet.getSheetName(), Msheet);
                for(int r = 0; r < sheet.getPhysicalNumberOfRows(); r++) {
                    NonTerminal Mrow = createBlankNonTerminal();
                    Msheet.put(r, Mrow);
                    Row row = sheet.getRow(r);
                    if(row == null)  {
                        // Shouldn't happen
                        continue;
                    }
                    for(int c = 0; c < row.getPhysicalNumberOfCells(); c++) {
                        Cell cell = row.getCell(c);
                        if(cell == null) {
                            // Shouldn't happen
                            continue;
                        }
                        Object contents;
                        switch(cell.getCellType()) {
                            case NUMERIC:
                                contents = cell.getNumericCellValue();
                                break;
                            case STRING:
                                contents = cell.getStringCellValue();
                                break;
                            case FORMULA:
                                contents = cell.getCellFormula();
                                break;
                            case BLANK:
                                contents = null;
                                break;
                            case BOOLEAN:
                                contents = cell.getBooleanCellValue();
                                break;
                            case ERROR:
                                contents = cell.getErrorCellValue();
                                break;
                            default:
                                contents = "<UNKNOWN CELL TYPE>";
                                break;
                        }
                        Mrow.put(c, contents);
                    }
                }
            }
            return M;
        } catch(Exception e) {
            throw new RuntimeException("Excel Extraction Failed", e);
        }
    }

    @Override
    protected String getName() {
        return "Excel Extractor";
    }
}
