(ns poker.deck-test
  (:require [clojure.test :refer [deftest is testing]]
            [poker.deck :as deck]))

(deftest create-a-deck-of-cards
  (let [deck (deck/deck)]
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
  (let [unshuffled (deck/deck)]
    (is (not (= unshuffled
                (deck/deck {:shuffle? true})))
        "A shuffled deck is not in the same order as a naturally created deck")))

(deftest deal-some-cards
  (testing "two hands, one round"
    (let [game (deck/deal {:hands [[] []]
                           :deck (deck/deck)})]
      (is (= [[:K :hearts]]
             (-> game :hands first))
          "The first hand received the first card")
      (is (= [[:Q :hearts]]
             (-> game :hands second))
          "The second hand received the second card")
      (is (= 50 (-> game :deck count))
          "The deck contains two fewer cards")))
  (testing "three hands, 3 rounds"
    (let [{:keys [hands deck]} (deck/deal {:hands [[] [] []]
                                           :deck (deck/deck)}
                                          {:rounds 3})]
      (is (= 43 (count deck))
          "The deck contains 9 fewer cards")
      (is (= [[:K :hearts]
              [:10 :hearts]
              [:7 :hearts]]
             (nth hands 0))
          "The first hand receives the first card of each round")
      (is (= [[:Q :hearts]
              [:9 :hearts]
              [:6 :hearts]]
             (nth hands 1))
          "The second hand receives the second card of each round")
      (is (= [[:J :hearts]
              [:8 :hearts]
              [:5 :hearts]]
             (nth hands 2))
          "The third hand receives the third card of each round"))))
