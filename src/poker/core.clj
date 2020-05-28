(ns poker.core
  (:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(def rank-values
  (->> [:2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K :A]
       (map-indexed (fn [i r] [r i]))
       (into {})))

(defn- extract-pair
  [hand]
  (let [[rank cards] (->> hand
                          (group-by first)
                          (filter #(<= 2 (count (second %))))
                          (sort-by (comp rank-values first) >)
                          (map #(update-in % [1] set))
                          first)]
    (when rank
      {:classification :pair
       :rank rank
       :cards cards
       :ranks (->> hand
                   (remove cards)
                   (map first)
                   (sort-by rank-values >))})))

(defn- extract-high-card
  [hand]
  {:classification :high-card
   :ranks (->> hand
               (map first)
               (sort-by rank-values >))})

(def hand-fns
  [extract-pair
   extract-high-card])

(defn score
  [hand]
  (some #(% hand) hand-fns))

;(defn beats?
;  [& _hands]
;  false)
