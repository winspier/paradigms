package expression.parser.types;

import expression.exceptions.DivisionByZeroException;

public class ByteType implements ParsingType<Byte> {
    @Override
    public Byte add(Byte a, Byte b) {
        return (byte) (a + b);
    }

    @Override
    public Byte subtract(Byte a, Byte b) {
        return (byte) (a - b);
    }

    @Override
    public Byte multiply(Byte a, Byte b) {
        return (byte) (a * b);
    }

    @Override
    public Byte divide(Byte a, Byte b) {
        if (b == 0) {
            throw new DivisionByZeroException(a, b);
        }
        return (byte) (a / b);
    }

    @Override
    public Byte min(Byte a, Byte b) {
        return (byte) Math.min(a, b);
    }

    @Override
    public Byte max(Byte a, Byte b) {
        return (byte) Math.max(a, b);
    }

    @Override
    public Byte negate(Byte a) {
        return (byte) -a;
    }

    @Override
    public Byte parse(String s) {
        return Byte.parseByte(s);
    }

    @Override
    public Byte valueOf(int a) {
        return (byte) a;
    }

    @Override
    public Byte bitCount(Byte a) {
        return (byte) Integer.bitCount(Byte.toUnsignedInt(a));
    }
    @Override
    public Byte shiftA(Byte a, Byte b) {
        return (byte) (a >>> b);
    }

    @Override
    public Byte shiftR(Byte a, Byte b) {
        return (byte) (a >> b);
    }

    @Override
    public Byte shiftL(Byte a, Byte b) {
        return (byte) (a << b);
    }
}
