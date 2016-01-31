(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom nil))

(defn roll-dice! []
  (println "roll-dice! needs an implementation..."))

(def dice-sides {:one {:color "cadetblue" :dots [{:x 37 :y 37}]}
                 :two {:color "coral" :dots [{:x 14.25 :y 57.5}
                                           {:x 56.75 :y 16.5}]}
                 :three {:color "cornflowerblue" :dots [{:x 14.25 :y 57.5}
                                                        {:x 37 :y 37}
                                                        {:x 56.75 :y 16.5}]}
                 :four {:color "darkkhaki" :dots [{:x 14.27 :y 16.5}
                                                  {:x 14.25 :y 57.5}
                                                  {:x 56.75 :y 16.5}
                                                  {:x 56.75 :y 57.5}]}
                 :five {:color "darkslateblue" :dots [{:x 14.27 :y 16.5}
                                                      {:x 14.25 :y 57.5}
                                                      {:x 37 :y 37}
                                                      {:x 56.75 :y 16.5}
                                                      {:x 56.75 :y 57.5}]}
                 :six {:color "brown" :dots [{:x 14.27 :y 16.5}
                                             {:x 14.27 :y 37}
                                             {:x 14.25 :y 57.5}
                                             {:x 56.75 :y 16.5}
                                             {:x 56.75 :y 37}
                                             {:x 56.75 :y 57.5}]}})

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
    (for [side-spec (vals dice-sides)]
      (dice-side side-spec))]

   [:div
     {:id "dice-roll-area"
      :on-click (fn [e]
                  (roll-dice!))}
     [:div
      {:id "dice-cube"
       :style {:transform (str " rotateX(" 145 "deg) rotateY(" -45 "deg) rotateZ(" 0 "deg)")}}
      (for [side-spec (vals dice-sides)]
        (dice-side side-spec))]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
