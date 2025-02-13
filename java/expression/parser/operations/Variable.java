package expression.parser.operations;

import expression.exceptions.UnsupportedVariableException;
import expression.parser.types.ParsingType;

import java.util.List;

public class Variable<T> extends AbstractValue<T> {
    private final String name;
    private final int variableId;

    public Variable(ParsingType<T> type, String name, int id) {
        super(type);
        variableId = id;
        this.name = name;
    }

    public Variable(ParsingType<T> type, String name) {
        this(type, name, -1);
    }

    public Variable(ParsingType<T> type, int id) {
        this(type, "$" + id, id);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public T evaluate(T x) {
        return x;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch (variableId) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> switch (name) {
                case "x" -> x;
                case "y" -> y;
                case "z" -> z;
                default ->
                        throw new UnsupportedVariableException("Variable " + this + " unsupported triple expression");
            };
        };

    }

    @Override
    public T evaluate(List<T> variables) {
        if (variableId < 0) {
            throw new UnsupportedVariableException("Variable " + this + " unsupported list expression");
        } else if (variableId < variables.size()) {
            return variables.get(variableId);
        } else {
            throw new UnsupportedVariableException(
                    "Cannot find variable #" + variableId + " in list of " + variables.size() + "elements"
            );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable<?> objVariable) {
            return name.equals(objVariable.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
