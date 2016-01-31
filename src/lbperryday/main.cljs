(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom nil))

(defn roll-dice! []
  (println "roll-dice! needs an implementation..."))

(defn main-view []
  [:center
   [:h1 "LBPERRYDAY!"]
   [:div
    {:id "dice-roll-area"
     :on-click (fn [e]
                 (roll-dice!))}
    [:svg
     {:width 100
      :height 100}
     [:rect
      {:width 74
       :height 74
       :fill "#DAD6CC"}]
     [:circle
      {:cx 37
       :cy 37
       :r 27
       :fill "#F8F1E5"}]
     [:g
       [:circle
        {:cx 37
         :cy 37
         :r 6
         :fill "#9B9993"}]]]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
