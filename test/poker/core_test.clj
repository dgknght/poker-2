(ns poker.core-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.data :refer [diff]]
            [clojure.pprint :refer [pprint]]
            [poker.core :as poker]))

(deftest identify-a-high-card
  (is (= {:classification :high-card
          :ranks [:K :9 :4 :3 :2]}
         (poker/score [[:K :clubs]
                       [:4 :diamonds]
                       [:2 :spades]
                       [:9 :spades]
                       [:3 :hearts]]))))

(deftest identify-a-pair
  (let [expected{:classification :pair
                 :rank :2
                 :cards #{[:2 :hearts]
                          [:2 :spades]}
                 :ranks [:K :9 :4]}
        actual (poker/score [[:K :clubs]
                             [:4 :diamonds]
                             [:2 :spades]
                             [:9 :spades]
                             [:2 :hearts]])]
    (when-not (= expected actual)
      (pprint (diff expected actual)))
    (is (= expected actual))))

;(def queen-high
;  [[:8  :hearts]
;   [:Q  :hearts]
;   [:3  :spades]
;   [:5 :clubs]
;   [:6 :clubs]])
;
;(def king-high
;  [[:9  :hearts]
;   [:K  :hearts]
;   [:2  :spades]
;   [:10 :clubs]
;   [:3  :clubs]])
;
;(deftest high-card-wins
;  (is (poker/beats? king-high queen-high)
;      "King high beats queen high")
;  (is (not (poker/beats? queen-high king-high))
;      "Queen high does not beat king high"))
;
;(def pair-of-twos
;  [[:A :hearts]
;   [:2 :hearts]
;   [:2 :spades]
;   [:6 :clubs]
;   [:J :diamonds]])
;
;(deftest a-pair-beats-a-high-card
;  (is (poker/beats? pair-of-twos king-high)
;      "A pair of twos beats a king-high")
;  (is (not (poker/beats? king-high pair-of-twos))
;      "A king-high does not beat a pair of twos"))
;
;(def pair-of-aces
;  [[:A :hearts]
;   [:2 :hearts]
;   [:A :spades]
;   [:6 :clubs]
;   [:J :diamonds]])
;
;(deftest a-pair-of-aces-beats-a-pair-of-twos
;  (is (poker/beats? pair-of-aces pair-of-twos)
;      "A pair of aces beats a pair of twos")
;  (is (not (poker/beats? pair-of-twos pair-of-aces))
;      "A pair of twos does not beat a pair of aces"))
