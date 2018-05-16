package com.cts.trader.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "Futures")
public class Future implements Serializable {
    @Id
    @Column(nullable = false, name = "futures_id")
    private String futureID;

    @Column(nullable = false, name = "futures_name")
    private String futureName;

    @Column(nullable = false, name = "period")
    private String period;

    @Column(nullable = false, name = "symbol")
    private String symbol;


    public Future() {}

    public Future(String futureID, String futureName, String period, String symbol) {
        this.futureID = futureID;
        this.futureName = futureName;
        this.period = period;
        this.symbol = symbol;
    }

    public String getFutureID() {
        return futureID;
    }

    public void setFutureID(String futureID) {
        this.futureID = futureID;
    }

    public String getFutureName() {
        return futureName;
    }

    public void setFutureName(String futureName) {
        this.futureName = futureName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
