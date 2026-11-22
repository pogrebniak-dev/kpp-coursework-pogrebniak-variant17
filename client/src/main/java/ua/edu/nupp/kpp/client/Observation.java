package ua.edu.nupp.kpp.client;

public class Observation {
    private Long id;
    private long fieldId;
    private String obsDate;
    private double f1;
    private double f2;
    private double f3;

    public Observation() {
    }

    public Observation(Long id, long fieldId, String obsDate, double f1, double f2, double f3) {
        this.id = id;
        this.fieldId = fieldId;
        this.obsDate = obsDate;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public String getObsDate() {
        return obsDate;
    }

    public void setObsDate(String obsDate) {
        this.obsDate = obsDate;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }
}
