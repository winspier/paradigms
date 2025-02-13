(defn AbstractOperation [f]
  (fn [& args]
    (fn [vars]
      (apply f (mapv #(% vars) args)))))

(defn goodDivide ([arg] (/ 1.0 arg)) ([firstArg & restArgs] (reduce #(/ %1 (double %2)) firstArg restArgs)))
(defn sumInvs [& args] (apply + (map goodDivide args)))

(defn doArithMean [& args] (goodDivide (apply + args) (count args)))
(defn doGeomMean [& args] (Math/pow (abs (apply * args)) (/ 1 (count args))))
(defn doHarmMean [& args] (goodDivide (count args) (apply sumInvs args)))

(def add (AbstractOperation +))
(def subtract (AbstractOperation -))
(def multiply (AbstractOperation *))
(def divide (AbstractOperation goodDivide))
(def negate (AbstractOperation unchecked-negate))
(def arithMean (AbstractOperation doArithMean))
(def geomMean (AbstractOperation doGeomMean))
(def harmMean (AbstractOperation doHarmMean))
(defn variable [name] #(get % name))
(def constant constantly)

(defn parser [operationMap constMaker varMaker]
  (fn [input]
    (letfn [(parse [expr]
              (cond
                (list? expr) (apply (get operationMap (first expr)) (mapv parse (rest expr)))
                (number? expr) (constMaker expr)
                :else (varMaker (str expr))))]
      (parse (read-string input)))))

(def FUNCTIONAL_OPERATIONS {
                            '+         add
                            '-         subtract
                            '*         multiply
                            '/         divide
                            'negate    negate
                            'arithMean arithMean
                            'geomMean  geomMean
                            'harmMean  harmMean
                            })

(def parseFunction (parser FUNCTIONAL_OPERATIONS constant variable))

(load-file "proto.clj")

(def __arguments (field :arguments))
(def _getSign (method :getSign))
(def _applyOperation (method :applyOperation))
(def evaluate (method :evaluate))
(def toString (method :toString))
(def diff (method :diff))
(def _partDiff (method :partDiff))
(def __value (field :value))
(def __diff (field :diff))
(def __expr (field :expr))

(def OperationPrototype
  {
   :arguments __arguments
   :evaluate  (fn [this vars] (apply (_applyOperation this) (mapv #(evaluate % vars) (__arguments this))))
   :toString  (fn [this] (str
                           "("
                           (_getSign this)
                           " "
                           (clojure.string/join " " (mapv toString (__arguments this)))
                           ")"))
   :diff      (fn [this v]
                (_partDiff this (mapv #(let [expr %] {:diff (diff expr v) :expr expr}) (__arguments this))))
   })

(def ValuePrototype
  {:value    __value
   :toString (fn [this] (str (__value this)))
   })

(declare CONSTANT_ZERO)
(def Constant
  (constructor
    (fn [this cnst]
      (assoc this
        :value cnst
        :evaluate (constantly cnst)
        :diff (fn [this v] CONSTANT_ZERO)))
    ValuePrototype))

(def CONSTANT_ONE (Constant 1))
(def CONSTANT_ZERO (Constant 0))

(def Variable
  (constructor
    (fn [this name]
      (assoc this
        :value name
        :evaluate (fn [this vars] (get vars name))
        :diff (fn [this v] (if (= v name) CONSTANT_ONE CONSTANT_ZERO))))
    ValuePrototype))

(defn OperationBuilder [sign f partDiff]
  (constructor
    (fn [this & args] (assoc this :arguments (vec args)))
    {:prototype      OperationPrototype
     :getSign        (fn [this] sign)
     :applyOperation (fn [this] f)
     :partDiff       (fn [this parts] (partDiff parts))
     }))

(declare Add)
(declare Subtract)
(declare Multiply)
(declare Divide)

(defn replacedProduct
  ([array getter] (replacedProduct array getter -1 nil))
  ([array getter replaced-id replaced-value]
  (apply Multiply
         (map-indexed (fn [index item] (if (= index replaced-id) replaced-value (getter item))) array))))

(defn sumPartDiffs
  ([parts] (sumPartDiffs parts __expr __diff))
  ([parts getExpr] (sumPartDiffs parts getExpr __diff))
  ([parts getExpr getDiff]
   (apply Add (map-indexed (fn [i part] (replacedProduct parts #(getExpr %) i (getDiff part))) parts))))

(defn subPartDiffs [parts]
  (let [product (if (= (count parts) 1)
                  (repeat 2 (__expr (first parts)))
                  (mapv __expr (rest parts)))]
    (apply Subtract
           (map-indexed
             (fn [i part]
               (Divide
                 (Multiply (__expr (first parts)) (__diff part))
                 (apply Multiply (__expr (nth parts i)) product)))
             parts))))

(defn squareExpr [x] (Multiply x x))

(def Add (OperationBuilder "+" +
                           (fn [parts] (apply Add (mapv __diff parts)))))
(def Subtract (OperationBuilder "-" -
                                (fn [parts] (apply Subtract (mapv __diff parts)))))
(def Multiply (OperationBuilder "*" *
                                (fn [parts] (sumPartDiffs parts))))
(def Divide (OperationBuilder "/" goodDivide
                              (fn [parts] (subPartDiffs parts))))
(def Negate (OperationBuilder "negate" unchecked-negate
                              (fn [parts] (Negate (__diff (first parts))))))
(def Pow (OperationBuilder "pow" #(Math/pow %1 %2)
                           (fn [parts] (Multiply
                                         (Constant (__expr (second parts)))
                                         (Pow (__diff (first parts)) (dec (__expr (second parts))))))))
(def ArithMean (OperationBuilder "arithMean" doArithMean
                           (fn [parts] (Divide (apply Add (mapv __diff parts)) (Constant (count parts))))))
(def GeomMean (OperationBuilder "geomMean" doGeomMean
                            (fn [parts]
                                    (Divide
                                      (Multiply
                                        (sumPartDiffs parts)
                                        (Pow
                                          (replacedProduct parts __expr)
                                          (Constant (dec (goodDivide 1.0 (count parts))))))
                                      (Constant (count parts))))))

(def HarmMean (OperationBuilder "harmMean" doHarmMean
                                (fn [parts]
                                  (Divide
                                    (Multiply
                                      (Constant (count parts))
                                      (sumPartDiffs parts #(squareExpr (__expr %))))
                                    (squareExpr (sumPartDiffs parts __expr (fn [arg] CONSTANT_ONE)))))))

(def OBJECT_OPERATIONS {
                        '+      Add
                        '-      Subtract
                        '*      Multiply
                        '/      Divide
                        'negate Negate
                        'arithMean ArithMean
                        'geomMean GeomMean
                        'harmMean HarmMean
                        })

(def parseObject (parser OBJECT_OPERATIONS Constant Variable))