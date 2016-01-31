(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def dice-specs
  [{:color "cadetblue"
    :dots [{:x 37 :y 37}]
    :val 1
    :transform {:rx 145 :ry -45 :rz 0}}
   {:color "coral"
    :dots [{:x 14.25 :y 57.5}
           {:x 56.75 :y 16.5}]
    :val 2
    :transform {:rx -45 :ry 50 :rz 0}}
   {:color "cornflowerblue"
    :dots [{:x 14.25 :y 57.5}
           {:x 37 :y 37}
           {:x 56.75 :y 16.5}]
    :val 3
    :transform {:rx -45 :ry 225 :rz -90}}
   {:color "darkkhaki"
    :dots [{:x 14.27 :y 16.5}
           {:x 14.25 :y 57.5}
           {:x 56.75 :y 16.5}
           {:x 56.75 :y 57.5}]
    :val 4
    :transform {:rx -45 :ry 50 :rz 90}}
   {:color "darkslateblue"
    :dots [{:x 14.27 :y 16.5}
           {:x 14.25 :y 57.5}
           {:x 37 :y 37}
           {:x 56.75 :y 16.5}
           {:x 56.75 :y 57.5}]
    :val 5
    :transform {:rx 50 :ry 0 :rz 50}}
   {:color "brown"
    :dots [{:x 14.27 :y 16.5}
           {:x 14.27 :y 37}
           {:x 14.25 :y 57.5}
           {:x 56.75 :y 16.5}
           {:x 56.75 :y 37}
           {:x 56.75 :y 57.5}]
    :val 6
    :transform {:rx 45 :ry 180 :rz -45}}])

(defonce app-state (atom (first dice-specs)))

(defn roll-dice! []
  (let [roll (rand-int 6)]
    (reset! app-state (assoc @app-state :current-dice (get dice-specs roll)))))

(defn current-dice-transform [key]
  (get-in @app-state [:current-dice :transform key]))

(defn dice-side
  ([side-color dots]
   ^{:key side-color}
   [:svg
    {:class "side"
     :width 100
     :height 100}
    [:rect
     {:width 74
      :height 74
      :fill side-color}]
    (for [dot dots]
      ^{:key dot}
      [:circle
       {:cx (:x dot)
        :cy (:y dot)
        :r 6
        :fill "snow"}])])
  ([side-spec]
   (dice-side (:color side-spec) (:dots side-spec))))

(defn main-view []
  [:center
   [:h1 "LBPERRYDAY!"]
   #_[:div
    {:id "dice-side-display"}
    (for [side-spec (vals dice-specs)]
      (dice-side side-spec))]

   [:div
     {:id "dice-roll-area"
      :on-click (fn [e]
                  (roll-dice!))}
     [:div
      {:id "dice-cube"
       :style {:transform (str " rotateX("
                               (current-dice-transform :rx)
                               "deg) rotateY("
                               (current-dice-transform :ry)
                               "deg) rotateZ("
                               (current-dice-transform :rz)
                               "deg)")}}
      (for [side-spec dice-specs]
        (dice-side side-spec))]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
