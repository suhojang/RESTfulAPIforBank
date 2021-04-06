package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.BooleanCell;
import jxl.BooleanFormulaCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.DateFormulaCell;
import jxl.ErrorFormulaCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.biff.FontRecord;
import jxl.biff.formula.FormulaException;
import jxl.format.Alignment;
import jxl.format.BoldStyle;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.CellView;

import com.kwic.io.JOutputStream;

/**
 * <pre>
 * Title		: JXL
 * Description	: jxl library를 이용한 excel 작성
 * Date			: 2012.9.01
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈 
 * @version	1.0
 * @since 1.6
 */
public class JXL{
	public static final int TYPE_HSSF	= 1;
	public static final int TYPE_XSSF	= 2;

	public static int COLUMN_TYPE_STRING	= 1;
	public static int COLUMN_TYPE_INTEGER	= 2;
	public static int COLUMN_TYPE_DOUBLE	= 5;
	public static int COLUMN_TYPE_DATE		= 3;
	public static int COLUMN_TYPE_BINARY	= 4;
	
	public static final	String[] apb	= new String[]{
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
		,"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
		,"BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ"
		,"CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ"
		,"DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ"
		,"EA","EB","EC","ED","EE","EF","EG","EH","EI","EJ","EK","EL","EM","EN","EO","EP","EQ","ER","ES","ET","EU","EV","EW","EX","EY","EZ"
	};
	
	public static final int ALIGN_LEFT		= 1;
	public static final int ALIGN_CENTER	= 2;
	public static final int ALIGN_RIGHT		= 3;
	
	public static final int ALIGN_LEFT_IMPACT		= 11;
	public static final int ALIGN_CENTER_IMPACT		= 12;
	public static final int ALIGN_RIGHT_IMPACT		= 13;

	public static final int ALIGN_LEFT_HEAD			= 21;
	public static final int ALIGN_CENTER_HEAD		= 22;
	public static final int ALIGN_RIGHT_HEAD		= 23;
	
	private String[] extendedApb;
	
	private WritableWorkbook 	jWBook;
	private InputStream 		is;
	private OutputStream 		os;

