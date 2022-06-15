package com.acuvuz.BarriersDesktop.DTO;


public class ParsedPortData {
    private String code;
    private String reader;

    public ParsedPortData(String portData) {
            var variables = portData.split(";");
            code = variables[0].split("=")[1].trim();
            reader = variables[1].split("=")[1].trim();
    }

    private ParsedPortData() {}

    public void setCode(String code) {
        this.code = code;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public static ParsedPortData createGuestParsedPortData(String reader) {
        var newParsed = new ParsedPortData();
        newParsed.setCode("guest");
        newParsed.setReader(reader);
        return newParsed;
    }

    public String getCode() {
        return code;
    }

    public String getReader() {
        return reader;
    }
}
