package com.igaozp.dsd;

public class SqlStatement {
    private String id;
    private String type; // select, insert, update, delete
    private String fullId;
    private int lineNumber;
    private DefinitionSource source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullId() {
        return fullId;
    }

    public void setFullId(String fullId) {
        this.fullId = fullId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public DefinitionSource getSource() {
        return source;
    }

    public void setSource(DefinitionSource source) {
        this.source = source;
    }

    private String rawSql;

    public String getRawSql() {
        return rawSql;
    }

    public void setRawSql(String rawSql) {
        this.rawSql = rawSql;
    }
}
