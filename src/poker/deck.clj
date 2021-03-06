(ns poker.deck
  #_(:require [clojure.pprint :refer [pprint]])
  (:gen-class))

(def suits
  #{:hearts
    :diamonds
    :spades
    :clubs})

(def ranks
  [:A :2 :3 :4 :5 :6 :7 :8 :9 :10 :J :Q :K])

(defn deck
  ([] (deck {}))
  ([{:keys [shuffle?] :or {shuffle? false}}]
   (let [shuffle-fn (if shuffle? shuffle identity)]
     (->> suits
          (mapcat #(interleave ranks
                               (repeat %)))
          (partition 2)
          shuffle-fn
          (into '())))))

(defn deal
  "Accepts a game contain a deck and any number of hands and
  deals a specified number of cards to each hand"
  ([game] (deal game {}))
  ([game {:keys [rounds] :or {rounds 1}}]
   (->> (range rounds)
        (mapcat (fn [round]
                  (map #(vector % round)
                       (range (count (:hands game))))))
        (reduce (fn [g [index _]]
                  (-> g
                      (update-in [:hands index] #(conj % (peek (:deck g))))
                      (update-in [:deck] pop)))
                game))))
