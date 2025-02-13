package expression.parser.operations;

public class Add<T> extends AbstractBinary<T> implements Commutativity {
    public Add(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    // :NOTE: type::add
    @Override
    protected T getResult(T a, T b) {
        return type.add(a, b);
    }

    @Override
    public String getSign() {
        return "+";
    }

    @Override
    protected int getPriority() {
        return 2;
    }
}
