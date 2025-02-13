package expression.parser.types;

import expression.exceptions.DivisionByZeroException;

import java.math.BigInteger;

public class BigIntegerType implements ParsingType<BigInteger> {
    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException(a, b);
        }
        return a.divide(b);
    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        return a.min(b);
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        return a.max(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }

    @Override
    public BigInteger valueOf(int a) {
        return BigInteger.valueOf(a);
    }

    @Override
    public BigInteger bitCount(BigInteger a) {
        return BigInteger.valueOf(a.bitCount());
    }

    @Override
    public BigInteger shiftA(BigInteger a, BigInteger b) {
        throw unsupportedOperation("BigInteger: shift_a");
    }

    @Override
    public BigInteger shiftR(BigInteger a, BigInteger b) {
        throw unsupportedOperation("BigInteger: shift_r");
    }

    @Override
    public BigInteger shiftL(BigInteger a, BigInteger b) {
        throw unsupportedOperation("BigInteger: shift_l");
    }
}
