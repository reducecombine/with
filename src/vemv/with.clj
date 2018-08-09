(ns vemv.with)

(defmacro with [clauses & body]
  (let [[test expr & more-clauses] (seq clauses)
        test (if (symbol? test)
               test
               (-> test str (subs 1) symbol))]
    (if more-clauses
      `(~test ~expr (with ~more-clauses (do ~@body)))
      `(~test ~expr ~@body))))
