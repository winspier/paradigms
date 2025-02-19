## Функциональные выражения на JavaScript
1. Разработайте функции `cnst`, `variable`, `add`, `subtract`, `multiply`, `divide`, `negate` для вычисления выражений с тремя переменными: `x`, `y` и `z`.
Функции должны позволять производить вычисления вида:
    ```javascript
    let expr = subtract(
        multiply(
            cnst(2),
            variable("x")
        ),
        cnst(3)
    );
    
    println(expr(5, 0, 0));
    `````
2. При вычислении выражения вместо каждой переменной подставляется значение, переданное в качестве соответствующего параметра функции `expr`. Таким образом, результатом вычисления приведенного примера должно быть число `7`.
3. Тестовая программа должна вычислять выражение `x2−2x+1`, для `x` от `0` до `10`.
4. Требуется дополнительно написать функцию `parse`, осуществляющую разбор выражений, записанных в обратной польской записи. Например, результатом
`parse("x x 2 - * x * 1 +")(5, 0, 0)`
должно быть число `76`.
5. При выполнении задания следует обратить внимание на:
   - Применение функций высшего порядка.
   - Выделение общего кода для операций.
## Объектные выражения на JavaScript
1. Разработайте классы `Const`, `Variable`, `Add`, `Subtract`, `Multiply`, `Divide`, `Negate` для представления выражений с тремя переменными: `x`, `y` и `z`.
   1. Пример описания выражения `2x-3`:
       ```javascript
       let expr = new Subtract(
           new Multiply(
               new Const(2),
               new Variable("x")
           ),
           new Const(3)
       );
    
       println(expr.evaluate(5, 0, 0));
       ``````
   2. При вычислении такого выражения вместо каждой переменной подставляется её значение, переданное в качестве аргумента метода `evaluate`. Таким образом, результатом вычисления приведенного примера должно стать число `7`.
   3. Метод `toString()` должен выдавать запись выражения в обратной польской записи. Например, `expr.toString()` должен выдавать «2 x * 3 -».
2. Функция `parse` должна выдавать разобранное объектное выражение.
3. Метод `diff("x")` должен возвращать выражение, представляющее производную исходного выражения по переменной `x`. Например, `expr.diff("x")` должен возвращать выражение, эквивалентное new `Const(2)`. Выражения `new Subtract(new Const(2), new Const(0))` и
    ```javascript
    new Subtract(
        new Add(
            new Multiply(new Const(0), new Variable("x")),
            new Multiply(new Const(2), new Const(1))
        ),
        new Const(0)
    )
    ``````
    так же будут считаться правильным ответом.
4. При выполнении задания следует обратить внимание на:
   - Применение инкапсуляции.
   - Выделение общего кода для операций.
   - Минимизацию необходимой памяти.
## Обработка ошибок на JavaScript
1. Добавьте в предыдущее домашнее задание функцию `parsePrefix(string)`, разбирающую выражения, задаваемые записью вида «(- (* 2 x) 3)». Если разбираемое выражение некорректно, метод `parsePrefix` должен бросать ошибки с человеко-читаемыми сообщениями.
2. Добавьте в предыдущее домашнее задание метод `prefix()`, выдающий выражение в формате, ожидаемом функцией `parsePrefix`.
3. При выполнении задания следует обратить внимание на:
   - Применение инкапсуляции.
   - Выделение общего кода для операций.
   - Минимизацию необходимой памяти.
   - Обработку ошибок.