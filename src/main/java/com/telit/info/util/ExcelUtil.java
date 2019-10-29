package com.telit.info.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;

public class ExcelUtil {

	public static WritableCellFormat CenterCellFormat = new WritableCellFormat();

	static{
		try {
			CenterCellFormat.setAlignment(Alignment.CENTRE);
			CenterCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	public static List<List<String>> readExcel(InputStream is) throws Exception{
		Workbook wb = Workbook.getWorkbook(is);
		Sheet sheet = wb.getSheet(0);
		List<List<String>> datas = new ArrayList<List<String>>();
		for (int i = 0; i < sheet.getRows(); i++) {
			List<String> temp = new ArrayList<String>();
			for (int j = 0; j < sheet.getColumns() ; j++) {
				temp.add(sheet.getCell(j,i).getContents());
			}
			datas.add(temp);
		}
		wb.close();
		return datas;
	}
	
	public static WritableWorkbook writeExcel(OutputStream out,String[] titles,boolean over) throws Exception{
		WritableWorkbook book = Workbook.createWorkbook(out);
		WritableSheet sheet = book.createSheet("sheet1",0);
		for (int i = 0 ; i < titles.length ; i++){
			Label label = new Label(i,0,titles[i],CenterCellFormat);
			sheet.addCell(label);
		}
		if (over) {
			book.write();
			book.close();
		}
		return book;
	}
}
