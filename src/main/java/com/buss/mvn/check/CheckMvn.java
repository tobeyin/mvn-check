package com.buss.mvn.check;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckMvn {

	public static void main(String[] args) {
		final List<File> tmpList = new ArrayList<File>();

		checkDirs(new File("C:\\Users\\Tony\\.m2\\repository"), f -> {
			tmpList.add(f);
		});

		tmpList.forEach(System.out::println);
	}

	private static void checkDirs(File dir, HandResult hs) {
		if (dir.isFile()) {
			return;
		}

		if (dir.listFiles(f -> "_remote.repositories".equals(f.getName())).length == 0) {
			for (File f : dir.listFiles(f -> f.isDirectory())) {
				checkDirs(f, hs);
			}
			return;
		}

		File[] fs = dir.listFiles(f -> {
			String version = f.getParentFile().getName();
			String jarName = f.getParentFile().getParentFile().getName();
			return f.getName().equals(jarName + "-" + version + ".jar");
		});

		if (fs.length == 0) {
			hs.handResult(dir);
		}
	}
}
