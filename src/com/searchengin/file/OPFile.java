package com.searchengin.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import com.searchengin.config.ConstantParams;
import com.searchengin.full.config.InitParams;
import com.searchengin.util.DataBaseUtils;
import com.searchengin.util.StringUtils;

public class OPFile {

	public static void singleParserSohuNews(String htmlContent,String outputPath){
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			Pattern p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
			Matcher m_script = p_script.matcher(htmlContent); 
			htmlContent = m_script.replaceAll("");
			
			String regEx_line = "\r\n";
			Pattern p_line = Pattern.compile(regEx_line,Pattern.CASE_INSENSITIVE); 
			Matcher m_line = p_line.matcher(htmlContent); 
			htmlContent = m_line.replaceAll("");
			
			
			Parser parser = Parser.createParser(htmlContent, "gb2312");
			NodeFilter title_Filter = new AndFilter(new TagNameFilter("h1"),new HasAttributeFilter("itemprop","headline"));
			NodeList title_List = parser.parse(title_Filter);
			Node title_node = title_List.elementAt(0);
			String title = "";
			if(title_node != null){
				title = title_node.toPlainTextString();
			}
			parser.reset();
			
			NodeFilter date_Filter = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","time"));
			NodeList date_list = parser.parse(date_Filter);
			Node date_node = date_list.elementAt(0);
			String date = "";
			if(date_node != null){
				date = date_node.toPlainTextString();
			}
//			System.out.println(date);
			parser.reset();
			
			NodeFilter source_Filter = new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("itemprop","name"));
			NodeList source_list = parser.parse(source_Filter);
			Node source_node = source_list.elementAt(0);
			String source = "";
			if(source_node != null){
				source = source_node.toPlainTextString();
			}
//			System.out.println(source);
			parser.reset();
			
			NodeFilter content_Filter = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("itemprop","articleBody"));
			NodeList content_list = parser.parse(content_Filter);
			Node content_node = content_list.elementAt(0);
			String content = "";
			if(content_node != null){
				content = content_node.toPlainTextString();
			}
