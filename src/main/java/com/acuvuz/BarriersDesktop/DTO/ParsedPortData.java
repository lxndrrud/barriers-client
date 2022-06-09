package com.acuvuz.BarriersDesktop.DTO;


public class ParsedPortData {
    private String code;
    private String reader;

    public ParsedPortData(String portData) {
            var variables = portData.split(";");
            code = variables[0].split("=")[1].trim();
            reader = variables[1].split("=")[1].trim();
    }

    public String getCode() {
        return code;
    }

    public String getReader() {
        return reader;
    }
}
