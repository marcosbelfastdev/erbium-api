package br.com.erbium.core;

public interface ReportRouter {
    void commit();
    void setTargetOutput(int targetOutput);
    int getTargetOutput();
    void route(EType level, EItem item, String message);
    void route(String message);
    void route(EType level, String message);
    void route(EItem item, String message);
    String getName();
}
