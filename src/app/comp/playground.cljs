
(ns app.comp.playground
  (:require [respo.macros :refer [defcomp <> list-> action-> input div input button span]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-ui.core :as ui]
            [app.schema :as schema]
            [app.style :as style]
            [clojure.string :as string]
            [respo-md.comp.md :refer [comp-md comp-md-block]]
            [hsl.core :refer [hsl]]
            [app.schema :refer [settings]]))

(defcomp
 comp-stone
 (board x y)
 (input
  {:style {:width 20,
           :height 20,
           :background-color (hsl 120 40 68 0.2),
           :margin 1,
           :border :none,
           :outline :none,
           :text-align :center,
           :line-height "16px",
           :font-family ui/font-code,
           :font-size 14},
   :value (get board (str x "+" y)),
   :on-input (fn [e d! m!]
     (let [text (last (string/trim (:value e)))]
       (d! :game/write {:position (str x "+" y), :char text})))}))

(defcomp
 comp-playground
 (game)
 (let [board (:board game), cursor (:cursor game), running? (:running? game)]
   (div
    {:style (merge ui/flex ui/center)}
    (div
     {}
     (comp-md
      "Online playground for [Befunge](https://dorianbrown.github.io/befunge/) ([Befunge 介绍](https://coolshell.cn/articles/4458.html)). Currently only `< > ^ v ? @` are supported. Will add more later..."))
    (=< nil 16)
    (div
     {:style {:position :relative}}
     (div
      {:style {:position :absolute,
               :width (:size settings),
               :height (:size settings),
               :background-color (hsl 30 80 80),
               :left (+ 1 (* (+ 2 (:size settings)) (:x cursor))),
               :top (+ 1 (* (+ 2 (:size settings)) (:y cursor))),
               :z-index -1}})
     (list->
      {:style {}}
      (->> (range (:y settings))
           (map
            (fn [y]
              [y
               (list->
                {:style (merge ui/row)}
                (->> (range (:x settings)) (map (fn [x] [x (comp-stone board x y)]))))])))))
    (=< nil 8)
    (div
     {:style ui/row-parted}
     (div
      {:style ui/row}
      (button {:style ui/button, :on-click (action-> :game/step nil)} (<> "Step"))
      (=< 8 nil)
      (button
       {:style ui/button,
        :on-click (fn [e d! m!]
          (if running? (d! :game/toggle-running false) (d! :effect/run nil)))}
       (<> (if running? "Stop" "Run"))))
     (=< 120 nil)
     (button {:style ui/button, :on-click (action-> :game/reset nil)} (<> "Reset"))))))
