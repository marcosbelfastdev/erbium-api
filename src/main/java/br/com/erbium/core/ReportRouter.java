package br.com.erbium.core;

public interface ReportRouter {
    void commit();
    void setTargetOutput(int targetOutput);
    int getTargetOutput();
    void route(LogType level, LogItem item, String message);
    void route(String message);
    void route(LogType level, String message);
    void route(LogItem item, String message);
    String getName();
}
