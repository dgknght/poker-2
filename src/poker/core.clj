(ns poker.core
  #_(:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(def rank-values
  (->> [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]
       (map-indexed (fn [i r] [r i]))
       (into {})))

(defn- of-a-kind
  [hand {:keys [count-of-kind]}]
  (->> hand
       (group-by first)
       (filter #(= count-of-kind (count (second %))))
       (sort-by (comp rank-values first) >)
       (map #(update-in % [1] set))
       first))

(defn- extract-pair
  [hand]
  (let [[rank cards] (of-a-kind hand {:count-of-kind 2})]
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
  (let [match (->> hand
                          (group-by first)
                          (filter #(= 2 (count (second %))))
                          (sort-by (comp rank-values first) >)
                          (map #(update-in % [1] set))
                          (take 2))]
    (when (= 2 (count match))
      {:classification :two-pair
       :ranks (map first match)
       :cards (map second match)
       :remaining-ranks (->> hand
                             (remove (-> match first second))
                             (remove (-> match second second))
                             (map first)
                             (sort-by rank-values >))})))

(defn- extract-three-of-a-kind
  [hand]
  (let [[rank cards] (of-a-kind hand {:count-of-kind 3})]
    (when rank
      {:classification :three-of-a-kind
       :rank rank
       :cards cards
       :remaining-ranks (->> hand
                             (remove cards)
                             (map first)
                             (sort-by rank-values >))})))

(defn- extract-high-card
  [hand]
  {:classification :high-card
   :remaining-ranks (->> hand
                         (map first)
                         (sort-by rank-values >))})

(def hand-fns
  [extract-three-of-a-kind
   extract-two-pair
   extract-pair
   extract-high-card])

(defn score
  [hand]
  (some #(% hand) hand-fns))

;(defn beats?
;  [& _hands]
;  false)
