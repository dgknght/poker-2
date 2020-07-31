(ns poker.core
  #_(:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(def rank-values
  (->> [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]
       (map-indexed (fn [i r] [r i]))
       (into {})))

(def ^:private rank first)

(def ^:private suit second)

(defmulti rank-value
  (fn [card-or-rank-key]
    (if (vector? card-or-rank-key)
      :vector
      :scalar)))

(defmethod rank-value :scalar
  [rank-key]
  {:pre [(contains? rank-values rank-key)]}
  (rank-values rank-key))

(defmethod rank-value :vector
  [card]
  ((comp rank-value rank) card))

(defn- extract-high-card
  [hand]
  {:classification :high-card
   :remaining-ranks (->> hand
                         (map rank)
                         (sort-by rank-values >))})

(defn- of-a-kind
  [cards {:keys [count-of-kind
                count-of-sets]}]
  (->> cards
       (group-by rank)
       (filter #(= count-of-kind (count (second %))))
       (sort-by (comp rank-values rank) >)
       (map #(update-in % [1] set))
       (take count-of-sets)))

(defn- extract-pair
  [hand]
  (let [[[rank cards]] (of-a-kind hand {:count-of-kind 2
                                        :count-of-sets 1})]
    (when rank
      {:classification :pair
       :rank rank
       :cards cards
       :remaining-ranks (->> hand
                             (remove cards)
                             (map first)
                             (sort-by rank-values >))})))

(defn- extract-two-pair
  [hand]
  (let [match (of-a-kind hand {:count-of-kind 2
                               :count-of-sets 2})]
    (when (= 2 (count match))
      {:classification :two-pair
       :ranks (map rank match)
       :cards (map second match)
       :remaining-ranks (->> hand
                             (remove (-> match first second))
                             (remove (-> match second second))
                             (map first)
                             (sort-by rank-values >))})))

(defn- extract-three-of-a-kind
  [hand]
  (let [[[rank cards]] (of-a-kind hand {:count-of-kind 3
                                        :count-of-sets 1})]
    (when rank
      {:classification :three-of-a-kind
       :rank rank
       :cards cards
       :remaining-ranks (->> hand
                             (remove cards)
                             (map first)
                             (sort-by rank-value >))})))

(defn- match-straight
  [cards]
  (let [card-seq (->> cards
                      (map #(hash-map :card %
                                      :rank-value (rank-value %)))
                      (sort-by :rank-value >)
                      (partition-all 2 1)
                      (map (fn [[n p]]
                             (assoc n :step (if p
                                              (- (:rank-value n)
                                                 (:rank-value p))
                                              1))))
                      (take-while #(= 1 (:step %)))
                      (map :card)
                      (take 5))]
    (when (= 5 (count card-seq))
      card-seq)))

(defn- extract-straight
  [cards]
  (let [match (match-straight cards)]
    (when match
      {:classification :straight
       :top-rank (-> match first rank)
       :cards match})))

(defn- match-flush
  [cards]
  (->> cards
       (group-by suit)
       vals
       (filter #(<= 5 (count %)))
       (map #(sort-by rank-value > %))
       (sort-by (comp rank-value ; sort the groups of cards
                      first))
       (map #(take 5 %))
       first))

(defn- extract-flush
  [cards]
  (let [match (match-flush cards)]
    (when match
      {:classification :flush
       :top-rank (ffirst match)
       :cards match})))

(defn- extract-four-of-a-kind
  [hand]
  (let [[[rank cards]] (of-a-kind hand {:count-of-kind 4
                                        :count-of-sets 1})]
    (when rank
      {:classification :four-of-a-kind
       :rank rank
       :cards cards
       :remaining-ranks (->> hand
                             (remove cards)
                             (map first)
                             (sort-by rank-value >))})))

(defn- extract-straight-flush
  [cards]
  (let [[top :as match] (-> cards
                            match-flush
                            match-straight)]
    (when match
      {:classification :straight-flush
       :cards match
       :suit (suit top)
       :top-rank (rank top)})))

(defn- extract-royal-flush
  [cards]
  (let [top (-> cards
                match-flush
                match-straight
                first)]
    (when (= :A (rank top))
      {:classification :royal-flush})))

(def hand-fns
  [extract-royal-flush
   extract-straight-flush
   extract-four-of-a-kind
   extract-flush
   extract-straight
   extract-three-of-a-kind
   extract-two-pair
   extract-pair
   extract-high-card])

(defn score
  [hand]
  (some #(% hand) hand-fns))

;(defn beats?
;  [& _hands]
;  false)
