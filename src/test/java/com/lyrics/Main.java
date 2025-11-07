package com.lyrics;

import com.lyrics.utils.FolderSelector;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    // 【可自定义修改】默认保存目录（桌面/code文件夹）
    private static  String DEFAULT_SAVE_DIR = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "code";

    // 命名转换工具类
    static class NameConverter {
        // 驼峰转下划线（用于接口路径）
        public static String camelToUnderline(String camelName) {
            if (camelName == null || camelName.isEmpty()) {
                return "";
            }
            return camelName.replaceAll("(?<!^)(?=[A-Z])", "_").toLowerCase();
        }

        // 首字母小写（用于变量名和@Service注解值）
        public static String firstLetterToLower(String className) {
            if (className == null || className.isEmpty()) {
                return "";
            }
            return className.substring(0, 1).toLowerCase() + className.substring(1);
        }

        // 帕斯卡命名（类名规范）
        public static String toPascalCase(String camelName) {
            if (camelName == null || camelName.isEmpty()) {
                return "";
            }
            return camelName.substring(0, 1).toUpperCase() + camelName.substring(1);
        }
    }

    // 1. 生成Controller代码
    public static String generateControllerCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String underlinePath = NameConverter.camelToUnderline(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceVarName = NameConverter.firstLetterToLower(serviceClassName);
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";
        String addUpdateReqClassName = "AddAndUpdate" + pascalBaseName + "Req";

        return "@RestController\n" +
                "@RequestMapping(\"/" + underlinePath + "\")\n" +
                "public class " + pascalBaseName + "Controller {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private " + serviceClassName + " " + serviceVarName + ";\n" +
                "\n" +
                "    @PostMapping(\"/select\")\n" +
                "    public ResponseResult<IPage<" + poClassName + ">> select(@RequestBody(required = false) " + reqClassName + " filter) {\n" +
                "        if (filter == null) {\n" +
                "            filter = new " + reqClassName + "();\n" +
                "        }\n" +
                "        return ResponseResult.success(\"成功\", " + serviceVarName + ".select(filter));\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/batchDelete\")\n" +
                "    public ResponseResult batchDelete(@RequestBody @Valid IdsRequestDTO idsRequestDTO){\n" +
                "        " + serviceVarName + ".batchDelete(idsRequestDTO);\n" +
                "        return ResponseResult.success(\"成功\");\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/selectOne\")\n" +
                "    public ResponseResult<" + poClassName + "> selectOne(@RequestBody(required = false) IdsRequestDTO filter) {\n" +
                "        if (filter == null) {\n" +
                "            filter = new IdsRequestDTO();\n" +
                "        }\n" +
                "        return ResponseResult.success(\"成功\", " + serviceVarName + ".selectOne(filter));\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/addOrUpdate\")\n" +
                "    public ResponseResult<" + addUpdateReqClassName + "> addOrUpdate(@RequestBody " + addUpdateReqClassName + " req){\n" +
                "        return ResponseResult.success(\"成功\", " + serviceVarName + ".addOrUpdate(req));\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    // 2. 生成Req类代码
    public static String generateReqCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String reqClassName = pascalBaseName + "Req";

        return "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + reqClassName + " extends CustomizedPage {\n" +
                "    // 可在此添加查询参数字段\n" +
                "}\n";
    }

    // 3. 生成PO类代码
    public static String generatePoCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String poClassName = pascalBaseName + "Po";
        String tableName = NameConverter.camelToUnderline(baseName);

        return "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@TableName(\"" + tableName + "\")\n" +
                "public class " + poClassName + " extends BasicBusinessAndPlatFormEntity {\n" +
                "    // 可在此添加表字段映射\n" +
                "}\n";
    }

    // 4. 生成AddAndUpdateReq类代码
    public static String generateAddUpdateReqCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String addUpdateReqClassName = "AddAndUpdate" + pascalBaseName + "Req";

        return "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + addUpdateReqClassName + " {\n" +
                "    private String id;\n" +
                "    // 可在此添加其他新增/修改字段\n" +
                "}\n";
    }

    // 5. 生成Service类代码
    public static String generateServiceCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceBeanName = NameConverter.firstLetterToLower(serviceClassName);
        String mapperClassName = pascalBaseName + "Mapper";
        String mapperVarName = NameConverter.firstLetterToLower(mapperClassName);
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";
        String addUpdateReqClassName = "AddAndUpdate" + pascalBaseName + "Req";

        return "@Service(\"" + serviceBeanName + "\")\n" +
                "public class " + serviceClassName + " extends ServiceImpl<" + mapperClassName + ", " + poClassName + "> {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private " + mapperClassName + " " + mapperVarName + ";\n" +
                "   \n" +
                "    public IPage<" + poClassName + "> select(" + reqClassName + " filter) {\n" +
                "        return getBaseMapper().search(filter);\n" +
                "    }\n" +
                "\n" +
                "    public " + poClassName + " selectOne(IdsRequestDTO filter) {\n"+
                "        "+poClassName + " po = new " + poClassName + "();\n"+
                "        po.setId(Long.parseLong(filter.getId()));\n"+
                "        return getBaseMapper().getById(po);\n"+
                "    }\n"+
                "\n"+
                "    @Transactional\n" +
                "    public " + addUpdateReqClassName + " addOrUpdate(" + addUpdateReqClassName + " req) {\n" +
                "        if(Utils4General.isEmpty(req)){\n" +
                "            throw new BadRequestException(\"空对象\");\n" +
                "        }\n" +
                "        reqId = req.getId();\n"+
                "        " + poClassName + " po = BeanUtil.copyProperties(req, " + poClassName + ".class, \"id\");\n" +
                "        if (reqId.equals(\"undefined\") || Utils4General.isEmpty(reqId) || reqId.equals(\"\")) {\n" +
                "            po.fillSourceInfo();\n" +
                "            po.fillCreationInfo();\n" +
                "\n" +
                "            List<" + poClassName + "> pos = baseMapper.exist(po);\n" +
                "            if(Utils4General.isNotEmpty(pos)){\n" +
                "                throw new BadRequestException(\"已存在\");\n" +
                "            }\n" +
                "\n" +
                "        }else {\n" +
                "            po.setId(Long.valueOf(reqId));\n" +
                "            po.fillUpdateInfo();\n" +
                "        }\n" +
                "        saveOrUpdate(po);\n" +
                "        return req;\n" +
                "    }\n" +
                "}";
    }
    
    public static String generateMapperCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String mapperClassName = pascalBaseName + "Mapper";
        String poClassName = pascalBaseName + "Po";

        return "@Repository\n" +
                "public interface " + mapperClassName + " extends BaseMapper<" + poClassName + "> {\n" +
                "\n" +
                "    List<" + poClassName + "> exist(" + poClassName + " po);\n" +
                "}";
    }

    // 7. 生成Mapper XML文件代码
    public static String generateMapperXmlCode(String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String mapperClassName = pascalBaseName + "Mapper";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<!-- 注意：请手动替换namespace为实际包名！格式示例：com.example.mapper." + mapperClassName + " -->\n" +
                "<mapper namespace=\"需要替换的实际包名." + mapperClassName + "\">\n" +
                "\n" +
                "</mapper>";
    }

    public static String generateIdsRequestDTOCode() {
        return "public class IdsRequestDTO {\n" +
                "\n" +
                "    private List<String> ids;\n" +
                "\n" +
                "    public String getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public void setId(String id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "\n" +
                "    private String id;\n" +
                "\n" +
                "    public List<String> getIds() {\n" +
                "        return ids;\n" +
                "    }\n" +
                "\n" +
                "    public void setIds(List<String> ids) {\n" +
                "        this.ids = ids;\n" +
                "    }\n" +
                "}";
    }

    public static void saveCodeToFile(String code, String fileName) throws IOException {
        File saveDir = new File(DEFAULT_SAVE_DIR);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File targetFile = new File(saveDir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            writer.write(code);
        }
        System.out.println("已生成：" + targetFile.getAbsolutePath());
    }

    public static String selectFolder(){
        // 设置 Swing 外观为系统默认
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FolderSelector.selectFolder("选择输出文件夹", DEFAULT_SAVE_DIR);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String selectPath = selectFolder();
        if(selectPath!=null) DEFAULT_SAVE_DIR = selectPath;
        System.out.println("所有文件将保存到：" + DEFAULT_SAVE_DIR);
        System.out.print("请输入基础名称（驼峰式）：");
        String baseName = scanner.nextLine().trim();

        if (baseName.isEmpty() || !baseName.matches("[A-Za-z][A-Za-z0-9]*")) {
            System.err.println("错误：基础名称只能包含字母和数字，且不能以数字开头！");
            scanner.close();
            return;
        }

        try {
            String pascalBaseName = NameConverter.toPascalCase(baseName);
            String mapperClassName = pascalBaseName + "Mapper";

            // 生成8类文件
            // 1. Controller
            String controllerCode = generateControllerCode(baseName);
            saveCodeToFile(controllerCode, pascalBaseName + "Controller.java");

            // 2. Req
            String reqCode = generateReqCode(baseName);
            saveCodeToFile(reqCode, pascalBaseName + "Req.java");

            // 3. PO
            String poCode = generatePoCode(baseName);
            saveCodeToFile(poCode, pascalBaseName + "Po.java");

            // 4. AddAndUpdateReq
            String addUpdateReqCode = generateAddUpdateReqCode(baseName);
            saveCodeToFile(addUpdateReqCode, "AddAndUpdate" + pascalBaseName + "Req.java");

            // 5. Service
            String serviceCode = generateServiceCode(baseName);
            saveCodeToFile(serviceCode, pascalBaseName + "Service.java");

            // 6. Mapper接口
            String mapperCode = generateMapperCode(baseName);
            saveCodeToFile(mapperCode, mapperClassName + ".java");

            // 7. Mapper XML
            String mapperXmlCode = generateMapperXmlCode(baseName);
            saveCodeToFile(mapperXmlCode, mapperClassName + ".xml");

            // 8. IdsRequestDTO（固定名称，新增）
            String idsRequestDTOCode = generateIdsRequestDTOCode();
            saveCodeToFile(idsRequestDTOCode, "IdsRequestDTO.java");

            System.out.println("=== 所有代码生成完成！注意：Mapper XML的namespace需要手动替换为实际包名 ===");
        } catch (Exception e) {
            System.err.println("代码生成失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}