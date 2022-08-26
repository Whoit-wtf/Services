package com;

public class ResultCommand {
    private Integer exitStatus =null;
    private String outLog = null;

    public void setExitStatus(Integer exitStatus) {
        this.exitStatus = exitStatus;
    }

    public Integer getExitStatus() {
        return exitStatus;
    }

    public String getOutLog() {
        return outLog;
    }

    public void setOutLog(String outLog) {
        if (outLog != null){
            this.outLog = outLog.replace("\n", "");
        }

    }
}
