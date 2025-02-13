package expression.parser.types;
public class SaturationIntegerType extends IntegerType{
    @Override
    public Integer add(Integer a, Integer b) {
        if (checkAddOverflow(a, b)) {
            return (a < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }
        return super.add(a, b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (checkMultiplyOverflow(a, b)) {
            return ((a < 0)^(b < 0) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }
        return super.multiply(a, b);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (checkSubtractOverflow(a, b)) {
            return (a < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        }
        return super.subtract(a, b);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (checkDivideOverflow(a, b)) {
            return Integer.MAX_VALUE;
        }
        return super.divide(a, b);
    }

    @Override
    public Integer negate(Integer a) {
        if (checkNegateOverflow(a)) {
            return Integer.MAX_VALUE;
        }
        return super.negate(a);
    }
}
