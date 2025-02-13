package expression.parser.types;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class CheckedIntegerType extends IntegerType {
    @Override
    public Integer add(Integer a, Integer b) {
        if (checkAddOverflow(a, b)) {
            throw new OverflowException("integer", "add", a, b);
        }
        return super.add(a, b);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (checkSubtractOverflow(a, b)) {
            throw new OverflowException("integer", "subtract", a, b);
        }
        return super.subtract(a, b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (checkMultiplyOverflow(a, b)) {
            throw new OverflowException("integer", "multiply", a, b);
        }
        return super.multiply(a, b);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException(a, b);
        }
        if (checkDivideOverflow(a, b)) {
            throw new OverflowException("integer", "divide", a, b);
        }
        return super.divide(a, b);
    }

    @Override
    public Integer negate(Integer a) {
        if (checkNegateOverflow(a)) {
            throw new OverflowException("integer", "negate", a);
        }
        return super.negate(a);
    }
}
