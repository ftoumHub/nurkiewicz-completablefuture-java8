package com.reactive.util;

public class Unit {

    public static Unit Unit = new Unit();

    public static Unit unit() {
        return Unit;
    }

    @Override
    public String toString() {
        return "Unit";
    }
}
