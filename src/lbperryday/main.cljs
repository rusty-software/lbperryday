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

(comment
  :app-state-sample
  {:current-dice {:color ""
                  :dots [{:x 0 :y 0}]
                  :val 1
                  :transform {:rx 0 :ry 0 :rz 0}}
   :roll-history []
   :game-on? true
   :players {:player1 ""
             :player2 ""
             :player3 ""
             :player4 ""
             :player5 ""
             :player6 ""}
   :current-player ""})

(defonce app-state (atom {:current-dice (first dice-specs)
                          :roll-history []
                          :players {:player1 nil
                                    :player2 nil
                                    :player3 nil
                                    :player4 nil
                                    :player5 nil
                                    :player6 nil}
                          :current-player nil
                          :game-on? false}))

(defn roll-history-row [roll]
  (let [current-player (or (:current-player @app-state) "you")]
    (str current-player " just rolled a " (inc roll) ".")))

(defn roll-dice [game-state]
  (let [roll (rand-int 6)]
    (assoc game-state :current-dice (get dice-specs roll)
                      :roll-history (conj (:roll-history game-state) (roll-history-row roll)))))
(defn roll-dice! []
  (swap! app-state roll-dice))

(defn set-player [game-state n name]
  (let [player-key (keyword (str "player" n))]
    (assoc-in game-state [:players player-key] name)))

(defn set-player! [n name]
  (swap! app-state set-player n name))

(defn initial-state [game-state]
  (assoc game-state :game-on? true
                    :current-player (get-in game-state [:players :player1])))

(defn start-game! []
  (swap! app-state initial-state))

(defn reset-game [game-state]
  (assoc game-state :current-dice (first dice-specs)
                    :roll-history []
                    :players {:player1 nil
                              :player2 nil
                              :player3 nil
                              :player4 nil
                              :player5 nil
                              :player6 nil}
                    :current-player nil
                    :game-on? false))

(defn reset-game! []
  (swap! app-state reset-game))

(defn current-dice-transform [key]
  (get-in @app-state [:current-dice :transform key]))

(defn hidden-during-game []
  (if (:game-on? @app-state)
    " hidden"
    ""))

(defn shown-during-game []
  (if (:game-on? @app-state)
    ""
    " hidden"))

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

(defn player-input-row [n]
  ^{:key (str "player" n)}
  [:div
   {:class "row"}
   [:div
    {:class "col-md-2 col-md-offset-5"}
    [:label
     {:for (str "txt-player" n)}
     (str "Player " n ": ")]
    [:input
     {:id (str "txt-player" n)
      :type "text"
      :on-change #(set-player! n (-> % .-target .-value))}]]])

(defn main-view []
  [:center
   [:h1 "LBPERRYDAY!"]
   [:button
    {:id "btn-get-players"
     :class (str "btn btn-primary" (hidden-during-game))
     :data-toggle "collapse"
     :data-target "#players"}
    "Player Names"]
   [:div
    {:id "players"
     :class (str "collapse" (hidden-during-game))}
    [:p "Please enter between two and six player names in the boxes below.  Enter them in playing order.  Don't be dicks about it."]
    (for [i (range 1 7)]
      (player-input-row i))
    [:button
     {:id "btn-start"
      :class (str "btn btn-success")
      :on-click #(start-game!)}
     "Start The Game!"]]
   [:div
    {:id "buttons"
     :class (shown-during-game)}
    [:button
     {:id "btn-end-game"
      :class "btn btn-danger"
      :on-click #(reset-game!)}
     "Punt On This Shitty Game!"]
    ]

   [:div
    [:div
     {:id "dice-roll-area"
      :on-click #(roll-dice!)}
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
        (dice-side side-spec))]]
    [:div
     {:id "dice-result"}
     (for [roll (reverse (:roll-history @app-state))]
       (do
         ^{:key (str roll (rand-int 10000000))}
         [:div
          {:class "row small"}
          roll]))]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
