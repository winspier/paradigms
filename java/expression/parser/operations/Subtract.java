package expression.parser.operations;

public class Subtract<T> extends AbstractBinary<T> implements HalfCommutativity {
    public Subtract(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T getResult(T a, T b) {
        return type.subtract(a, b);
    }

    @Override
    public String getSign() {
        return "-";
    }

    @Override
    protected int getPriority() {
        return 2;
    }
}
