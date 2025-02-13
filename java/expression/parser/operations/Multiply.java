package expression.parser.operations;

public class Multiply<T> extends AbstractBinary<T> implements Commutativity {
    public Multiply(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T getResult(T a, T b) {
        return type.multiply(a, b);
    }

    @Override
    public String getSign() {
        return "*";
    }

    @Override
    protected int getPriority() {
        return 1;
    }
}