	public final WritableCellFormat CELL_THOUSANDS_INTEGER	= new WritableCellFormat(NumberFormats.THOUSANDS_INTEGER);
	{
		try {
			CELL_THOUSANDS_INTEGER.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_INTEGER.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_INTEGER.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_INTEGER.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_FLOAT		= new WritableCellFormat(NumberFormats.THOUSANDS_FLOAT);
	{
		try {
			CELL_THOUSANDS_FLOAT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_FLOAT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_FLOAT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_FLOAT.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_INTEGER				= new WritableCellFormat(NumberFormats.INTEGER);
	{
		try {
			CELL_INTEGER.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_INTEGER.setAlignment(Alignment.RIGHT);
			CELL_INTEGER.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_INTEGER.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_FLOAT				= new WritableCellFormat(NumberFormats.FLOAT);
	{
		try {
			CELL_FLOAT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_FLOAT.setAlignment(Alignment.RIGHT);
			CELL_FLOAT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_FLOAT.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_INTEGER_IMPACT	= new WritableCellFormat(NumberFormats.THOUSANDS_INTEGER);
	{
		try {
			CELL_THOUSANDS_INTEGER_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_INTEGER_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_INTEGER_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_INTEGER_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_FLOAT_IMPACT	= new WritableCellFormat(NumberFormats.THOUSANDS_FLOAT);
	{
		try {
			CELL_THOUSANDS_FLOAT_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_FLOAT_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_FLOAT_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_FLOAT_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_INTEGER_IMPACT	= new WritableCellFormat(NumberFormats.INTEGER);
	{
		try {
			CELL_INTEGER_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_INTEGER_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_INTEGER_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_INTEGER_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}
	public final WritableCellFormat CELL_FLOAT_IMPACT	= new WritableCellFormat(NumberFormats.FLOAT);
	{
		try {
			CELL_FLOAT_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_FLOAT_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_FLOAT_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_FLOAT_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
		}
	}

	
	private WritableCellFormat headFormat;
	private WritableCellFormat cellFormat;
	private WritableCellFormat cellFormatL;
	private WritableCellFormat cellFormatC;
	private WritableCellFormat cellFormatR;
	private WritableCellFormat impactFormatL;
	private WritableCellFormat impactFormatC;
	private WritableCellFormat impactFormatR;
	private WritableCellFormat impactFormatLH;
	private WritableCellFormat impactFormatCH;
	private WritableCellFormat impactFormatRH;
	
	private Map<String,WritableCellFormat>	numberFormats;
	private Map<String,WritableCellFormat>	dateFormats;
	
	public JXL() throws IOException, BiffException, WriteException{
		this(new JOutputStream());
	}
	public JXL(File file) throws IOException, BiffException, WriteException{
		this(file,new JOutputStream());
	}
	public JXL(File src,File tgt) throws IOException, BiffException, WriteException{
		this(new FileInputStream(src),new FileOutputStream(tgt));
	}
	public JXL(File src,OutputStream os) throws BiffException, IOException, WriteException{
		this(new FileInputStream(src),os);
	}
	public JXL(OutputStream os) throws IOException, WriteException{
		this.os	= os;
		jWBook	= Workbook.createWorkbook(os);
		initCellFormat();
	}
	public JXL(InputStream is) throws BiffException, IOException, WriteException{
		this.is	= is;
		os		= new JOutputStream();
		jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		initCellFormat();
	}
	public JXL(InputStream is,OutputStream os) throws BiffException, IOException, WriteException{
		if(is!=null && os!=null){
			jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		}else if(is!=null && os==null){
			os		= new JOutputStream();
			jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		}else if(is==null && os!=null){
			jWBook	= Workbook.createWorkbook(os);
		}else{
			os	= new JOutputStream();
			jWBook	= Workbook.createWorkbook(os);
		}

		this.is	= is;
		this.os	= os;
		initCellFormat();
	}
	
	//get bytes array
	public byte[] getBytes(){
		if(os.getClass()==JOutputStream.class){
			return ((JOutputStream)os).getBytes();
		}
		return null;
	}

	//write excel file
	public void write() throws IOException{
		jWBook.write();
	}
	//close readable & writable stream
	public void close(){
		try{if(is!=null)is.close();}catch(Exception e){is=null;}
		try{if(os!=null)os.close();}catch(Exception e){os=null;}
		try{if(jWBook!=null)jWBook.close();}catch(Exception e){jWBook=null;}
	}
	//set cell align
	public void setCellAlign(int sIdx,int rIdx,int cIdx,int align){
		WritableCellFormat frmt	= null;
		
		if(align==ALIGN_LEFT)
			frmt	= cellFormatL;
		else if(align==ALIGN_CENTER)
			frmt	= cellFormatC;
		else if(align==ALIGN_RIGHT)
			frmt	= cellFormatR;
		else if(align==ALIGN_LEFT_IMPACT)
			frmt	= impactFormatL;
		else if(align==ALIGN_CENTER_IMPACT)
			frmt	= impactFormatC;
		else if(align==ALIGN_RIGHT_IMPACT)
			frmt	= impactFormatR;
		else if(align==ALIGN_LEFT_HEAD)
			frmt	= impactFormatLH;
		else if(align==ALIGN_CENTER_HEAD)
			frmt	= impactFormatCH;
		else if(align==ALIGN_RIGHT_HEAD)
			frmt	= impactFormatRH;
		
		getSheet(sIdx).getWritableCell(cIdx, rIdx).setCellFormat(frmt);
	}

	//initialize cell format
	public void initCellFormat() throws WriteException{
		cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.ALL , BorderLineStyle.THIN);

		cellFormatL = new WritableCellFormat();
		cellFormatL.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatL.setAlignment(Alignment.LEFT);
		cellFormatL.setVerticalAlignment(VerticalAlignment.CENTRE);
		cellFormatC = new WritableCellFormat();
		cellFormatC.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatC.setAlignment(Alignment.CENTRE);
		cellFormatC.setVerticalAlignment(VerticalAlignment.CENTRE);
		cellFormatR = new WritableCellFormat();
		cellFormatR.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatR.setAlignment(Alignment.RIGHT);
		cellFormatR.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		headFormat	= new WritableCellFormat();
		headFormat.setBorder(Border.ALL , BorderLineStyle.THIN);
		headFormat.setBackground(jxl.format.Colour.YELLOW);
		FontRecord font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		headFormat.setAlignment(Alignment.CENTRE);
		headFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		headFormat.setFont(font);
		

		impactFormatL	= new WritableCellFormat();
		impactFormatL.setBorder(Border.ALL , BorderLineStyle.THIN);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatL.setAlignment(Alignment.LEFT);
		impactFormatL.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatL.setFont(font);
		
		impactFormatC	= new WritableCellFormat();
		impactFormatC.setBorder(Border.ALL , BorderLineStyle.THIN);
		//impactFormatC.setBackground(jxl.format.Colour.ORANGE);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatC.setAlignment(Alignment.CENTRE);
		impactFormatC.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatC.setFont(font);
		
		impactFormatR	= new WritableCellFormat();
		impactFormatR.setBorder(Border.ALL , BorderLineStyle.THIN);
		//impactFormatR.setBackground(jxl.format.Colour.ORANGE);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatR.setAlignment(Alignment.RIGHT);
		impactFormatR.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatR.setFont(font);

		impactFormatLH	= new WritableCellFormat();
		impactFormatLH.setBorder(Border.ALL , BorderLineStyle.THIN);
		impactFormatLH.setBackground(jxl.format.Colour.YELLOW);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatLH.setAlignment(Alignment.LEFT);
		impactFormatLH.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatLH.setFont(font);
		
		impactFormatCH	= new WritableCellFormat();
		impactFormatCH.setBorder(Border.ALL , BorderLineStyle.THIN);
		impactFormatCH.setBackground(jxl.format.Colour.YELLOW);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatCH.setAlignment(Alignment.CENTRE);
		impactFormatCH.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatCH.setFont(font);
		
		impactFormatRH	= new WritableCellFormat();
		impactFormatRH.setBorder(Border.ALL , BorderLineStyle.THIN);
		impactFormatRH.setBackground(jxl.format.Colour.YELLOW);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatRH.setAlignment(Alignment.RIGHT);
		impactFormatRH.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatRH.setFont(font);
		
		numberFormats	= new HashMap<String,WritableCellFormat>();
		dateFormats		= new HashMap<String,WritableCellFormat>();
	}
	public int getRowsize(int sheetIdx){
		return jWBook.getSheet(sheetIdx).getRows();
	}
	public String toString(){
		StringBuffer sb	= new StringBuffer();
		try{
			WritableSheet[] sheetArr	= jWBook.getSheets();
			Cell[] cellArr	= null;
			for(int i=0;i<sheetArr.length;i++){
				sb.append("---- "+sheetArr[i].getName()+" ----\n");
				for(int j=0;j<sheetArr[i].getRows();j++){
					cellArr	= sheetArr[i].getRow(j);
					for(int k=0;k<cellArr.length;k++){
						sb.append(k==0?"":",").append(getValue(cellArr[k]));
					}
					sb.append("\n");
				}
			}
		}catch(Exception e){
			sb.append("[toString ERROR]");
		}
		return sb.toString();
	}
	//get excel column index (AA~ZZ)
	public String getXLColumnName(int cIdx){
		if(cIdx<apb.length)
			return apb[cIdx];

		if(extendedApb!=null)
			return extendedApb[cIdx-apb.length];
		
		extendedApb	= new String[apb.length*apb.length];
		
		for(int i=0;i<apb.length;i++){
			for(int j=0;j<apb.length;j++){
				extendedApb[i*apb.length+j]	= apb[i]+apb[j];
			}
		}
		return extendedApb[cIdx-apb.length];
	}
	public WritableSheet getSheet(String sheetName){
		return jWBook.getSheet(sheetName);
	}
	public WritableSheet getSheet(int sIdx){
		return jWBook.getSheet(sIdx);
	}
	public WritableSheet addSheet(){
		return jWBook.createSheet("sheet_"+jWBook.getNumberOfSheets(), jWBook.getNumberOfSheets());
	}	
	public WritableSheet addSheet(String sheetName){
		return jWBook.createSheet(sheetName, jWBook.getNumberOfSheets());
	}	
	public WritableSheet insertSheet(int sIdx){
		return jWBook.createSheet("sheet_"+sIdx, sIdx);
	}
	public WritableSheet insertSheet(String sheetName,int sIdx){
		return jWBook.createSheet(sheetName, sIdx);
	}
	public String getSheetName(int sIdx){
		return jWBook.getSheetNames()[sIdx];
	}
	public int getSheetIndex(String sheetName){
		String[] arr	= jWBook.getSheetNames();
		for(int i=0;i<arr.length;i++){
			if(arr[i].equals(sheetName))
				return i;
		}
		return -1;
	}
	
	
	
	public void addTitleRow(int sIdx,Object[] arr) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addRow(sIdx,arr,true);
	}
	public void addRow(int sIdx,Object[] arr) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addRow(sIdx,arr,false);
	}
	public void addRow(int sIdx,Object[] arr,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		WritableSheet sheet	= getSheet(sIdx);
		int rowIdx	= sheet.getRows()==0?0:sheet.getRows();
		
		for(int i=0;i<arr.length;i++){
			addCell(sIdx,rowIdx,i,arr[i],isHead);
		}
	}
	public void insertRow(int sIdx,int rIdx,Object[] arr,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		WritableSheet sheet	= getSheet(sIdx);
		if(rIdx<0)
			rIdx	= 0;
		if(rIdx>=sheet.getRows()){
			addRow(sIdx,arr,isHead);
			return;
		}
		sheet.insertRow(rIdx);
	}

	
	private void addCell(int sIdx,int rIdx,int cIdx,Object val,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,isHead?headFormat:cellFormat);
	}
	public void addCell(int sIdx,int rIdx,int cIdx,Object val,WritableCellFormat cf) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int	size = 2048;

		WritableSheet sheet	= getSheet(sIdx);
		if(val==null){
			sheet.addCell(new jxl.write.Label(cIdx,rIdx,"",cf));
		}else if(val.getClass()==Boolean.class || val.getClass()==boolean.class){
			sheet.addCell(new jxl.write.Boolean(cIdx,rIdx,(Boolean)val,cf));
		}else if(val.getClass()==String.class){
			if(val.toString().trim().startsWith("="))
				sheet.addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(val).trim().substring(1),cf));
			else
				sheet.addCell(new jxl.write.Label(cIdx,rIdx,String.valueOf(val),cf));

			size	= String.valueOf(val).getBytes("EUC-KR").length*350;		
		}else if(val.getClass()==Integer.class|| val.getClass()==int.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Integer)val,cf));
			size	= new java.math.BigDecimal((Integer)val).toString().length()*250;		
		}else if(val.getClass()==Long.class|| val.getClass()==long.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Long)val,cf));
			size	= new java.math.BigDecimal((Long)val).toString().length()*250;	
		}else if(val.getClass()==Short.class|| val.getClass()==short.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Short)val,cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==Double.class|| val.getClass()==double.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Double)val,cf));
			size	= new java.math.BigDecimal((Double)val).toString().length()*250;		
		}else if(val.getClass()==Float.class|| val.getClass()==float.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Float)val,cf));
			size	= new java.math.BigDecimal((Float)val).toString().length()*250;		
		}else if(val.getClass()==BigInteger.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,((BigInteger)val).intValue(),cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==BigDecimal.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,((BigDecimal)val).doubleValue(),cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==java.util.Date.class){
			sheet.addCell(new jxl.write.DateTime(cIdx,rIdx,(java.util.Date)val,cf));
			size	= 250*12;
		}else{
			sheet.addCell(new jxl.write.Label(cIdx,rIdx,String.valueOf(val),cf));
			size	= String.valueOf(val).getBytes("EUC-KR").length*250;		
		}
		CellView cv	= sheet.getColumnView(cIdx);
		if(rIdx!=0 && cv.getSize()<size){
			cv.setSize((int)size);
		}
		sheet.setColumnView(cIdx, cv);
	}
	
	public void setCellSize(int sIdx,int cIdx,int size){
		jxl.CellView cv	= null;
		cv	= getSheet(sIdx).getColumnView(cIdx);
		cv.setSize(size);
		getSheet(sIdx).setColumnView(cIdx, cv);
	}

	public void setCellSize(int sIdx,int[] sizeArr){
		jxl.CellView cv	= null;
		for(int i=0;i<sizeArr.length;i++){
			if(sizeArr[i]<0)
				continue;
			cv	= getSheet(sIdx).getColumnView(i);
			cv.setSize(sizeArr[i]);
			getSheet(sIdx).setColumnView(i, cv);
		}
	}
	
	public void setValue(int sIdx,int rIdx,int cIdx,String formula,String format) throws RowsExceededException, WriteException{
		if(!formula.toString().trim().startsWith("="))
			return;
		if(format.indexOf("y")>=0 || format.indexOf("M")>=0 || format.indexOf("d")>=0 || format.indexOf("H")>=0 || format.indexOf("m")>=0 || format.indexOf("s")>=0 || format.indexOf("S")>=0 || format.indexOf("W")>=0){
			getSheet(sIdx).addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(formula).trim().substring(1),makeDateFromat(format)));
		}else{
			getSheet(sIdx).addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(formula).trim().substring(1),makeNumberFromat(format)));
		}
	}
	public void setValue(int sIdx,int rIdx,int cIdx,short val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Short(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,short val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Short(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,int val,String format) throws WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Integer(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,int val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Integer(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,long val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Long(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,long val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Long(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,float val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Float(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,float val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Float(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,double val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Double(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,double val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Double(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,java.util.Date val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,java.util.Date val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,Object val) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		setValue(sIdx,rIdx,cIdx,val,false);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,Object val,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,isHead);
	}
	
	//format : SimpleDateFormat
	public void setDateFormat(int sIdx,int rIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,getValue(sIdx,rIdx,cIdx),makeDateFromat(format));
	}
	//format : NumberFormat
	public void setNumberFormat(int sIdx,int rIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,getValue(sIdx,rIdx,cIdx),makeNumberFromat(format));
	}
	
	private WritableCellFormat makeDateFromat(String format) throws WriteException{
		WritableCellFormat cf	= null;
		if(dateFormats.get(format)==null){
			cf = new WritableCellFormat(new DateFormat(format));
			cf.setBorder(Border.ALL , BorderLineStyle.THIN);
			cf.setAlignment(Alignment.CENTRE);
			dateFormats.put(format,cf);
		}
		cf	= dateFormats.get(format);
		return cf;
	}
	
	private WritableCellFormat makeNumberFromat(String format) throws WriteException{
		
		WritableCellFormat cf	= null;
		if(numberFormats.get(format)==null){
			cf = new WritableCellFormat(new NumberFormat(format));
			cf.setBorder(Border.ALL , BorderLineStyle.THIN);
			cf.setAlignment(Alignment.RIGHT);
			numberFormats.put(format,cf);
		}
		cf	= numberFormats.get(format);
		
		return cf;
	}
	
	//열 전체 날짜 형식 지정
	public void setDateFormat(int sIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int rows	= getSheet(sIdx).getRows();
		for(int i=0;i<rows;i++){
			setDateFormat(sIdx,i,cIdx,format);
		}
	}
	//열전체 숫자형식 지정
	public void setNumberFormat(int sIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int rows	= getSheet(sIdx).getRows();
		for(int i=0;i<rows;i++){
			setNumberFormat(sIdx,i,cIdx,format);
		}
	}
	
	public Object[] getValues(int sIdx,int rIdx){
		Cell[] cells	= getSheet(sIdx).getRow(rIdx);
		
		Object[] vals	= new Object[cells.length];
		for(int i=0;i<vals.length;i++){
			vals[i]	= getValue(cells[i]);
		}
		return vals;
	}
	public Object getValue(int sIdx,int rIdx,int cIdx){
		return getValue(getSheet(sIdx).getCell(cIdx,rIdx));
	}
	public Object getValue(Cell cell){
		if(cell.getType()==CellType.BOOLEAN){
			return ((BooleanCell)cell).getValue();
		}else if(cell.getType()==CellType.BOOLEAN_FORMULA){
			return ((BooleanFormulaCell )cell).getValue();
		}else if(cell.getType()==CellType.DATE){
			return ((DateCell)cell).getDate();
		}else if(cell.getType()==CellType.DATE_FORMULA){
			return ((DateFormulaCell )cell).getDate();
		}else if(cell.getType()==CellType.FORMULA_ERROR){
			return ((ErrorFormulaCell)cell).getErrorCode();
		}else if(cell.getType()==CellType.LABEL){
			return ((LabelCell)cell).getString();
		}else if(cell.getType()==CellType.STRING_FORMULA){
			return ((StringFormulaCell)cell).getString();
		}else if(cell.getType()==CellType.NUMBER){
			return ((NumberCell)cell).getValue();
		}else if(cell.getType()==CellType.NUMBER_FORMULA){
			return ((NumberFormulaCell)cell).getValue();
		}
		return cell.getContents();
	}	
	
	public short getShort(int sIdx,int rIdx,int cIdx){
		return (Short)getValue(sIdx,rIdx,cIdx);
	}
	public int getInt(int sIdx,int rIdx,int cIdx){
		return (Integer)getValue(sIdx,rIdx,cIdx);
	}
	public long getLong(int sIdx,int rIdx,int cIdx){
		return (Long)getValue(sIdx,rIdx,cIdx);
	}
	public float getFloat(int sIdx,int rIdx,int cIdx){
		return (Float)getValue(sIdx,rIdx,cIdx);
	}
	public double getDouble(int sIdx,int rIdx,int cIdx){
		return (Double)getValue(sIdx,rIdx,cIdx);
	}
	public java.util.Date getDate(int sIdx,int rIdx,int cIdx){
		return (java.util.Date)getValue(sIdx,rIdx,cIdx);
	}
	public String getString(int sIdx,int rIdx,int cIdx){
		return String.valueOf(getValue(sIdx,rIdx,cIdx));
	}
	public boolean getBoolean(int sIdx,int rIdx,int cIdx){
		return (Boolean)(getValue(sIdx,rIdx,cIdx));
	}
	public String getFormula(int sIdx,int rIdx,int cIdx) throws FormulaException{
		return getFormula(getSheet(sIdx).getCell(cIdx,rIdx));
	}
	public String getFormula(Cell cell) throws FormulaException{
		if(cell.getType()==CellType.BOOLEAN_FORMULA){
			return ((BooleanFormulaCell )cell).getFormula();
		}else if(cell.getType()==CellType.DATE_FORMULA){
			return ((DateFormulaCell )cell).getFormula();
		}else if(cell.getType()==CellType.FORMULA_ERROR){
			return ((ErrorFormulaCell)cell).getFormula();
		}else if(cell.getType()==CellType.STRING_FORMULA){
			return ((StringFormulaCell)cell).getFormula();
		}else if(cell.getType()==CellType.NUMBER_FORMULA){
			return ((NumberFormulaCell)cell).getFormula();
		}
		return cell.getContents();
	}

	public void mergeCells(int sIdx, int sRowIdx,int sColIdx,int eRowIdx,int eColIdx) throws RowsExceededException, WriteException{
		getSheet(sIdx).mergeCells(sColIdx, sRowIdx, eColIdx, eRowIdx );
	} 
	public static JXL makeExcel(String sheetname,String[][] titleArr,String[] colArr,int[] colAlignArr,List<Map<String,Object>> list) throws Exception{
		return appendSheet(null,0,sheetname,titleArr,colArr,colAlignArr,list); 
	}
	public static JXL makeExcel(JXL jxl,int startSheetIdx,String sheetname,String[][] titleArr,String[] colArr,int[] colAlignArr,List<Map<String,Object>> list) throws Exception{
		return appendSheet(jxl,startSheetIdx,sheetname,titleArr,colArr,colAlignArr,list); 
	}
	public static JXL appendSheet(JXL jxl,int startSheetIdx,String sheetname,String[][] titleArr,String[] colArr,int[] colAlignArr,List<Map<String,Object>> list) throws Exception{
		int[] colTypeArr	= new int[colArr.length];
		for(int i=0;i<colTypeArr.length;i++)
			colTypeArr[i]	= COLUMN_TYPE_STRING;
		return appendSheet(jxl, startSheetIdx, sheetname,titleArr,colArr,colTypeArr,colAlignArr,list,titleArr.length);
	}
	@SuppressWarnings("unchecked")
	public static JXL appendSheet(JXL jxl, int startSheetIdx, String sheetname,String[][] titleArr,String[] colArr,int[] colTypeArr,int[] colAlignArr,List<Map<String,Object>> list,int sRIdx) throws Exception{
		//JXL-MERGE{'jxl-sidx':sidx,'jxl-ridx':ridx,coName:{jxl-cidx:cidx,row:x1,col:y1},coName2:{row:x2,col:y2},...}}
		/*
		Map<String,Object> merge	= new HashMap<String,Object>();
		Map<String,Integer> inf	= new HashMap<String,Integer>();
		
		Map<String,Object> title	= new HashMap<String,Object>();
		title.put("col1","판매대금 계좌 및 잔액");
		title.put("col2","");
		title.put("col3","");
		title.put("col4","");
		title.put("col5","");

		merge	= new HashMap<String,Object>();
		inf	= new HashMap<String,Integer>();
		inf.put("col", 5);
		inf.put("row", 1);
		merge.put("col1", inf);
		title.put("JXL-MERGE", merge);
		
		list.add(0, title);
		list.add(0, qls갲);
		
		
		inf.put("row", 5);
		inf.put("row", 2);
		merge.put("col1", inf);
		list.get(0).put("JXL-MERGE", merge); 
		*/
		if(list==null)
			return null;
		if(jxl==null)
			jxl	= new JXL();
		int sheetIdx	= 0;
		int rowLimit	= 60000;
		Map<String,Object> mergeRowInf	= null;
		try{
			Object val	= null;
			
			jxl.addSheet(sheetname);
			for(int i=0;i<titleArr.length;i++)
				jxl.addTitleRow(startSheetIdx, titleArr[i]);
			
			Map<String,Object> inf	= null;
			for(int i=0;i<list.size();i++){
				if(i>=(sheetIdx+1)*rowLimit){
					sheetIdx++;
					jxl.addSheet(sheetname+"_"+String.valueOf(sheetIdx));
					sRIdx	= 0;
				}
				inf	= list.get(i);
				//merge가 있을 경우
				if(list.get(i).get("JXL-MERGE")!=null){
					mergeRowInf	= (Map<String,Object>)list.get(i).get("JXL-MERGE");
					mergeRowInf.put("jxl-sidx",sheetIdx+startSheetIdx);//merge sheet index
					mergeRowInf.put("jxl-ridx",i+sRIdx-(sheetIdx*rowLimit));//merge start row index
				}else{
					mergeRowInf	= null;
				}
				
				for(int j=0;j<colArr.length;j++){
					if(mergeRowInf!=null && mergeRowInf.get(colArr[j])!=null){
						((Map<String,Object>)mergeRowInf.get(colArr[j])).put("jxl-cidx",j);//merge start col index
					}
					if(colTypeArr[j]==COLUMN_TYPE_INTEGER){
						jxl.setValue(sheetIdx+startSheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Long.parseLong((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_INTEGER);
					}else if(colTypeArr[j]==COLUMN_TYPE_DOUBLE){
						if(Double.parseDouble((String)inf.get(colArr[j])) ==Long.parseLong((String)inf.get(colArr[j]))){
							jxl.setValue(sheetIdx+startSheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Long.parseLong((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_INTEGER);
						}else{
							jxl.setValue(sheetIdx+startSheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Double.parseDouble((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_FLOAT);
						}
					}else{
						val	= inf.get(colArr[j]);
						jxl.setValue(sheetIdx+startSheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, val);
						jxl.setCellAlign(sheetIdx+startSheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, colAlignArr[j]);
					}
				}
			}
			
			//merge
			Iterator<String> iter	= null;
			String key	= null;
			Map<String,Integer> mergeCellInf	= null;
			int rowSpan	= 0;
			int colSpan	= 0;
			int rIdx	= 0;
			int cIdx	= 0;
			for(int i=0;i<list.size();i++){
				mergeRowInf	= (Map<String,Object>)list.get(i).get("JXL-MERGE");
				if(mergeRowInf==null)
					continue;
				iter	= mergeRowInf.keySet().iterator();
				while(iter.hasNext()){
					key	= iter.next();
					mergeCellInf	= (Map<String, Integer>) mergeRowInf.get(key);
					
					rowSpan	= mergeCellInf.get("row")==null?0:mergeCellInf.get("row")-1;
					colSpan	= mergeCellInf.get("col")==null?0:mergeCellInf.get("col")-1;
					rIdx	= (int)mergeRowInf.get("jxl-ridx");
					cIdx	= mergeCellInf.get("jxl-cidx");
					jxl.mergeCells((int)mergeRowInf.get("jxl-sidx"), cIdx,rIdx,cIdx+colSpan,rIdx+rowSpan);
				}
			}			
		}catch(Exception e){
			throw e;
		}finally{
		}
		return jxl;
	}	

	public static void encrypt(File inFile, File outfile, String password) throws Exception{
		encrypt(new FileInputStream(inFile), new FileOutputStream(outfile), password,inFile.getName().endsWith(".xls")?TYPE_HSSF:TYPE_XSSF);
	}	
	public static void encrypt(InputStream inFile, OutputStream outfile, String password,int type) throws Exception{
		if(type==TYPE_HSSF)
			encryptHSSF(inFile, outfile, password);
		else
			encryptXSSF(inFile, outfile, password);
	}
	/*public static void encryptXSSF(File inFile, File outfile, String password)  throws Exception{
		XSSFWorkbook	wb	= new XSSFWorkbook(new FileInputStream(inFile));
		
		POIFSFileSystem fs = new POIFSFileSystem();
		EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile, CipherAlgorithm.aes192, HashAlgorithm.sha384, -1, -1, null);
		Encryptor enc = info.getEncryptor();
		enc.confirmPassword(password);

		OutputStream os = enc.getDataStream(fs);
		wb.getCTWorkbook().save(os);
		wb.close();
		
		FileOutputStream fos = new FileOutputStream(outfile);
		fs.writeFilesystem(fos);
		fos.close();		
	}*/
	//xssf not working..
	public static void encryptXSSF(InputStream inFile, OutputStream outfile, String password) throws Exception{
		XSSFWorkbook		xwb	= null;
		POIFSFileSystem		fs	= null;
		try {
			xwb	= new XSSFWorkbook(inFile);
			xwb.setWorkbookPassword(password, HashAlgorithm.sha256);
			xwb.write(outfile);
		}catch(Exception e) {
			throw e;
		}finally {
			try {if(xwb!=null)xwb.close();}catch(Exception e) {xwb=null;}
			try {if(fs!=null)fs.close();}catch(Exception e) {fs=null;}
			try {if(inFile!=null)inFile.close();}catch(Exception e) {inFile=null;}
			try {if(outfile!=null)outfile.close();}catch(Exception e) {outfile=null;}
		}
	}
	//hssf working
	public static void encryptHSSF(InputStream inFile, OutputStream outfile, String password) throws Exception{
		HSSFWorkbook		hwb	= null;
		POIFSFileSystem		fs	= null;
		try {
			fs	= new POIFSFileSystem(inFile);
			hwb	= new HSSFWorkbook(fs);
			hwb.writeProtectWorkbook(password, "");
			hwb.write(outfile);
		}catch(Exception e) {
			throw e;
		}finally {
			try {if(hwb!=null)hwb.close();}catch(Exception e) {hwb=null;}
			try {if(fs!=null)fs.close();}catch(Exception e) {fs=null;}
			try {if(inFile!=null)inFile.close();}catch(Exception e) {inFile=null;}
			try {if(outfile!=null)outfile.close();}catch(Exception e) {outfile=null;}
		}
	}
}



