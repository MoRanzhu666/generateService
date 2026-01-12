package com.lyrics; // 这是生成器本身的包名

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// import java.time.LocalDate; // Unused import removed
import java.time.LocalDateTime;
// import java.util.Date; // Unused import removed
import java.util.Scanner;

public class MainService {

    // 【可自自定义修改】默认保存根目录（例如项目src/main/java所在目录的父级）
    // private static final String DEFAULT_ROOT_SAVE_DIR = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "generated_code";
    private static final String DEFAULT_ROOT_SAVE_DIR = createSaveRootDir();

    private static final String DEFAULT_PACKAGE_NAME = "com.circlelog.cblogisticsservice";

    private static final String DEFAULT_MODEL_NAME = "warehouse";

    // 存储用户输入的IdsRequestDTO包路径，或默认生成路径
    private static String idsRequestDtoPackage = "com.circlelog.cblogisticsservice.common.dto"; // 默认值，会被用户输入覆盖

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

    // 1. 生成Controller代码 (调整引用)
    // 1. 生成Controller代码 (增加 COMMON_APIS_V1_PREFIX 前缀)
    public static String generateControllerCode(String packageName, String baseName,
                                                String servicePackage, String poPackage, String reqPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String underlinePath = NameConverter.camelToUnderline(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceVarName = NameConverter.firstLetterToLower(serviceClassName);
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";
        // --- 统一使用 XxxAddOrUpdateReq ---
        String addUpdateReqFullClassName = reqPackage + "." + pascalBaseName + "AddOrUpdateReq";
        String addUpdateReqSimpleClassName = pascalBaseName + "AddOrUpdateReq";

        return "package " + packageName + ";\n\n" +
                "import " + poPackage + "." + poClassName + ";\n" +
                "import " + reqPackage + "." + reqClassName + ";\n" +
                "import " + addUpdateReqFullClassName + "; // 导入 AddOrUpdateReq\n" +
                "import " + servicePackage + "." + serviceClassName + ";\n" +
                "import " + idsRequestDtoPackage + ".IdsRequestDTO;\n" +
                "import com.baomidou.mybatisplus.core.metadata.IPage;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.PostMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "\n" +
                "import javax.validation.Valid;\n" +
                "\n" +
                "// 导入 API 前缀常量\n" +
                "import static circlelog.jigsaw.lfs.common.model.Constant4Common.COMMON_APIS_V1_PREFIX;\n" + // 添加静态导入
                "\n" +
                "@RestController\n" +
                // 修改 RequestMapping 以包含前缀
                "@RequestMapping(COMMON_APIS_V1_PREFIX + \"/" + baseName + "\")\n" +
                "public class " + pascalBaseName + "Controller {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private " + serviceClassName + " " + serviceVarName + ";\n" +
                "\n" +
                "    @PostMapping(\"/search\")\n" +
                "    public IPage<" + poClassName + "> search(@RequestBody(required = false) " + reqClassName + " filter) {\n" +
                "        if (filter == null) {\n" +
                "            filter = new " + reqClassName + "();\n" +
                "        }\n" +
                "        return " + serviceVarName + ".search(filter);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/batchDelete\")\n" +
                "    public void batchDelete(@RequestBody @Valid IdsRequestDTO idsRequestDTO){\n" +
                "        " + serviceVarName + ".batchDelete(idsRequestDTO);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/searchById\")\n" +
                "    public " + poClassName + " searchById(@RequestBody(required = false) IdsRequestDTO filter) {\n" +
                "        if (filter == null) {\n" +
                "            filter = new IdsRequestDTO();\n" +
                "        }\n" +
                "        return  " + serviceVarName + ".searchById(filter);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(\"/addOrUpdate\")\n" +
                "    public " + addUpdateReqSimpleClassName + " addOrUpdate(@RequestBody " + addUpdateReqSimpleClassName + " req){\n" +
                "        return " + serviceVarName + ".addOrUpdate(req);\n" +
                "    }\n" +
                "\n" +
                "}";
    }


    // 2. 生成Req类代码
    public static String generateReqCode(String packageName, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String reqClassName = pascalBaseName + "Req";

        return "package " + packageName + ";\n\n" +
                "import circlelog.jigsaw.lfs.common.mybatis.CustomizedPage;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + reqClassName + " extends CustomizedPage {\n" +
                "    // 可在此添加查询参数字段\n" +
                "}\n";
    }

    // 3. 生成PO类代码
    public static String generatePoCode(String packageName, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String poClassName = pascalBaseName + "Po";
        String tableName = NameConverter.camelToUnderline(baseName);

        return "package " + packageName + ";\n\n" +
                "import circlelog.jigsaw.lfs.common.model.basic.onlyid.BasicDataIsolationOnlyPo;\n" +
                "import com.baomidou.mybatisplus.annotation.TableName;\n" +
                "import com.baomidou.mybatisplus.annotation.TableField;\n" +
                "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.math.BigDecimal;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "@TableName(\"" + tableName + "\")\n" +
                "public class " + poClassName + " extends BasicDataIsolationOnlyPo implements Serializable {\n" +
                "    // 可在此添加表字段映射\n" +
                "}\n";
    }

    // 4. 生成AddAndUpdateReq类代码 (调整类名)
    public static String generateAddUpdateReqCode(String packageName, String baseName) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        // --- 统一使用 XxxAddOrUpdateReq ---
        String addUpdateReqClassName = pascalBaseName + "AddOrUpdateReq";

        return "package " + packageName + ";\n\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class " + addUpdateReqClassName + " {\n" +
                "    private String id;\n" +
                "    // 可在此添加其他新增/修改字段\n" +
                "}\n";
    }

    // 5. 生成Service类代码 (调整引用)
// 5. 生成Service类代码 (调整引用)
// 5. 生成Service类代码 (调整引用和 batchDelete 实现)
    public static String generateServiceCode(String packageName, String baseName,
                                             String mapperPackage, String poPackage, String reqPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String serviceClassName = pascalBaseName + "Service";
        String serviceBeanName = NameConverter.firstLetterToLower(serviceClassName);
        String mapperClassName = pascalBaseName + "Mapper";
        String mapperVarName = NameConverter.firstLetterToLower(mapperClassName);
        String poClassName = pascalBaseName + "Po"; // 对应 FeeDetPo
        String reqClassName = pascalBaseName + "Req";
        // --- 统一使用 XxxAddOrUpdateReq ---
        String addUpdateReqFullClassName = reqPackage + "." + pascalBaseName + "AddOrUpdateReq";
        String addUpdateReqSimpleClassName = pascalBaseName + "AddOrUpdateReq";

        // 构建 Service 类的完整代码
        StringBuilder serviceCodeBuilder = new StringBuilder();
        serviceCodeBuilder.append("package ").append(packageName).append(";\n\n")
                .append("import ").append(poPackage).append(".").append(poClassName).append(";\n")
                .append("import ").append(reqPackage).append(".").append(reqClassName).append(";\n")
                .append("import ").append(addUpdateReqFullClassName).append("; // 导入 AddOrUpdateReq\n")
                .append("import ").append(mapperPackage).append(".").append(mapperClassName).append(";\n")
                .append("import ").append(idsRequestDtoPackage).append(".IdsRequestDTO;\n")
                .append("import circlelog.jigsaw.lfs.common.model.exception.BadRequestException;\n")
                .append("import circlelog.jigsaw.lfs.common.utils.Utils4General;\n")
                .append("import cn.hutool.core.bean.BeanUtil;\n")
                .append("import com.baomidou.mybatisplus.core.metadata.IPage;\n")
                .append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n")
                .append("import org.springframework.beans.factory.annotation.Autowired;\n")
                .append("import org.springframework.stereotype.Service;\n")
                .append("import org.springframework.transaction.annotation.Transactional;\n")
                .append("\n")
                .append("import javax.validation.Valid;\n")
                .append("import java.util.List;\n")
                .append("\n")
                .append("@Service(\"").append(serviceBeanName).append("\")\n")
                .append("public class ").append(serviceClassName).append(" extends ServiceImpl<").append(mapperClassName).append(", ").append(poClassName).append("> {\n")
                .append("\n")
                .append("    @Autowired\n")
                .append("    private ").append(mapperClassName).append(" ").append(mapperVarName).append(";\n")
                .append("   \n")
                .append("    public IPage<").append(poClassName).append("> search(").append(reqClassName).append(" filter) {\n")
                .append("        return getBaseMapper().search(filter);\n")
                .append("    }\n")
                .append("\n")
                .append("    public ").append(poClassName).append(" searchById(IdsRequestDTO filter) {\n")
                .append("        ").append(poClassName).append(" po = new ").append(poClassName).append("();\n")
                .append("        po.setId(filter.getId());\n")
                .append("        return getById(po);\n")
                .append("    }\n")
                .append("\n")
                // --- 修改后的 batchDelete 方法 ---
                .append("\t/**\n")
                .append("\t * 批量逻辑删除\n")
                .append("\t * @param idsRequestDTO 包含待删除ID列表的请求对象\n")
                .append("\t */\n")
                .append("\t@Transactional(rollbackFor = Exception.class)\n")
                .append("\tpublic void batchDelete(@Valid IdsRequestDTO idsRequestDTO) {\n")
                .append("\t\tif (idsRequestDTO == null || idsRequestDTO.getIds() == null || idsRequestDTO.getIds().isEmpty()) {\n")
                .append("\t\t\treturn; // No IDs provided, nothing to delete\n")
                .append("\t\t}\n")
                .append("\t\tList<").append(poClassName).append("> entityList = this.lambdaQuery()\n")
                .append("\t\t\t\t.in(").append(poClassName).append("::getId, idsRequestDTO.getIds()) // Assumes ID field name is 'id' in database/column\n")
                .append("\t\t\t\t.list();\n")
                .append("\n")
                .append("\t\tfor (").append(poClassName).append(" entity : entityList) {\n")
                .append("\t\t\tif (handleBatchDeleteCheck(entity)) { // Check returns true if deletion should be skipped\n")
                .append("\t\t\t\tcontinue;\n")
                .append("\t\t\t}\n")
                .append("\t\t\tentity.fillUpdateInfo(); // Assuming this method exists to set updateTime, updater etc.\n")
                .append("\t\t\tentity.setDeleteFlag(true); // Perform logical delete\n")
                .append("\t\t}\n")
                .append("\t\tupdateBatchById(entityList); // Batch update the entities with deleteFlag=true\n")
                .append("\t}\n")
                .append("\n")
                // --- handleBatchDeleteCheck 辅助方法占位符 (可选) ---
                .append("\t/**\n")
                .append("\t * 处理批量删除前的检查逻辑。\n")
                .append("\t * 子类可以重写此方法以实现特定业务规则的校验。\n")
                .append("\t * @param entity 待删除的实体对象\n")
                .append("\t * @return 如果应跳过该实体的删除，则返回 true；否则返回 false。\n")
                .append("\t */\n")
                .append("\tprotected boolean handleBatchDeleteCheck(").append(poClassName).append(" entity) {\n")
                .append("\t\t// 默认实现不阻止任何删除\n")
                .append("\t\t// TODO: Implement specific business logic here if needed\n")
                .append("\t\t// Example check (uncomment and adapt as needed):\n")
                .append("\t\t// if (someConditionOnEntity(entity)) {\n")
                .append("\t\t//     // Log warning or throw exception based on requirement\n")
                .append("\t\t//     return true; // Skip deleting this entity\n")
                .append("\t\t// }\n")
                .append("\t\treturn false; // Allow deletion by default\n")
                .append("\t}\n")
                .append("\n")
                // --- addOrUpdate 方法保持不变 ---
                .append("\t@Transactional\n")
                .append("\tpublic ").append(addUpdateReqSimpleClassName).append(" addOrUpdate(").append(addUpdateReqSimpleClassName).append(" req) {\n")
                .append("\t\tif(Utils4General.isEmpty(req)){\n")
                .append("\t\t\tthrow new BadRequestException(\"参数校验失败\");\n")
                .append("\t\t}\n")
                .append("\t\tString reqId = req.getId();\n")
                .append("\t\t").append(poClassName).append(" po = BeanUtil.copyProperties(req, ").append(poClassName).append(".class, \"id\");\n")
                .append("\t\tif (Utils4General.isEmpty(reqId) ||  reqId.equals(\"undefined\") || reqId.equals(\"\")) {\n")
                .append("\t\t\tpo.fillSourceInfo();\n")
                .append("\t\t\tpo.fillCreationInfo();\n")
                .append("\n")
                .append("\n")
                .append("\t\t}else {\n")
                .append("\t\t\tpo.setId(reqId);\n")
                .append("\t\t\tpo.fillUpdateInfo();\n")
                .append("\t\t}\n")
                .append("\t\tsaveOrUpdate(po);\n")
                .append("\t\treturn req;\n")
                .append("\t}\n")
                .append("}");

        return serviceCodeBuilder.toString();
    }


    // 6. 生成Mapper接口代码
    public static String generateMapperCode(String packageName, String baseName, String poPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String mapperClassName = pascalBaseName + "Mapper";
        String poClassName = pascalBaseName + "Po";
        String reqClassName = pascalBaseName + "Req";

        return "package " + packageName + ";\n\n" +
                "import " + poPackage + "." + poClassName + ";\n" +
                "import " + poPackage.replace(".model.po", ".model.req") + "." + reqClassName + ";\n" + // 假设req在同一模块下
                "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
                "import com.baomidou.mybatisplus.core.metadata.IPage;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Repository\n" +
                "public interface " + mapperClassName + " extends BaseMapper<" + poClassName + "> {\n" +
                "\n" +
                "    IPage<" + poClassName + "> search(" + reqClassName + " filter);\n" +
                "}";
    }

    // 7. 生成Mapper XML文件代码
    public static String generateMapperXmlCode(String packageName, String baseName, String poPackage) {
        String pascalBaseName = NameConverter.toPascalCase(baseName);
        String underlineTableName = NameConverter.camelToUnderline(baseName); // 动态生成下划线表名
        String mapperClassName = pascalBaseName + "Mapper";
        String poClassName = pascalBaseName + "Po";

        // 正确构建 namespace
        String namespace = packageName + ".mapper." + mapperClassName;

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"" + namespace + "\">\n" + // 使用动态 namespace
                "\n" +
                "    <select id=\"search\" resultType=\"" + poPackage + "." + poClassName + "\">\n" +
                "        SELECT * FROM " + underlineTableName + " WHERE delete_flag = 0\n" + // 使用动态 table_name
                "    </select>\n" +
                "\n" +
                "</mapper>";
    }

    // 8. 生成IdsRequestDTO代码 (修复字段顺序导致的方法错位)
    public static String generateIdsRequestDTOCode(String packageName) { // 添加包名参数
        return "package " + packageName + ";\n\n" + // 添加 package 声明
                "import java.util.List;\n" +
                "\n" +
                "public class IdsRequestDTO {\n" +
                "\n" +
                "    private List<String> ids;\n" +
                "    private String id; // 单个ID字段\n" +
                "\n" +
                "    // Getter and Setter for 'ids'\n" +
                "    public List<String> getIds() {\n" +
                "        return ids;\n" +
                "    }\n" +
                "\n" +
                "    public void setIds(List<String> ids) {\n" +
                "        this.ids = ids;\n" +
                "    }\n" +
                "\n" +
                "    // Getter and Setter for 'id'\n" +
                "    public String getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public void setId(String id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "}";
    }

    // 工具方法：保存文件 (支持包名创建目录)
    public static void saveCodeToFile(String code, String rootSaveDir, String packageName, String fileName) throws IOException {
        // 构建完整的目录路径
        String packagePath = packageName.replace('.', File.separatorChar); // 将 . 替换为系统分隔符
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
     * 例如：user_name -> userName
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

        System.out.print("请输入根包名 (例如 com.circlelog.cblogisticsservice): ");
        String rootPackage = scanner.nextLine().trim();
        if (rootPackage.isEmpty()) {
            rootPackage = DEFAULT_PACKAGE_NAME;
        }
        System.out.println("根包名：" + rootPackage);

        System.out.print("请输入基础模块名 (例如 user, order): ");
        String baseModule = scanner.nextLine().trim();
        if (baseModule.isEmpty()) {
            baseModule = DEFAULT_MODEL_NAME;
        }
        System.out.println("基础模块名：" + baseModule);

        System.out.print("请输入具体实体名（驼峰式/小写下划线）：");
        String baseName = scanner.nextLine().trim();

        if (baseName.contains("_") && baseName.equals(baseName.toLowerCase())) {
            // 将小写下划线转换为小驼峰
            baseName = convertSnakeToCamel(baseName);
        }

        if (baseName.isEmpty() || !baseName.matches("[A-Za-z][A-Za-z0-9]*")) {
            System.err.println("错误：基础名称只能包含字母和数字，且不能以数字开头！");
            scanner.close();
            return;
        }
        System.out.println("基础名称：" + baseName);

        // ===== 新增逻辑：处理 IdsRequestDTO 的生成选项 =====
        System.out.print("是否需要为此模块生成 IdsRequestDTO.java 文件？(y/n，默认为 y): ");
        String generateIdsDtoInput = scanner.nextLine().trim().toLowerCase();
        boolean shouldGenerateIdsDto = generateIdsDtoInput.isEmpty() || generateIdsDtoInput.equals("y") || generateIdsDtoInput.equals("yes");

        if (shouldGenerateIdsDto) {
            // 如果生成，则使用默认的公共包路径或模块内的 req 包
            // 这里我们让它生成在模块的 req 包下，你可以根据需要调整
            idsRequestDtoPackage = rootPackage + "." + baseModule + ".model.req";
            System.out.println("IdsRequestDTO 将生成在包: " + idsRequestDtoPackage);
        } else {
            System.out.print("请输入现有的 IdsRequestDTO 类的完整包路径 (例如 com.circlelog.cblogisticsservice.order.model.req): ");
            String providedPackage = scanner.nextLine().trim();
            if (providedPackage.isEmpty()) {
                providedPackage = idsRequestDtoPackage;
            }
            idsRequestDtoPackage = providedPackage;
            System.out.println("将使用外部 IdsRequestDTO，包路径为: " + idsRequestDtoPackage);
        }
        System.out.println("IdsRequestDTO 包路径：" + idsRequestDtoPackage);
        // ===================================================


        // 根据输入计算各层包名
        String baseModulePackage = rootPackage + "." + baseModule;
        String poPackage = baseModulePackage + ".model.po";
        String reqPackage = baseModulePackage + ".model.req";
        String servicePackage = baseModulePackage + ".service";
        String controllerPackage = baseModulePackage + ".controller";
        String mapperPackage = baseModulePackage + ".mapper";

        try {
            String pascalBaseName = NameConverter.toPascalCase(baseName);
            String mapperClassName = pascalBaseName + "Mapper";
            // --- 统一使用 XxxAddOrUpdateReq ---
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

            // 4. AddAndUpdateReq (现在是 XxxAddOrUpdateReq)
            String addUpdateReqCode = generateAddUpdateReqCode(reqPackage, baseName); // 放在 req 包下
            saveCodeToFile(addUpdateReqCode, DEFAULT_ROOT_SAVE_DIR, reqPackage, addUpdateReqClassName + ".java");

            // 5. Service
            String serviceCode = generateServiceCode(servicePackage, baseName, mapperPackage, poPackage, reqPackage);
            saveCodeToFile(serviceCode, DEFAULT_ROOT_SAVE_DIR, servicePackage, pascalBaseName + "Service.java");

            // 6. Mapper接口
            String mapperCode = generateMapperCode(mapperPackage, baseName, poPackage);
            saveCodeToFile(mapperCode, DEFAULT_ROOT_SAVE_DIR, mapperPackage, mapperClassName + ".java");

            // 7. Mapper XML (通常放在 resources/mapper 下，这里简化处理)
            String mapperXmlCode = generateMapperXmlCode(baseModulePackage, baseName, poPackage);
            // 注意：XML 文件通常不放在 Java 源码目录下，这里仅为演示如何生成内容。
            // 实际项目中，您可能需要指定不同的保存路径。
            File xmlSaveDir = new File(DEFAULT_ROOT_SAVE_DIR, "resources" + File.separator + "mapper" + File.separator + baseModule);
            if (!xmlSaveDir.exists()) xmlSaveDir.mkdirs();
            File xmlTargetFile = new File(xmlSaveDir, mapperClassName + ".xml");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlTargetFile))) {
                writer.write(mapperXmlCode);
            }
            System.out.println("已生成 XML：" + xmlTargetFile.getAbsolutePath());

            // 8. IdsRequestDTO（根据用户选择决定是否生成）
            if (shouldGenerateIdsDto) {
                String idsRequestDTOCode = generateIdsRequestDTOCode(idsRequestDtoPackage); // 使用确定的包路径
                saveCodeToFile(idsRequestDTOCode, DEFAULT_ROOT_SAVE_DIR, idsRequestDtoPackage, "IdsRequestDTO.java");
            }


            System.out.println("\n=== 所有代码生成完成！ ===");
            System.out.println("请注意以下几点：");
            System.out.println("1. Mapper XML的namespace需要手动替换为实际包名 (" + mapperPackage + ")。");
            System.out.println("2. 请检查各文件的import语句是否符合您的项目结构。");
            System.out.println("3. 生成的目录结构位于: " + DEFAULT_ROOT_SAVE_DIR);
            if (!shouldGenerateIdsDto) {
                System.out.println("4. 已配置使用外部 IdsRequestDTO，包路径为: " + idsRequestDtoPackage);
            }

        } catch (Exception e) {
            System.err.println("代码生成失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}