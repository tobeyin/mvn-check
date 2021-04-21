package com.buss;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.buss.mvn.check.CheckMvn;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class MvnCheckFrame extends JFrame {

	private static final long serialVersionUID = -7802818852803546674L;

	private static final Object[] HEADER = new Object[] { "groupId", "artifactId", "version" };

	public MvnCheckFrame() {
		setTitle("mvn仓库检查");
		setLayout(null);
		setResizable(false);
		setBounds(400, 200, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton scanBtn = new JButton("浏览...");
		scanBtn.setBounds(10, 10, 80, 30);
		add(scanBtn);

		final JTextField jtf = new JTextField();
		jtf.setEditable(false);
		jtf.setBounds(100, 10, 270, 30);
		add(jtf);

		JButton copyBtn = new JButton("复制到剪切板");
		copyBtn.setBounds(10, 45, 120, 30);
		add(copyBtn);

		JButton exportBtn = new JButton("导出");
		exportBtn.setBounds(135, 45, 60, 30);
		add(exportBtn);

		JButton deletBtn = new JButton("删除缺失目录");
		deletBtn.setBounds(200, 45, 120, 30);
		add(deletBtn);

		final DefaultTableModel dtm = new DefaultTableModel(new Object[][] {}, HEADER);
		JTable table = new JTable(dtm);

		JScrollPane sPane = new JScrollPane(table);
		sPane.setBounds(10, 85, 360, 260);
		add(sPane);

		scanBtn.addActionListener(e -> {
			JFileChooser fc = new JFileChooser();
			fc.setMultiSelectionEnabled(false);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(this);
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			fc.setVisible(true);

			File file = fc.getSelectedFile();
			jtf.setText(file.getAbsolutePath());
			dtm.setRowCount(0);

			CheckMvn.checkDirs(file, f -> {
				String version = f.getName();
				String artifactId = f.getParentFile().getName();
				String groupId = f.getParentFile().getParentFile().getAbsolutePath()
						.replace(fc.getSelectedFile().getAbsolutePath(), "");
				if (groupId.startsWith("\\")) {
					groupId = groupId.substring(1);
				}
				groupId = groupId.replace("\\", ".");
				dtm.addRow(new Object[] { groupId, artifactId, version });
			});
		});

		copyBtn.addActionListener(l -> copyTable(table));
		exportBtn.addActionListener(l -> exoprtExcel(table));
		deletBtn.addActionListener(l -> cleanErrorDirs(table, jtf));

		setVisible(true);
	}

	private void cleanErrorDirs(JTable table, JTextField jtf) {
		String base = jtf.getText() + "\\";
		Object[][] datas = getData(table);
		for (int i = 1; i < datas.length; i++) {
			String dir = base + String.valueOf(datas[i][0]).replaceAll("\\.", "\\\\");
			dir += "\\" + String.valueOf(datas[i][1]);
			dir += "\\" + String.valueOf(datas[i][2]);
			FileUtil.delete(dir);
		}
		JOptionPane.showMessageDialog(this, "删除成功！");
	}

	private void exoprtExcel(JTable table) {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".xls");
			}
		});
		fc.showOpenDialog(this);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setVisible(true);

		File file = fc.getSelectedFile();

		try {
			WritableWorkbook book = Workbook.createWorkbook(file);
			WritableSheet sh = book.createSheet("mvn", 0);

			Object[][] datas = getData(table);
			for (int i = 0; i < datas.length; i++) {
				Object[] data = datas[i];
				for (int j = 0; j < data.length; j++) {
					sh.addCell(new Label(j, i, String.valueOf(data[j])));
				}
			}

			book.write();
			book.close();
			JOptionPane.showMessageDialog(this, "导出成功！");
		} catch (IOException | WriteException e) {
			JOptionPane.showMessageDialog(this, "导出失败！");
			e.printStackTrace();
		}
	}

	private void copyTable(JTable table) {
		StringBuilder s = new StringBuilder();
		Object[][] datas = getData(table);
		for (Object[] objects : datas) {
			for (Object object : objects) {
				s.append(object).append("\t");
			}
			s.append("\r\n");
		}
		// 获取系统剪贴板
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 封装文本内容
		Transferable trans = new StringSelection(s.toString());
		// 把文本内容设置到系统剪贴板
		clipboard.setContents(trans, null);
		JOptionPane.showMessageDialog(this, "已复制到剪切板！");
	}

	private Object[][] getData(JTable table) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Vector<?> data = model.getDataVector();
		Object[][] objs = new Object[data.size() + 1][];
		Object[] header = new Object[model.getColumnCount()];
		for (int i = 0; i < model.getColumnCount(); i++) {
			header[i] = model.getColumnName(i);
		}
		objs[0] = header;

		for (int i = 0; i < data.size(); i++) {
			@SuppressWarnings("unchecked")
			Vector<Object> v = (Vector<Object>) data.get(i);
			Object[] obj = new Object[v.size()];
			v.toArray(obj);
			objs[i + 1] = obj;
		}
		return objs;
	}

	public static void main(String[] args) {
		new MvnCheckFrame();
	}

	public static class FileUtil {

		/**
		 * 处理文件
		 * 
		 * @param file    根目录（文件）
		 * @param filter  过滤器
		 * @param handler 处理方法
		 */
		public static void handFile(File file, java.io.FileFilter filter, Consumer<File> handler) {
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
	}
}
