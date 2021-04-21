package com.buss.mvn.check;

import java.io.File;
import java.io.FileFilter;

public class CheckMvn {

	public static void checkDirs(File dir, HandResult hs) {

		if (containsFile(dir, f -> "_remote.repositories".equals(f.getName()))) {
//			if (!containsVersion(dir, "jar") && !containsVersion(dir, "pom")) {
//				hs.handResult(dir);
//			}
			return;
		}

		if (containsFile(dir, f -> f.getName().endsWith(".lastUpdated"))) {
			hs.handResult(dir);
			return;
		}
		
		for (File f : dir.listFiles(f -> f.isDirectory())) {
			checkDirs(f, hs);
		}
	}

	private static boolean containsFile(File f, FileFilter filter) {
		return f.listFiles(filter).length > 0;
	}

//	private static boolean containsVersion(File file, String end) {
//		return containsFile(file, f -> containsVersionFile(f, end));
//	}
//
//	private static boolean containsVersionFile(File f, String end) {
//		String version = f.getParentFile().getName();
//		String jarName = f.getParentFile().getParentFile().getName();
//		return f.getName().equals(jarName + "-" + version + "." + end);
//	}
}
