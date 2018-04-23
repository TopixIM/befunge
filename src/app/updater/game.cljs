
(ns app.updater.game (:require [app.schema :refer [settings]]))

(defn reset-cursor [db op-data sid op-id op-time]
  (-> db
      (update
       :game
       (fn [game] (-> game (assoc :cursor {:x 0, :y 0}) (assoc :direction :right))))
      (assoc :running true)))

(defn step [db op-data sid op-id op-time]
  (let [game (:game db)
        cursor (:cursor game)
        board (:board game)
        x (:x cursor)
        y (:y cursor)
        char (get board (str x "+" y))
        x-begin? (zero? x)
        x-end? (= (:x settings) (inc x))
        y-begin? (zero? y)
        y-end? (= (:y settings) (inc y))
        direction (case char
                    "v" :down
                    ">" :right
                    "<" :left
                    "^" :up
                    "?" (get [:up :down :left :right] (rand-int 4))
                    (:direction game))]
    (update
     db
     :game
     (fn [game]
       (-> game
           (update
            :cursor
            (fn [cursor]
              (case direction
                :left
                  (if x-begin? (assoc cursor :x (dec (:x settings))) (update cursor :x dec))
                :right (if x-end? (assoc cursor :x 0) (update cursor :x inc))
                :up
                  (if y-begin? (assoc cursor :y (dec (:y settings))) (update cursor :y dec))
                (if y-end? (assoc cursor :y 0) (update cursor :y inc)))))
           (assoc :direction direction))))))

(defn toggle-running [db op-data sid op-id op-time] (assoc db :running? op-data))

(defn write [db op-data session-id op-id op-time]
  (let [position (:position op-data), char (:char op-data)]
    (assoc-in db [:game :board position] char)))
