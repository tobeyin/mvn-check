package com.buss.mvn.check;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestCheckMvn {

	@Test
	public void testMvn() {
		
		new CheckMvn();
		
		final List<File> tmpList = new ArrayList<File>();

		File file = new File("C:\\Users\\Tony\\.m2\\repository");
		File file1 = new File("C:\\Users\\Tony\\.m2\\repository\\tools\\com.sun\\1.7\\m2e-lastUpdated.properties");

		CheckMvn.checkDirs(file, tmpList::add);
		CheckMvn.checkDirs(file1, f -> {});
		
		tmpList.forEach(System.out::println);
		// 删除缺失jar包的目录
//		CheckMvn.checkDirs(file, FileUtil::delete);
	}
	
}