//			System.out.println(content);
			parser.reset();
			
			String result = title+ConstantParams.CHENG_LINE+date+ConstantParams.CHENG_LINE+source+ConstantParams.CHENG_LINE+content;
			StringUtils.string2File(result, outputPath);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveData(String inputPath){
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		String filePath = StringUtils.getConfigParam(InitParams.TXT_FILEPATH, "", InitParams.PROPERTIES_NAME);
		try {
			conn = DataBaseUtils.getConnection();
			String sqlString = "INSERT INTO s_news (newsTitle,sourceUrl,filePath,newsTime,sourceNet,columnId) VALUES ";
			StringBuilder stringBuilder = null;
			File file = new File(inputPath);
			File[] files = file.listFiles();
			for(File f : files){
				if(f.isDirectory()){
					saveData(f.getAbsolutePath());//递归
				}else{
					String sourcePath = f.getPath();
					String htmlContent = StringUtils.getContent(sourcePath);
					
					String fileName = StringUtils.getFileNameFromPath(sourcePath);
					String fileFullPath = filePath+fileName+".txt";
					
					int position = sourcePath.indexOf("mirror")+6;
					String sourceUrl = sourcePath.substring(position);
					sourceUrl = "http:/"+sourceUrl.replace("\\", "/");
					
					String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
					Pattern p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
					Matcher m_script = p_script.matcher(htmlContent); 
					htmlContent = m_script.replaceAll("");
					
					String regEx_line = "\r\n";
					Pattern p_line = Pattern.compile(regEx_line,Pattern.CASE_INSENSITIVE); 
					Matcher m_line = p_line.matcher(htmlContent); 
					htmlContent = m_line.replaceAll("");
					
					
					Parser parser = Parser.createParser(htmlContent, "gb2312");
					NodeFilter title_Filter = new AndFilter(new TagNameFilter("h1"),new HasAttributeFilter("itemprop","headline"));
					NodeList title_List = parser.parse(title_Filter);
					Node title_node = title_List.elementAt(0);
					String title = "";
					if(title_node != null){
						title = title_node.toPlainTextString();
					}
					parser.reset();
					
					NodeFilter content_Filter = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("itemprop","articleBody"));
					NodeList content_list = parser.parse(content_Filter);
					Node content_node = content_list.elementAt(0);
					String content = "";
					if(content_node != null){
						content = content_node.toPlainTextString();
					}
//					System.out.println(content);
					parser.reset();
					
					if(StringUtils.isNotEmpty(title) || StringUtils.isNotEmpty(content)){
						NodeFilter date_Filter = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","time"));
						NodeList date_list = parser.parse(date_Filter);
						Node date_node = date_list.elementAt(0);
						String date = "";
						if(date_node != null){
							date = date_node.toPlainTextString();
						}
//					System.out.println(date);
						parser.reset();
						
						NodeFilter source_Filter = new AndFilter(new TagNameFilter("span"),new HasAttributeFilter("itemprop","name"));
						NodeList source_list = parser.parse(source_Filter);
						Node source_node = source_list.elementAt(0);
						String source = "";
						if(source_node != null){
							source = source_node.toPlainTextString();
						}
//					System.out.println(source);
						parser.reset();
						
						StringUtils.string2File(content, fileFullPath);
						stringBuilder = new StringBuilder();
						stringBuilder.append("(");
						stringBuilder.append("'"+title+"'");
						stringBuilder.append(",");
						stringBuilder.append("'"+sourceUrl+"'");
						stringBuilder.append(",");
						stringBuilder.append("'"+fileFullPath+"'");
						stringBuilder.append(",");
						stringBuilder.append("'"+date+"'");
						stringBuilder.append(",");
						stringBuilder.append("'"+source+"'");
						stringBuilder.append(",");
						stringBuilder.append("1");
						stringBuilder.append(")");
						
						System.out.println(sqlString+stringBuilder.toString());
						
						pstmt = conn.prepareStatement(sqlString+stringBuilder.toString());
						pstmt.execute();
						
						//维护索引表
						String sql = "SELECT LAST_INSERT_ID()";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(sql);
						int id = 0;
						if(rs.next()){
							id = rs.getInt(1);
						}
						String indexTable = StringUtils.getConfigParam(InitParams.INDEXTABLE, "", InitParams.PROPERTIES_NAME);
						String insertSql = "insert into "+indexTable+" (businessId,TYPE,action) values (?,?,?)";
						pstmt = conn.prepareStatement(insertSql);
						pstmt.setInt(1, id);
						pstmt.setString(2, "news");
						pstmt.setString(3, "add");
						pstmt.execute();
					}
					
					
					//处理文件：（删除，复制***）
					String copyPath = StringUtils.getConfigParam(InitParams.COPY_FILEPATH, "", InitParams.PROPERTIES_NAME);
					String fullName = getFileFullNameFromPath(sourcePath);
					File targetFile = new File(copyPath+fullName);
					copyFile(f, targetFile);
					f.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DataBaseUtils.closeConnection(conn, pstmt, stmt, rs);
		}
	}
	
	public static void copyFile(File sourceFile,File targetFile){
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			
			byte[] b = new byte[1024 * 5];
			int len;
			while((len = inBuff.read(b)) != -1){
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(inBuff != null){
					inBuff.close();
				}
				if(outBuff != null){
					outBuff.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getFileFullNameFromPath(String path){
		String result = "";
		if(StringUtils.isEmpty(path)){
			return result;
		}
		if(path.contains("/")){
			result = path.substring(path.lastIndexOf("/")+1);
		}else if(path.contains("\\")){
			result = path.substring(path.lastIndexOf("\\")+1);
		}else{
			result = path;
		}
		return result;
	}
	
}
