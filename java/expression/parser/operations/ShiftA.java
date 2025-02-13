package expression.parser.operations;

public class ShiftA<T> extends AbstractBinary<T> {
    public ShiftA(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    public String getSign() {
        return ">>>";
    }

    @Override
    protected int getPriority() {
        return 4;
    }

    @Override
    protected T getResult(T a, T b) {
        return type.shiftA(a, b);
    }
}
