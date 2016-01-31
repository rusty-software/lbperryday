(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom nil))

(defn roll-dice! []
  (println "roll-dice! needs an implementation..."))

(defn dice-side [side-color dots]
  [:svg
     {:width 100
      :height 100}
     [:rect
      {:width 74
       :height 74
       :fill side-color}]
     #_[:circle
      {:cx 37
       :cy 37
       :r 27
       :fill "#F8F1E5"}]
   (for [dot dots]
     ^{:key dot}
     [:circle
      {:cx (:x dot)
       :cy (:y dot)
       :r 6
       :fill "snow"}])])

(defn main-view []
  [:center
   [:h1 "LBPERRYDAY!"]
   [:div
    {:id "dice-roll-area"
     :on-click (fn [e]
                 (roll-dice!))}
    (dice-side "cadetblue"
               [{:x 37 :y 37}])
    (dice-side "coral"
               [{:x 14.25 :y 57.5}
                {:x 56.75 :y 16.5}])
    (dice-side "cornflowerblue"
               [{:x 14.25 :y 57.5}
                {:x 37 :y 37}
                {:x 56.75 :y 16.5}])
    (dice-side "darkkhaki"
               [{:x 14.27 :y 16.5}
                {:x 14.25 :y 57.5}
                {:x 56.75 :y 16.5}
                {:x 56.75 :y 57.5}])
    (dice-side "darkslateblue"
               [{:x 14.27 :y 16.5}
                {:x 14.25 :y 57.5}
                {:x 37 :y 37}
                {:x 56.75 :y 16.5}
                {:x 56.75 :y 57.5}])
    (dice-side "brown"
               [{:x 14.27 :y 16.5}
                {:x 14.27 :y 37}
                {:x 14.25 :y 57.5}
                {:x 56.75 :y 16.5}
                {:x 56.75 :y 37}
                {:x 56.75 :y 57.5}])]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
