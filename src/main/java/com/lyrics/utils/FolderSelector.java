package com.lyrics.utils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FolderSelector {
    
    /**
     * 打开文件夹选择对话框
     * @return 选择的文件夹路径，如果取消选择则返回 null
     */
    public static String selectFolder() {
        return selectFolder("请选择文件夹");
    }
    
    /**
     * 打开文件夹选择对话框
     * @param dialogTitle 对话框标题
     * @return 选择的文件夹路径，如果取消选择则返回 null
     */
    public static String selectFolder(String dialogTitle) {
        return selectFolder(dialogTitle, null);
    }
    
    /**
     * 打开文件夹选择对话框
     * @param dialogTitle 对话框标题
     * @param initialDirectory 初始目录
     * @return 选择的文件夹路径，如果取消选择则返回 null
     */
    public static String selectFolder(String dialogTitle, String initialDirectory) {
        JFileChooser fileChooser = new JFileChooser();
        
        // 设置对话框标题
        fileChooser.setDialogTitle(dialogTitle);
        
        // 设置初始目录
        if (initialDirectory != null && !initialDirectory.trim().isEmpty()) {
            fileChooser.setCurrentDirectory(new File(initialDirectory));
        }
        
        // 设置为只选择目录
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // 禁用"所有文件"选项
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        // 显示对话框
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        // 设置 Swing 外观为系统默认
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 选择文件夹
        String selectedPath = selectFolder("请选择一个文件夹", "C:\\");
        
        if (selectedPath != null) {
            System.out.println("选择的文件夹路径: " + selectedPath);
            
            // 这里可以添加处理选中文件夹的逻辑
            JOptionPane.showMessageDialog(null, 
                "已选择文件夹: " + selectedPath, 
                "选择结果", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("用户取消了选择");
            JOptionPane.showMessageDialog(null, 
                "已取消选择文件夹", 
                "提示", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
}