package com.searchengin.file;

import java.io.File;

import junit.framework.TestCase;

import com.searchengin.util.StringUtils;

public class TestOPFile extends TestCase{

	public void test1(){
		String inputPath = "D:/searchengin/myheritrix/jobs/output/mirror/news.sohu.com/20110801/n315136029.shtml";
		String htmlContent = StringUtils.getContent(inputPath);
		String outputPath = "D:/verticalretrieve/testContent.txt";
		OPFile.singleParserSohuNews(htmlContent, outputPath);
	}
	
	public void test2(){
		String inputPath = "D:/verticalretrieve/sourcepath/mirror";
		OPFile.saveData(inputPath);
	}
	
	public void test3(){
		String inputPath = "G:/verticalretrieve/sourcepath/mirror/news.sohu.com/20140215/n395039562.shtml";
		String outputPath = "G:/verticalretrieve/filebat/n395039562.shtml";
		File sourceFile = new File(inputPath);
		File targetFile = new File(outputPath);
		OPFile.copyFile(sourceFile, targetFile);
	}
	
}
