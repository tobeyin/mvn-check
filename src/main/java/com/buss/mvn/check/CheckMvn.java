package com.buss.mvn.check;

import java.io.File;

public class CheckMvn {

	public static void checkDirs(File dir, HandResult hs) {
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
