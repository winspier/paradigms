package expression.parser;

import expression.exceptions.*;
import expression.parser.operations.*;
import expression.parser.types.ParsingType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;

public class ExpressionParser<T> {
    public MyGenericExpression<T> parse(final ParsingType<T> type, final String source) {
        return parse(type, new StringSource(source), new String[]{"x", "y", "z"});
    }

    public MyGenericExpression<T> parse(final ParsingType<T> type, final String source, final List<String> variables) {
        return parse(type, new StringSource(source), variables.toArray(new String[0]));
    }

    public MyGenericExpression<T> parse(final ParsingType<T> type, final CharSource source, final String[] variables) {
        return new MyExpressionParser<>(type, source, variables).parseExpression();
    }

    private static class MyExpressionParser<T> extends BaseParser {
        private final String[] VARIABLES;
        private final ParsingType<T> TYPE;
        private final List<Map<String, BinaryOperationFactory<T>>> OPERATIONS_BY_PRIORITY = List.of(
            Map.of("*", Multiply::new,"/", Divide::new),
            Map.of("+", Add::new,"-", Subtract::new),
            Map.of("min", Min::new,"max", Max::new),
            Map.of(">>>", ShiftA::new,">>", ShiftL::new,"<<", ShiftR::new)
        );
        private final Map<String, UnaryOperationFactory<T>> UNARY_FACTORY = Map.of(
                "-", Negate::new,
                "count", Count::new
        );
        private final String[][] SIGNS_BY_PRIORITY = OPERATIONS_BY_PRIORITY.stream()
                .map(map -> map.keySet().toArray(String[]::new))
                .toArray(String[][]::new);
        private final Map<String, BinaryOperationFactory<T>> BINARY_FACTORY = OPERATIONS_BY_PRIORITY.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        private final String[] BINARY_OPERATIONS = BINARY_FACTORY.keySet().toArray(new String[0]);
        private final String[] UNARY_OPERATIONS = UNARY_FACTORY.keySet().toArray(new String[0]);

        public MyExpressionParser(final ParsingType<T> type, final CharSource source, final String[] variables) {
            super(source);
            this.TYPE = type;
            this.VARIABLES = variables;
        }

        public MyGenericExpression<T> parseExpression() {
            final MyGenericExpression<T> result = parseElement();
            if (eof()) {
                return result;
            } else {
                int pos = getPosition();
                throw new EndOfExpressionException("Expected END, found " + nextToken(), pos);
            }
        }

        private MyGenericExpression<T> parseElement() {
            skipWhitespace();
            if (eof()) {
                throw new EmptyExpressionException("Expected expression", getPosition());
            }
            final MyGenericExpression<T> result = parseComplex(OPERATIONS_BY_PRIORITY.size());
            skipWhitespace();
            return result;
        }

        private MyGenericExpression<T> parseBrackets() {
            int pos = getPosition();
            char closeBracket = switch (take()) {
                case '(' -> ')';
                case '[' -> ']';
                case '{' -> '}';
                default -> throw new IllegalStateException("Expected parenthesis, found " + current());
            };
            skipWhitespace();
            if (take(closeBracket)) {
                throw new EmptyExpressionException("Empty expression in brackets", pos);
            } else if (test(')', ']', '}')) {
                throw new InvalidBracketSequenceException(
                        "Incorrect closing parenthesis: expected " + closeBracket + ", found " + take(), getPosition() - 1
                );
            }
            var res = parseElement();
            if (!test(closeBracket) && test(')', ']', '}')) {
                throw new InvalidBracketSequenceException(
                        "Incorrect closing parenthesis: expected " + closeBracket + ", found " + take(), getPosition() - 1
                );
            } else if (!take(closeBracket)) {
                throw new InvalidBracketSequenceException("No closing parenthesis", getPosition());
            }
            return res;
        }

        private MyGenericExpression<T> parseElementary() {
            skipWhitespace();
            final int pos = getPosition();
            if (eof()) {
                throw new MissedArgumentsException("No last argument", pos);
            } else if (take('-')) {
                return isDigit() ?
                        new Const<>(TYPE, parseNumber(true)) :
                        UNARY_FACTORY.get("-").create(parseElementary());
            } else if (take(BINARY_OPERATIONS) != null) {
                throw new MissedArgumentsException("No first argument", pos);
            } else if (isDigit()) {
                return new Const<>(TYPE, parseNumber(false));
            } else if (test('(', '[', '{')) {
                return parseBrackets();
            } else {
                for (String unary : UNARY_OPERATIONS) {
                    if (take(unary) != null) {
                        return UNARY_FACTORY.get(unary).create(parseElementary());
                    }
                }
                int variableId = takeId(VARIABLES);
                if (variableId >= 0 && (eof() || !isJavaIdentifierPart(current()))) {
                    return new Variable<>(TYPE, VARIABLES[variableId], variableId);
                } else {
                    throw new UnsupportedTokenException(nextToken(), pos);
                }
            }
        }

        private MyGenericExpression<T> parseComplex(int priority) {
            if (priority == 0) {
                return parseElementary();
            }
            var resExpression = parseComplex(priority - 1);
            skipWhitespace();
            if (!eof()) {
                String operation = getBinaryOperation(priority);
                skipWhitespace();
                while (operation != null) {
                    resExpression = getBinary(operation, resExpression, parseComplex(priority - 1));
                    operation = getBinaryOperation(priority);
                }
            }
            return resExpression;
        }

        private boolean isCorrectVariable(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (!isJavaIdentifierPart(s.charAt(i))) {
                    return false;
                }
            }
            return isJavaIdentifierStart(s.charAt(0));
        }

        private String getBinaryOperation(int priority) {
            skipWhitespace();
            String result = take(SIGNS_BY_PRIORITY[priority - 1]);
            if (!eof() && result != null && !isWhitespace() &&
                    isCorrectVariable(result) &&
                    !test('(', ')', '[', ']', '{', '}', '-')
            ) {
                throw new UnsupportedTokenException("Unexpected " + current() + " with " + result, getPosition());
            }
            return result;
        }

        private AbstractBinary<T> getBinary(String sign, MyGenericExpression<T> left, MyGenericExpression<T> right) {
            return BINARY_FACTORY.get(sign).create(left, right);
        }

        private T parseNumber(boolean isNegative) {
            StringBuilder sb = new StringBuilder();
            if (isNegative || take('-')) {
                sb.append('-');
            }
            if (take('0')) {
                sb.append('0');
                if (take('0')) {
                    throw new InvalidNumberException("Duplicated 0", getPosition());
                }
            } else if (between('1', '9') || test('.')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
                if (take('.')) sb.append('.');
                while (between('0', '9')) {
                    sb.append(take());
                }
            } else {
                throw new InvalidNumberException("Expected digit, found " + take(), getPosition());
            }
            int pos = getPosition();
            skipWhitespace();
            if (isDigit()) {
                throw new InvalidNumberException("Spaces in numbers", pos);
            }
            try {
                return TYPE.parse(sb.toString());
            } catch (NumberFormatException e) {
                throw new InvalidNumberException("Number overflow", pos - sb.length() + 1);
            }
        }

    }
}
