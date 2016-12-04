/*
 *  LifeCycleAgent
 *
 *  MIT License
 *
 *  Copyright (c) 2016
 *
 *  Geoffrey Mastenbroek, geoffrey.mastenbroek@student.hu.nl
 *  Feiko Wielsma, feiko.wielsma@student.hu.nl
 *  Robbin van den Berg, robbin.vandenberg@student.hu.nl
 *  Arnoud den Haring, arnoud.denharing@student.hu.nl
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.maschel.lca.lcacloud.lca.lcadevice.analytics;

import java.util.function.BiFunction;

public class AggregateOperator<T, U, R> implements Aggregates<T, U, R> {

    public static AggregateOperator<Integer, Integer, Integer> TOTAL(Integer defaultValue) {
        return new AggregateOperator<>("total", (oldValue, addValue) -> oldValue + addValue, defaultValue);
    }
    public static AggregateOperator<Double, Double, Double> TOTAL(Double defaultValue) {
        return new AggregateOperator<>("total", (oldValue, addValue) -> oldValue + addValue, defaultValue);
    }
    public static AggregateOperator<Integer, Integer, Integer> MAX(Integer defaultValue) {
        return new AggregateOperator<>("max", (oldValue, newValue) -> oldValue > newValue ? oldValue:newValue,
                defaultValue);
    }
    public static AggregateOperator<Double, Double, Double> MAX(Double defaultValue) {
        return new AggregateOperator<>("max", (oldValue, newValue) -> oldValue > newValue ? oldValue:newValue,
                defaultValue);
    }
    public static AggregateOperator<Integer, Integer, Integer> MIN(Integer defaultValue) {
        return new AggregateOperator<>("min", (oldValue, newValue) -> oldValue < newValue ? oldValue:newValue,
                defaultValue);
    }
    public static AggregateOperator<Double, Double, Double> MIN(Double defaultValue) {
        return new AggregateOperator<>("min", (oldValue, newValue) -> oldValue < newValue ? oldValue:newValue,
                defaultValue);
    }

    private String description;
    private BiFunction<T, U, R> operator;
    private R defaultValue;

    public AggregateOperator(String description, BiFunction<T, U, R> operator, R defaultValue) {
        this.description = description;
        this.operator = operator;
        this.defaultValue = defaultValue;
    }

    @Override
    public R calculate(T firstArg, U secondArg) {
        return operator.apply(firstArg, secondArg);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public R getDefaultValue() {
        return defaultValue;
    }
}
