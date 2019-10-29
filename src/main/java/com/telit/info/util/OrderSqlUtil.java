package com.telit.info.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class OrderSqlUtil {
	
	public static void main(String[] args) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader("D:\\t_pay_tran.sql"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\t_pay_tran_out.sql"));
		while(true) {
			String data = reader.readLine();
			if (data == null) {
				break;
			}
			String[] ss = data.split(",");
			String str = ss[7];
			if (str.length() > 21) {
				String s = str.substring(0,21) + "'";
				data = data.replace(str,s);
			}
			writer.write(data);
			writer.newLine();
		}
		reader.close();
		writer.flush();
		writer.close();
	}
}
