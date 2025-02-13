package expression.parser.types;

import expression.exceptions.DivisionByZeroException;

public class IntegerType implements ParsingType<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException(a, b);
        }
        return a / b;
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }

    @Override
    public Integer negate(Integer a) {
        return -a;
    }

    @Override
    public Integer bitCount(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer parse(String s) {
        return Integer.parseInt(s);
    }

    @Override
    public Integer valueOf(int a) {
        return a;
    }

    @Override
    public Integer shiftA(Integer a, Integer b) {
        return a >>> b;
    }

    @Override
    public Integer shiftR(Integer a, Integer b) {
        return a >> b;
    }

    @Override
    public Integer shiftL(Integer a, Integer b) {
        return a << b;
    }

    public boolean checkAddOverflow(Integer a, Integer b) {
        return b > 0 && a > Integer.MAX_VALUE - b || b < 0 && a < Integer.MIN_VALUE - b;
    }
    public boolean checkSubtractOverflow(Integer a, Integer b) {
        return b < 0 && a > Integer.MAX_VALUE + b || b > 0 && a < Integer.MIN_VALUE + b;
    }
    public boolean checkMultiplyOverflow(Integer a, Integer b) {
        return (
            (a > 0) && (
                (b > 0) && (a > (Integer.MAX_VALUE / b)) ||
                (b < 0) && (b < (Integer.MIN_VALUE / a))
            ) ||
            (a < 0) &&
            (
                (b > 0) && (a < (Integer.MIN_VALUE / b)) ||
                (b < 0) && (b < (Integer.MAX_VALUE / a))
        ));
    }
    public boolean checkDivideOverflow(Integer a, Integer b) {
        return a == Integer.MIN_VALUE && b == -1;
    }
    public boolean checkNegateOverflow(Integer a){
        return a == Integer.MIN_VALUE;
    }
}
