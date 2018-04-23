
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo-ui.colors :as colors]
            [respo.macros :refer [defcomp <> div span action-> button]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.comp.space :refer [=<]]
            [app.comp.navigation :refer [comp-navigation]]
            [app.comp.profile :refer [comp-profile]]
            [app.comp.login :refer [comp-login]]
            [respo-message.comp.msg-list :refer [comp-msg-list]]
            [app.comp.reel :refer [comp-reel]]
            [app.schema :refer [dev?]]
            [app.comp.playground :refer [comp-playground]]))

(defcomp
 comp-offline
 ()
 (div
  {:style (merge ui/global ui/fullscreen ui/center)}
  (span
   {:style {:cursor :pointer}, :on-click (action-> :effect/connect nil)}
   (<>
    "Socket broken! Click to retry."
    {:font-family ui/font-fancy, :font-weight 100, :font-size 32}))))

(defcomp
 comp-status-color
 (color)
 (div
  {:style {:width 16,
           :height 16,
           :position :absolute,
           :top 60,
           :right 8,
           :background-color color,
           :border-radius "8px",
           :opacity 0.8}}))

(defcomp
 comp-container
 (states store)
 (let [state (:data states), session (:session store)]
   (if (nil? store)
     (comp-offline)
     (div
      {:style (merge ui/global ui/fullscreen ui/row)}
      (if (:logged-in? store)
        (let [router (:router store)]
          (case (:name router)
            :profile (comp-profile (:user store) (:data router))
            :home (comp-playground (:game store) (:running? store))
            {}))
        (comp-login states))
      (comp-navigation (:logged-in? store) (:count store))
      (comp-status-color (:color store))
      (comp-msg-list (get-in store [:session :notifications]) :session/remove-notification)
      (when dev? (comp-inspect "Store" store {:bottom 0, :left 0, :max-width "100%"}))
      (when dev? (comp-reel (:reel-length store) {:left 0, :bottom 30}))))))

(def style-body {:padding "8px 16px"})
