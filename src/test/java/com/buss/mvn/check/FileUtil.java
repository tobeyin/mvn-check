package com.buss.mvn.check;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

public class FileUtil {

	/**
	 * 处理文件
	 * 
	 * @param file    根目录（文件）
	 * @param filter  过滤器
	 * @param handler 处理方法
	 */
	public static void handFile(File file, FileFilter filter, Consumer<File> handler) {
		if (file.isFile()) {
			handler.accept(file);
		} else {
			for (File fs : file.listFiles(filter)) {
				handFile(fs, filter, handler);
			}
			handler.accept(file);
		}
	}

	/**
	 * 处理所有文件
	 * 
	 * @param file    根目录（文件）
	 * @param handler 处理方法
	 */
	public static void handAllFile(File file, Consumer<File> handler) {
		handFile(file, (f) -> true, handler);
	}

	/**
	 * 删除文件和文件夹
	 * 
	 * @param file 文件或文件夹
	 */
	public static void delete(File file) {
		handAllFile(file, (f) -> f.delete());
	}

	/**
	 * 删除文件和文件夹
	 * 
	 * @param file 文件或文件夹
	 */
	public static void delete(String file) {
		delete(new File(file));
	}

	/**
	 * 创建目录
	 * 
	 * @param file 目录
	 */
	public static void mkdir(File file) {
		file.mkdirs();
	}

	/**
	 * 创建目录
	 * 
	 * @param file 目录
	 */
	public static void mkdir(String file) {
		mkdir(new File(file));
	}

	/**
	 * 写内容到文件中
	 * 
	 * @param content 写入的内容
	 * @param file    需要写入的文件
	 * @throws IOException 当文件不存在时抛出该异常
	 */
	public static void writeFile(String content, File file) throws IOException {
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(content);
			fw.flush();
			fw.close();
		}
	}

	/**
	 * 写内容到文件中
	 * 
	 * @param content 写入的内容
	 * @param file    需要写入的文件
	 * @throws IOException 当文件不存在时抛出该异常
	 */
	public static void writeFile(String content, String file) throws IOException {
		writeFile(content, new File(file));
	}

	/**
	 * 读取文件的内容
	 * 
	 * @param file 需要读取的文件
	 * @return 文件的内容
	 * @throws IOException 文件不存在，或者读取错误时抛出该异常
	 */
	public static String readFile(File file) {
		try (FileReader fr = new FileReader(file)) {
			StringBuilder s = new StringBuilder();
			int val = -1;
			while ((val = fr.read()) != -1) {
				s.append((char) val);
			}
			return s.toString();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * 读取文件的内容
	 * 
	 * @param file 需要读取的文件
	 * @return 文件的内容
	 * @throws IOException 文件不存在，或者读取错误时抛出该异常
	 */
	public static String readFile(String file) {
		return readFile(new File(file));
	}
}
