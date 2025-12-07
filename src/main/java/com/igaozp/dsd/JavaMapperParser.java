package com.igaozp.dsd;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

public class JavaMapperParser {

    private static final Set<String> BASE_MAPPER_METHODS = new HashSet<>();

    static {
        BASE_MAPPER_METHODS.add("insert");
        BASE_MAPPER_METHODS.add("deleteById");
        BASE_MAPPER_METHODS.add("deleteBatchIds");
        BASE_MAPPER_METHODS.add("deleteByMap");
        BASE_MAPPER_METHODS.add("delete");
        BASE_MAPPER_METHODS.add("updateById");
        BASE_MAPPER_METHODS.add("update");
        BASE_MAPPER_METHODS.add("selectById");
        BASE_MAPPER_METHODS.add("selectBatchIds");
        BASE_MAPPER_METHODS.add("selectByMap");
        BASE_MAPPER_METHODS.add("selectOne");
        BASE_MAPPER_METHODS.add("selectList");
        BASE_MAPPER_METHODS.add("selectMaps");
        BASE_MAPPER_METHODS.add("selectCount");
        BASE_MAPPER_METHODS.add("selectPage");
        BASE_MAPPER_METHODS.add("selectMapsPage");
        BASE_MAPPER_METHODS.add("selectObjs");
        BASE_MAPPER_METHODS.add("exists");
    }

    public MapperInfo parseMapperInterface(File javaFile) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(javaFile);

        MapperInfo mapperInfo = new MapperInfo();

        cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                .filter(ClassOrInterfaceDeclaration::isInterface)
                .forEach(interfaceDecl -> {
                    String packageName = cu.getPackageDeclaration()
                            .map(NodeWithName::getNameAsString).orElse("");
                    String fullName = packageName.isEmpty()
                            ? interfaceDecl.getNameAsString()
                            : packageName + "." + interfaceDecl.getNameAsString();

                    mapperInfo.setNamespace(fullName);
                    mapperInfo.setFilePath(javaFile.getPath());

                    // 检查是否继承 BaseMapper（MyBatis-Plus）
                    boolean extendsBaseMapper = interfaceDecl.getExtendedTypes().stream()
                            .anyMatch(t -> t.getNameAsString().contains("BaseMapper"));
                    mapperInfo.setExtendsBaseMapper(extendsBaseMapper);

                    // 解析所有方法上的 SQL 注解
                    interfaceDecl.getMethods().forEach(method -> {
                        parseMethodAnnotations(method, mapperInfo);
                    });
                });

        return mapperInfo;
    }

    private void parseMethodAnnotations(MethodDeclaration method, MapperInfo mapperInfo) {
        Set<String> sqlAnnotations = new HashSet<>();
        sqlAnnotations.add("Select");
        sqlAnnotations.add("Insert");
        sqlAnnotations.add("Update");
        sqlAnnotations.add("Delete");

        Set<String> providerAnnotations = new HashSet<>();
        providerAnnotations.add("SelectProvider");
        providerAnnotations.add("InsertProvider");
        providerAnnotations.add("UpdateProvider");
        providerAnnotations.add("DeleteProvider");

        method.getAnnotations().forEach(annotation -> {
            String annName = annotation.getNameAsString();

            if (sqlAnnotations.contains(annName) || providerAnnotations.contains(annName)) {
                SqlStatement stmt = new SqlStatement();
                stmt.setId(method.getNameAsString());
                stmt.setType(annName.replace("Provider", "").toLowerCase());
                stmt.setFullId(mapperInfo.getNamespace() + "." + stmt.getId());
                stmt.setSource(DefinitionSource.ANNOTATION);
                stmt.setLineNumber(method.getBegin()
                        .map(p -> p.line).orElse(-1));

                // 提取注解中的 SQL 内容（用于高级分析）
                if (annotation instanceof SingleMemberAnnotationExpr) {
                    try {
                        String sql = ((SingleMemberAnnotationExpr) annotation)
                                .getMemberValue().toString();
                        // Remove surrounding quotes if present
                        if (sql.startsWith("\"") && sql.endsWith("\"")) {
                            sql = sql.substring(1, sql.length() - 1);
                        }
                        stmt.setRawSql(sql);
                    } catch (Exception e) {
                        // ignore if cannot parse member value
                    }
                }

                mapperInfo.addStatement(stmt);
            }
        });
    }

    public boolean isBaseMapperMethod(String methodName) {
        return BASE_MAPPER_METHODS.contains(methodName);
    }
}
