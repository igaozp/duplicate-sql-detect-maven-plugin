package com.igaozp.dsd;

import java.util.ArrayList;
import java.util.List;

public class MapperInfo {
    private String namespace;
    private String filePath;
    private List<SqlStatement> statements;

    public MapperInfo(String namespace, String filePath) {
        this.namespace = namespace;
        this.filePath = filePath;
        this.statements = new ArrayList<>();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<SqlStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<SqlStatement> statements) {
        this.statements = statements;
    }

    public void addStatement(SqlStatement statement) {
        this.statements.add(statement);
    }
}
