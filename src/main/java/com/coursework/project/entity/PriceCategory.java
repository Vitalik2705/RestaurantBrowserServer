package com.coursework.project.entity;

public enum PriceCategory {
  LOW("$"),
  MEDIUM("$$"),
  HIGH("$$$");

  private final String symbol;

  PriceCategory(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}