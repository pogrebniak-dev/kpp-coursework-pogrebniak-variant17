package ua.edu.nupp.kpp.client;

public class Field {
    private Long id;
    private String name;
    private double area;
    private String cropType;

    public Field() {
    }

    public Field(Long id, String name, double area, String cropType) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.cropType = cropType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
}