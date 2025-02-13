(defn applyWithPrecond [f precond]
  (fn [& args] {:pre [(precond args)]}
    (apply mapv f args)))

(defn isCollectionOf [isTrueColl isTrueElement]
  #(and (isTrueColl %) (or (empty? %) (every? isTrueElement %))))

(def linearVector? (isCollectionOf vector? number?))
(def linearMatrix? (isCollectionOf vector? linearVector?))

(defn isOneSpaceColls [isTrueColl]
  #(and (every? isTrueColl %) (apply = (map count %))))

(def isOneSpaceVectors (isOneSpaceColls linearVector?))
(def isOneSpaceMatrices (isOneSpaceColls linearMatrix?))

(defn vectorOperation [f] (applyWithPrecond f isOneSpaceVectors))
(defn matrixOperation [f] (applyWithPrecond f isOneSpaceMatrices))

(def v+ (vectorOperation +))
(def v- (vectorOperation -))
(def v* (vectorOperation *))
(def vd (vectorOperation /))

(defn v*s [v & s]
  {:pre [(linearVector? v) (every? number? s)]
   :post [(vector? %)(= (count v) (count %))]}
  (let [factor (apply * s)]
  (mapv #(* % factor) v)))

(defn scalar [& args]
  {:pre [(isOneSpaceVectors args)]
   :post [(number? %)]}
  (apply + (apply v* args)))

(defn transpose [m]
  {:pre [(linearMatrix? m)]
   :post [(linearMatrix? %)]}
  (apply mapv vector m))

(def m+ (matrixOperation v+))
(def m- (matrixOperation v-))
(def m* (matrixOperation v*))
(def md (matrixOperation vd))

(defn m*s [m & s]
  {:pre [(linearMatrix? m) (every? number? s)]
   :post [(linearMatrix? %)]}
  (mapv #(apply v*s % s) m))

(defn m*v [m & v]
  {:pre [(linearMatrix? m) (isOneSpaceVectors v)]
   :post [(linearVector? %)]}
  (mapv #(apply scalar % v) m))

(defn vect [& args]
  {:pre [(isOneSpaceVectors args)(<= (count (first args)) 3)]
   :post [(linearVector? %)]}
  (reduce
    (fn [a b]
      (vec
        (map #(- (* (nth a (mod % 3) 0) (nth b (mod (+ % 1) 3) 0))
                 (* (nth a (mod (+ % 1) 3) 0) (nth b (mod % 3) 0)))
             (range 1 4))))
    args))

(defn matrixCompatibility? [& args]
  {:pre (every? linearMatrix? args)
   :post (boolean? %)}
  (= (reduce
       #(if (= (first %1) (second %2)) [(first %1) (second %2)] [-1 -1])
       (mapv #(vector (count %) (count (first %))) args))
     [(count (first args)) (count (first (last args)))]))

(defn m*m [& args]
  {:pre [(every? linearMatrix? args)(matrixCompatibility? args)]
   :post [(linearMatrix? %)]}
  (reduce #(mapv (partial m*v (transpose %2)) %1) args))

(defn tensorShape
  ([t]
   (tensorShape t true))
  ([t unchecked]
   (letfn [(rec [w]
             (if (every? number? w)
               (vector (count w))
               (if (or unchecked (apply = (mapv rec w)))
                 (conj (rec (first w)) (count w))
                 nil)))]
     (if (number? t) (vector) (rec t)))))

(defn linearTensor? [t] (false? (nil? (tensorShape t false))))

(defn compareShape? [& shapes]
  {:pre [(every? linearVector? shapes)]
   :post [(boolean? %)]}
  (every?
    (fn [i]
      (apply = (remove neg? (mapv #(nth % i -1) shapes))))
    (range (apply max (mapv count shapes)))))

(defn castedTensors? [& args] (apply compareShape? (mapv tensorShape args)))

(defn broadcastByShape [t shape]
  {:pre [(compareShape? (tensorShape t) shape)]}
  (let [castShape (subvec shape (count (tensorShape t)))]
    (reduce
      (fn [acc dim]
        (vec (repeat dim acc)))
      t
      castShape)))

(defn broadcast [& args]
  {:pre [(apply castedTensors? args)]}
  (let [commonShape (apply max-key count (mapv tensorShape args))]
    (mapv #(broadcastByShape % commonShape) args)))

(defn tensorOperation [f]
  (fn [& tensors]
    {:pre [(every? linearTensor? tensors)(apply castedTensors? tensors)]}
    (letfn [(rec [& args]
              (if (every? number? args)
                (apply f args)
                (apply mapv rec args)))]
      (apply rec (apply broadcast tensors)))))

(def b+ (tensorOperation +))
(def b- (tensorOperation -))
(def b* (tensorOperation *))
(def bd (tensorOperation /))