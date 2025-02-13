package expression.parser.types;

import static java.lang.Double.doubleToLongBits;

public class DoubleType implements ParsingType<Double> {
    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double min(Double a, Double b) {
        return Math.min(a, b);
    }

    @Override
    public Double max(Double a, Double b) {
        return Math.max(a, b);
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double parse(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public Double valueOf(int a) {
        return (double) a;
    }

    @Override
    public Double bitCount(Double a) {
        return (double) Long.bitCount(doubleToLongBits(a));
    }

    @Override
    public Double shiftA(Double a, Double b) {
        throw unsupportedOperation("Double: shiftA");
    }

    @Override
    public Double shiftR(Double a, Double b) {
        throw unsupportedOperation("Double: shiftR");
    }

    @Override
    public Double shiftL(Double a, Double b) {
        throw unsupportedOperation("Double: shiftL");
    }
}
