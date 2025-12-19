package programs;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	public static void main(String[] args) throws IOException {
		// Path of your Excel file
		FileInputStream file = new FileInputStream("D:\\Eclispseworkspace\\ICTC\\src\\test\\resources\\employee.xlsx");

		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);

		int rowCount = sheet.getPhysicalNumberOfRows();

		// Loop through rows using normal for loop
		for (int i = 1; i < rowCount; i++) { // i=1 → skip header row
			Row row = sheet.getRow(i);

			if (row != null) {
				Cell nameCell = row.getCell(0); // first column (name)

				if (nameCell != null && nameCell.getStringCellValue().equalsIgnoreCase("john")) {
					// Print all cells in this row
					int colCount = row.getPhysicalNumberOfCells();

					for (int j = 0; j < colCount; j++) {
						Cell cell = row.getCell(j);

						if (cell != null) {
							switch (cell.getCellType()) {
							case STRING:
								System.out.print(cell.getStringCellValue() + "\t");
								break;
							case NUMERIC:
								System.out.print(cell.getNumericCellValue() + "\t");
								break;
							case BOOLEAN:
								System.out.print(cell.getBooleanCellValue() + "\t");
								break;
							default:
								System.out.print(" \t");
							}
						}
					}
					System.out.println();
				}
			}
		}

		workbook.close();
		file.close();
	}
}
