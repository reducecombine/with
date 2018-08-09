(ns vemv.with-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [vemv.with :refer :all]))

(deftest with-test

  (testing "Supports :let, :when :when-let, :do and :while directives"
    (testing "Basic :let and :when support"
      (testing "Passing :when clause"
        (is (= [4 4]
               (with [:let [a 2]
                      :when (pos? a)
                      :let [b (* a 2)]]
                     [b b]))))
      (testing "Failing :when clause"
        (is (nil?
             (with [:let [a -1]
                    :when (pos? a)
                    :let [b (* a 2)]]
                   [b b])))))

    (testing ":when-let"
      (testing "Passing :when-let clause"
        (is (= [4 4]
               (with [:let [a 2]
                      :when-let [b (some-> a (* 2))]]
                     [b b]))))
      (testing "Failing :when-let clause"
        (is (nil?
             (with [:let [a nil]
                    :when-let [b (some-> a (* 2))]]
                   [b b])))))

    (testing ":do"
      (let [proof (atom nil)
            result (with [:let [a 2]
                          :when (pos? a)
                          :do (reset! proof :proven)
                          :let [b (* a 2)]]
                         [b b])]
        (is (= result [4 4]))
        (is (= :proven @proof))))

    (testing ":while"
      (testing "Top-level :while"
        (let [counter (atom 0)
              withition (atom true)
              proof (atom [])]
          (with [:while @withition
                 :let [n (inc @counter)]
                 :do (reset! counter n)
                 :do (reset! withition (< @counter 10))]
                (swap! proof conj n))
          (is (= @proof [1 2 3 4 5 6 7 8 9 10]))))
      (testing "Nested :while"
        (let [proof (atom [])]
          (with [:let [a (atom 1)]
                 :while (< @a 10)
                 :let [b (* @a @a)]
                 :do (swap! proof conj b)]
                (swap! a inc))
          (is (= @proof [1 4 9 16 25 36 49 64 81])))))

    (testing ":when-some"
      (testing "Passing :when-some clause"
        (is (= [false false]
               (with [:let [a [false]]
                      :when-some [b (some-> a first)]]
                     [b b]))))
      (testing "Failing :when-some clause"
        (is (nil?
             (with [:let [a [nil]]
                    :when-some [b (some-> a first)]]
                   [b b])))))))
