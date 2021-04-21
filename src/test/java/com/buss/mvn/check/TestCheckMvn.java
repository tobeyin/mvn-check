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

		CheckMvn.checkDirs(file, tmpList::add);
		
		tmpList.forEach(System.out::println);
		// 删除缺失jar包的目录
//		CheckMvn.checkDirs(file, FileUtil::delete);
	}
	
}
