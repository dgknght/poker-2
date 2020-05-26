(ns poker.core-test
  (:require [clojure.test :refer [deftest is]]
            [poker.core :as poker]))

(deftest create-a-deck-of-cards
  (let [deck (poker/deck)]
    (is (sequential? deck) "The deck is a sequence")
    (is (= 52 (count deck)) "The deck contains 52 'cards'")
    (is (= 13 (->> deck
                   (filter #(= :hearts (second %)))
                   count))
        "There are 13 hears")
    (is (= 13 (->> deck
                   (filter #(= :diamonds (second %)))
                   count))
        "There are 13 diamonds")
    (is (= 13 (->> deck
                   (filter #(= :spades (second %)))
                   count))
        "There are 13 spades")
    (is (= 13 (->> deck
                   (filter #(= :clubs (second %)))
                   count))
        "There are 13 clubs")))

(deftest create-a-shuffled-deck-of-cards
  (let [unshuffled (poker/deck)]
    (is (not (= unshuffled
                (poker/deck {:shuffle? true})))
        "A shuffled deck is not in the same order as a naturally created deck")))

(deftest deal-two-hands
  (let [game (poker/deal {:hands [[] []]
                          :deck (poker/deck)})]
    (is (= [[:K :hearts]]
           (-> game :hands first))
        "The first hand received the first card")
    (is (= [[:Q :hearts]]
           (-> game :hands second))
        "The second hand received the second card")
    (is (= 50 (-> game :deck count))
        "The deck contains two fewer cards")))
