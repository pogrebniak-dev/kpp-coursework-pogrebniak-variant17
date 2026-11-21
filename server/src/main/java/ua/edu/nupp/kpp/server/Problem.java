package ua.edu.nupp.kpp.server;

public class Problem {
    private Long id;
    private long observationId;
    private ProblemState state;
    private double riskIndex;

    public Problem() {
    }

    public Problem(Long id, long observationId, ProblemState state, double riskIndex) {
        this.id = id;
        this.observationId = observationId;
        this.state = state;
        this.riskIndex = riskIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getObservationId() {
        return observationId;
    }

    public void setObservationId(long observationId) {
        this.observationId = observationId;
    }

    public ProblemState getState() {
        return state;
    }

    public void setState(ProblemState state) {
        this.state = state;
    }

    public double getRiskIndex() {
        return riskIndex;
    }

    public void setRiskIndex(double riskIndex) {
        this.riskIndex = riskIndex;
    }
}