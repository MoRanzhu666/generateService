package com.lyrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MainService {

    // 【可自定义修改】默认保存根目录
    private static final String DEFAULT_ROOT_SAVE_DIR = createSaveRootDir();
    private static final String DEFAULT_ROOT_PACKAGE = "com.circlelog.tos";
    private static final String DEFAULT_MODEL_NAME = "base";

    private static String createSaveRootDir() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("D:\\Download\\%04d%02d%02d_%02d%02d%02d_generated_code",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
    }

    // 命名转换工具类
    static class NameConverter {
        // 驼峰转下划线（用于接口路径和表名）
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
    public static String generateControllerCode(String controllerPackage, String baseName,
                                                String servicePackage, String poPackage, String reqPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceVarName = NameConverter.firstLetterToLower(serviceClassName);
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";
        String addUpdateReqSimpleClassName = pascalBaseName + "AddOrUpdateReq";

        return "package " + controllerPackage + ";\n\n" +
                "import " + poPackage + "." + poClassName + ";\n" +
                "import " + reqPackage + "." + reqClassName + ";\n" +
                "import " + reqPackage + "." + addUpdateReqSimpleClassName + ";\n" +
                "import " + servicePackage + "." + serviceClassName + ";\n" +
                "import com.baomidou.mybatisplus.core.metadata.IPage;\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.PostMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "import javax.validation.Valid;\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(\"/api/v1/" + NameConverter.camelToUnderline(baseName) + "\")\n" +
                "public class " + pascalBaseName + "Controller {\n" +
                "\n" +
                "    private static final Logger logger = LoggerFactory.getLogger(" + pascalBaseName + "Controller.class);\n" +
                "\n" +
                "    @Autowired\n" +
                "    private " + serviceClassName + " " + serviceVarName + ";\n" +
                "\n" +
                "    @PostMapping(\"/search\")\n" +
                "    public IPage<" + poClassName + "> search(@RequestBody(required = false) " + reqClassName + " filter) {\n" +
                "        logger.info(\"进入" + pascalBaseName + "Controller.search方法，filter参数: {}\", filter);\n" +
                "        if (filter == null) {\n" +
                "            filter = new " + reqClassName + "();\n" +
                "        }\n" +
                "        return " + serviceVarName + ".search(filter);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/batchDelete\")\n" +
                "    public void batchDelete(@RequestBody @Valid " + reqClassName + " req) {\n" +
                "        logger.info(\"进入" + pascalBaseName + "Controller.batchDelete方法，req参数: {}\", req);\n" +
                "        " + serviceVarName + ".batchDelete(req);\n" +
                "        logger.info(\"批量删除操作完成\");\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/searchById\")\n" +
                "    public " + poClassName + " searchById(@RequestBody(required = false) " + reqClassName + " filter) {\n" +
                "        logger.info(\"进入" + pascalBaseName + "Controller.searchById方法，filter参数: {}\", filter);\n" +
                "        if (filter == null) {\n" +
                "            filter = new " + reqClassName + "();\n" +
                "        }\n" +
                "        return " + serviceVarName + ".searchById(filter);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/addOrUpdate\")\n" +
                "    public " + addUpdateReqSimpleClassName + " addOrUpdate(@RequestBody " + addUpdateReqSimpleClassName + " req) {\n" +
                "        logger.info(\"进入" + pascalBaseName + "Controller.addOrUpdate方法，req参数: {}\", req);\n" +
                "        return " + serviceVarName + ".addOrUpdate(req);\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    // 2. 生成Req类代码
    public static String generateReqCode(String reqPackage, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String reqClassName = pascalBaseName + "Req";

        return "package " + reqPackage + ";\n\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + reqClassName + " extends Page {\n" +
                "\n" +
                "    private static final Logger logger = LoggerFactory.getLogger(" + reqClassName + ".class);\n" +
                "\n" +
                "    private List<String> ids;\n" +
                "    private String id;\n" +
                "    \n" +
                "    // 可在此添加查询参数字段\n" +
                "}\n";
    }

    // 3. 生成PO类代码
    public static String generatePoCode(String poPackage, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String poClassName = pascalBaseName + "Po";
        String tableName = NameConverter.camelToUnderline(baseName);

        return "package " + poPackage + ";\n\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import com.baomidou.mybatisplus.annotation.TableName;\n" +
                "import com.baomidou.mybatisplus.annotation.TableField;\n" +
                "import com.baomidou.mybatisplus.annotation.TableId;\n" +
                "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.time.LocalDateTime;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@TableName(\"" + tableName + "\")\n" +
                "public class " + poClassName + " implements Serializable {\n" +
                "\n" +
                "    private static final Logger logger = LoggerFactory.getLogger(" + poClassName + ".class);\n" +
                "\n" +
                "    @TableId\n" +
                "    private String id;\n" +
                "    \n" +
                "    @TableField(value = \"create_time\")\n" +
                "    private LocalDateTime createTime;\n" +
                "    \n" +
                "    @TableField(value = \"update_time\")\n" +
                "    private LocalDateTime updateTime;\n" +
                "    \n" +
                "    @TableField(value = \"create_user\")\n" +
                "    private String createUser;\n" +
                "    \n" +
                "    @TableField(value = \"update_user\")\n" +
                "    private String updateUser;\n" +
                "    \n" +
                "    @TableField(value = \"delete_flag\")\n" +
                "    private Boolean deleteFlag;\n" +
                "    \n" +
                "    // 可在此添加表字段映射\n" +
                "}\n";
    }

    // 4. 生成AddAndUpdateReq类代码
    public static String generateAddUpdateReqCode(String reqPackage, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String addUpdateReqClassName = pascalBaseName + "AddOrUpdateReq";

        return "package " + reqPackage + ";\n\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + addUpdateReqClassName + " {\n" +
                "\n" +
                "    private static final Logger logger = LoggerFactory.getLogger(" + addUpdateReqClassName + ".class);\n" +
                "\n" +
                "    private String id;\n" +
                "    // 可在此添加其他新增/修改字段\n" +
                "}\n";
    }

    // 5. 生成Service类代码
    public static String generateServiceCode(String servicePackage, String baseName,
                                             String mapperPackage, String poPackage, String reqPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceBeanName = NameConverter.firstLetterToLower(serviceClassName);
        String mapperClassName = pascalBaseName + "Mapper";
        String mapperVarName = NameConverter.firstLetterToLower(mapperClassName);
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";
        String addUpdateReqSimpleClassName = pascalBaseName + "AddOrUpdateReq";

        StringBuilder serviceCodeBuilder = new StringBuilder();
        serviceCodeBuilder.append("package ").append(servicePackage).append(";\n\n")
                .append("import ").append(poPackage).append(".").append(poClassName).append(";\n")
                .append("import ").append(reqPackage).append(".").append(reqClassName).append(";\n")
                .append("import ").append(reqPackage).append(".").append(addUpdateReqSimpleClassName).append(";\n")
                .append("import ").append(mapperPackage).append(".").append(mapperClassName).append(";\n")
                .append("import com.baomidou.mybatisplus.core.metadata.IPage;\n")
                .append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n")
                .append("import org.slf4j.Logger;\n")
                .append("import org.slf4j.LoggerFactory;\n")
                .append("import org.springframework.beans.factory.annotation.Autowired;\n")
                .append("import org.springframework.stereotype.Service;\n")
                .append("import org.springframework.transaction.annotation.Transactional;\n")
                .append("import org.springframework.util.CollectionUtils;\n")
                .append("import org.springframework.util.StringUtils;\n")
                .append("\n")
                .append("import java.time.LocalDateTime;\n")
                .append("import java.util.List;\n")
                .append("\n")
                .append("@Service(\"").append(serviceBeanName).append("\")\n")
                .append("public class ").append(serviceClassName).append(" extends ServiceImpl<").append(mapperClassName).append(", ").append(poClassName).append("> {\n")
                .append("\n")
                .append("    private static final Logger logger = LoggerFactory.getLogger(").append(serviceClassName).append(".class);\n")
                .append("\n")
                .append("    @Autowired\n")
                .append("    private ").append(mapperClassName).append(" ").append(mapperVarName).append(";\n")
                .append("   \n")
                .append("    public IPage<").append(poClassName).append("> search(").append(reqClassName).append(" filter) {\n")
                .append("        logger.info(\"进入").append(serviceClassName).append(".search方法，filter参数: {}\", filter);\n")
                .append("        return getBaseMapper().search(filter);\n")
                .append("    }\n")
                .append("\n")
                .append("    public ").append(poClassName).append(" searchById(").append(reqClassName).append(" filter) {\n")
                .append("        logger.info(\"进入").append(serviceClassName).append(".searchById方法，filter参数: {}\", filter);\n")
                .append("        if (filter == null || !StringUtils.hasText(filter.getId())) {\n")
                .append("            return null;\n")
                .append("        }\n")
                .append("        return getById(filter.getId());\n")
                .append("    }\n")
                .append("\n")
                .append("    @Transactional(rollbackFor = Exception.class)\n")
                .append("    public void batchDelete(").append(reqClassName).append(" req) {\n")
                .append("        logger.info(\"进入").append(serviceClassName).append(".batchDelete方法，req参数: {}\", req);\n")
                .append("        if (req == null || CollectionUtils.isEmpty(req.getIds())) {\n")
                .append("            logger.warn(\"批量删除参数为空，跳过处理\");\n")
                .append("            return;\n")
                .append("        }\n")
                .append("        List<").append(poClassName).append("> entityList = this.lambdaQuery()\n")
                .append("                .in(").append(poClassName).append("::getId, req.getIds())\n")
                .append("                .list();\n")
                .append("        logger.info(\"查询到{}条待删除记录\", entityList.size());\n")
                .append("\n")
                .append("        int skippedCount = 0;\n")
                .append("        for (").append(poClassName).append(" entity : entityList) {\n")
                .append("            if (handleBatchDeleteCheck(entity)) {\n")
                .append("                skippedCount++;\n")
                .append("                continue;\n")
                .append("            }\n")
                .append("            entity.setUpdateTime(LocalDateTime.now());\n")
                .append("            // TODO: 设置当前用户 entity.setUpdateUser(currentUser);\n")
                .append("            entity.setDeleteFlag(true);\n")
                .append("        }\n")
                .append("        updateBatchById(entityList);\n")
                .append("        logger.info(\"批量删除完成，共处理{}条记录，跳过{}条\", entityList.size() - skippedCount, skippedCount);\n")
                .append("    }\n")
                .append("\n")
                .append("    protected boolean handleBatchDeleteCheck(").append(poClassName).append(" entity) {\n")
                .append("        // 默认实现不阻止任何删除\n")
                .append("        // TODO: Implement specific business logic here if needed\n")
                .append("        return false;\n")
                .append("    }\n")
                .append("\n")
                .append("    @Transactional\n")
                .append("    public ").append(addUpdateReqSimpleClassName).append(" addOrUpdate(").append(addUpdateReqSimpleClassName).append(" req) {\n")
                .append("        logger.info(\"进入").append(serviceClassName).append(".addOrUpdate方法，req参数: {}\", req);\n")
                .append("        if (req == null) {\n")
                .append("            logger.error(\"参数校验失败，req为空\");\n")
                .append("            throw new RuntimeException(\"参数校验失败\");\n")
                .append("        }\n")
                .append("        ").append(poClassName).append(" po = new ").append(poClassName).append("();\n")
                .append("        // TODO: 使用BeanUtils.copyProperties(req, po) 复制属性\n")
                .append("        po.setId(req.getId());\n")
                .append("        \n")
                .append("        if (!StringUtils.hasText(req.getId())) {\n")
                .append("            logger.info(\"执行新增操作\");\n")
                .append("            po.setCreateTime(LocalDateTime.now());\n")
                .append("            // TODO: 设置创建用户 po.setCreateUser(currentUser);\n")
                .append("            po.setDeleteFlag(false);\n")
                .append("        } else {\n")
                .append("            logger.info(\"执行更新操作，ID: {}\", req.getId());\n")
                .append("            po.setUpdateTime(LocalDateTime.now());\n")
                .append("            // TODO: 设置更新用户 po.setUpdateUser(currentUser);\n")
                .append("        }\n")
                .append("        saveOrUpdate(po);\n")
                .append("        logger.info(\"保存操作完成\");\n")
                .append("        return req;\n")
                .append("    }\n")
                .append("}");

        return serviceCodeBuilder.toString();
    }

    // 6. 生成Mapper接口代码
    public static String generateMapperCode(String mapperPackage, String baseName, String poPackage, String reqPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String mapperClassName = pascalBaseName + "Mapper";
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";

        return "package " + mapperPackage + ";\n\n" +
                "import " + poPackage + "." + poClassName + ";\n" +
                "import " + reqPackage + "." + reqClassName + ";\n" +
                "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
                "import com.baomidou.mybatisplus.core.metadata.IPage;\n" +
                "import org.apache.ibatis.annotations.Mapper;\n" +
                "import org.apache.ibatis.annotations.Param;\n" +
                "\n" +
                "@Mapper\n" +
                "public interface " + mapperClassName + " extends BaseMapper<" + poClassName + "> {\n" +
                "\n" +
                "    IPage<" + poClassName + "> search(@Param(\"filter\") " + reqClassName + " filter);\n" +
                "}";
    }

    // 7. 生成Mapper XML文件代码
    public static String generateMapperXmlCode(String mapperPackage, String baseName, String poPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String underlineTableName = NameConverter.camelToUnderline(baseName);
        String mapperClassName = pascalBaseName + "Mapper";
        String poClassName = pascalBaseName + "Po";

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"" + mapperPackage + "." + mapperClassName + "\">\n" +
                "\n" +
                "    <select id=\"search\" resultType=\"" + poPackage + "." + poClassName + "\">\n" +
                "        SELECT * FROM " + underlineTableName + " \n" +
                "        WHERE delete_flag = 0\n" +
                "        ORDER BY update_time DESC\n" +
                "    </select>\n" +
                "\n" +
                "</mapper>";
    }

    // 工具方法：保存文件
    public static void saveCodeToFile(String code, String rootSaveDir, String packageName, String fileName) throws IOException {
        String packagePath = packageName.replace('.', File.separatorChar);
        File fullDir = new File(rootSaveDir, packagePath);
        if (!fullDir.exists()) {
            fullDir.mkdirs();
        }
        File targetFile = new File(fullDir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            writer.write(code);
        }
        System.out.println("已生成：" + targetFile.getAbsolutePath());
    }

    /**
     * 将小写下划线格式转换为小驼峰格式
     */
    private static String convertSnakeToCamel(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;

        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    // 主函数
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("所有文件将保存到：" + DEFAULT_ROOT_SAVE_DIR);

        System.out.print("请输入根包名 (例如 com.circlelog.tos，默认：" + DEFAULT_ROOT_PACKAGE + "): ");
        String rootPackage = scanner.nextLine().trim();
        if (rootPackage.isEmpty()) {
            rootPackage = DEFAULT_ROOT_PACKAGE;
        }
        rootPackage = "src.main.java." +rootPackage;
        System.out.println("根包名：" + rootPackage);

        System.out.print("请输入模块名 (例如 base, order, warehouse，默认：" + DEFAULT_MODEL_NAME + "): ");
        String moduleName = scanner.nextLine().trim();
        if (moduleName.isEmpty()) {
            moduleName = DEFAULT_MODEL_NAME;
        }
        System.out.println("模块名：" + moduleName);

        System.out.print("请输入具体实体名（驼峰式/小写下划线）：");
        String baseName = scanner.nextLine().trim();

        if (baseName.contains("_") && baseName.equals(baseName.toLowerCase())) {
            baseName = convertSnakeToCamel(baseName);
        }

        if (baseName.isEmpty() || !baseName.matches("[A-Za-z][A-Za-z0-9]*")) {
            System.err.println("错误：基础名称只能包含字母和数字，且不能以数字开头！");
            scanner.close();
            return;
        }
        System.out.println("基础名称：" + baseName);

        // 根据输入计算各层包名（新结构）
        String controllerPackage = rootPackage + ".controller." + moduleName ;
        String servicePackage = rootPackage + ".service." + moduleName;
        String mapperPackage = rootPackage + ".mapper." + moduleName;
        String poPackage = rootPackage + ".model.po." + moduleName;
        String reqPackage = rootPackage + ".model.req." + moduleName ;

        try {
            String pascalBaseName = NameConverter.toPascalCase(baseName);
            String mapperClassName = pascalBaseName + "Mapper";
            String addUpdateReqClassName = pascalBaseName + "AddOrUpdateReq";

            // 生成代码并保存
            // 1. Controller
            String controllerCode = generateControllerCode(controllerPackage, baseName, servicePackage, poPackage, reqPackage);
            saveCodeToFile(controllerCode, DEFAULT_ROOT_SAVE_DIR, controllerPackage, pascalBaseName + "Controller.java");

            // 2. Req
            String reqCode = generateReqCode(reqPackage, baseName);
            saveCodeToFile(reqCode, DEFAULT_ROOT_SAVE_DIR, reqPackage, pascalBaseName + "Req.java");

            // 3. PO
            String poCode = generatePoCode(poPackage, baseName);
            saveCodeToFile(poCode, DEFAULT_ROOT_SAVE_DIR, poPackage, pascalBaseName + "Po.java");

            // 4. AddAndUpdateReq
            String addUpdateReqCode = generateAddUpdateReqCode(reqPackage, baseName);
            saveCodeToFile(addUpdateReqCode, DEFAULT_ROOT_SAVE_DIR, reqPackage, addUpdateReqClassName + ".java");

            // 5. Service
            String serviceCode = generateServiceCode(servicePackage, baseName, mapperPackage, poPackage, reqPackage);
            saveCodeToFile(serviceCode, DEFAULT_ROOT_SAVE_DIR, servicePackage, pascalBaseName + "Service.java");

            // 6. Mapper接口
            String mapperCode = generateMapperCode(mapperPackage, baseName, poPackage, reqPackage);
            saveCodeToFile(mapperCode, DEFAULT_ROOT_SAVE_DIR, mapperPackage, mapperClassName + ".java");

            // 7. Mapper XML
            String mapperXmlCode = generateMapperXmlCode(mapperPackage, baseName, poPackage);
            // 在TOS项目中，XML通常放在resources/mapper目录下
            File xmlSaveDir = new File(DEFAULT_ROOT_SAVE_DIR, "src/main/resources/mapper/" + moduleName + "/" + baseName);
            if (!xmlSaveDir.exists()) {
                xmlSaveDir.mkdirs();
            }
            File xmlTargetFile = new File(xmlSaveDir, mapperClassName + ".xml");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlTargetFile))) {
                writer.write(mapperXmlCode);
            }
            System.out.println("已生成 XML：" + xmlTargetFile.getAbsolutePath());

            System.out.println("\n=== 所有代码生成完成！ ===");
            System.out.println("1. 每个类已自动添加日志属性：private static final Logger logger = LoggerFactory.getLogger(XXX.class);");
            System.out.println("2. 在关键方法中添加了日志记录语句");
            System.out.println("3. 移除了IdsRequestDTO，统一使用" + pascalBaseName + "Req作为请求对象");
            System.out.println("4. 生成的目录结构位于: " + DEFAULT_ROOT_SAVE_DIR);
            System.out.println("5. 包结构：" + rootPackage + ".{controller|service|mapper|model}/{模块名}/{实体名}/");

        } catch (Exception e) {
            System.err.println("代码生成失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}