(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [lbperryday.html-colors :as colors]
            [lbperryday.components :as c]))

(enable-console-print!)
(defn not-implemented [function-name]
  (println function-name "is not implemented!"))

(def dice-specs
  [{:color (get colors/dice-colors 0)
    :dots [{:x 37 :y 37}]
    :val 1
    :transform {:rx 145 :ry -45 :rz 0}}
   {:color (get colors/dice-colors 1)
    :dots [{:x 14.25 :y 57.5}
           {:x 56.75 :y 16.5}]
    :val 2
    :transform {:rx -45 :ry 50 :rz 0}}
   {:color (get colors/dice-colors 2)
    :dots [{:x 14.25 :y 57.5}
           {:x 37 :y 37}
           {:x 56.75 :y 16.5}]
    :val 3
    :transform {:rx -45 :ry 225 :rz -90}}
   {:color (get colors/dice-colors 3)
    :dots [{:x 14.27 :y 16.5}
           {:x 14.25 :y 57.5}
           {:x 56.75 :y 16.5}
           {:x 56.75 :y 57.5}]
    :val 4
    :transform {:rx -45 :ry 50 :rz 90}}
   {:color (get colors/dice-colors 4)
    :dots [{:x 14.27 :y 16.5}
           {:x 14.25 :y 57.5}
           {:x 37 :y 37}
           {:x 56.75 :y 16.5}
           {:x 56.75 :y 57.5}]
    :val 5
    :transform {:rx 50 :ry 0 :rz 50}}
   {:color (get colors/dice-colors 5)
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
   :players ["" "" ""]
   :current-player ""})

(defonce app-state (atom {:current-dice (first dice-specs)
                          :roll-history []
                          :add-player-name nil
                          :players []
                          :game-on? false}))

(defn current-player [game-state]
  (first (:player-cycle game-state)))

(defn roll-history-row [roll]
  (let [current-player (or (current-player @app-state) "you")]
    (str current-player " just rolled a " (inc roll) ".")))

(defn roll-dice [game-state]
  (let [roll (rand-int 6)]
    (assoc game-state :current-dice (get dice-specs roll)
                      :roll-history (conj (:roll-history game-state) (roll-history-row roll)))))
(defn roll-dice! []
  (swap! app-state roll-dice))

(defn update-player-name [game-state name]
  (assoc game-state :add-player-name name))

(defn update-player-name! [name ]
  (swap! app-state update-player-name name))

(defn player-queue [players]
  (let [queue #queue []]
    (apply conj queue players)))

(defn initial-state [game-state]
  (assoc game-state :game-on? true
                    :player-cycle (player-queue (:players game-state))))

(defn start-game! []
  (swap! app-state initial-state))

(defn add-player [game-state]
  (assoc game-state :players (conj (:players game-state) (:add-player-name game-state))
                    :add-player-name nil))

(defn add-player! []
  (when-not (str/blank? (:add-player-name @app-state))
    (swap! app-state add-player)
    (when (= 6 (count (:players @app-state)))
      (start-game!))))

(defn reset-game [game-state]
  (assoc game-state :current-dice (first dice-specs)
                    :roll-history []
                    :add-player-name nil
                    :players []
                    :game-on? false))

(defn reset-game! []
  (swap! app-state reset-game))

(defn next-player [game-state]
  (let [current-player (peek (:player-cycle game-state))
        updated-queue (pop (:player-cycle game-state))]
    (assoc game-state :player-cycle (conj updated-queue current-player))))

(defn next-player! []
  (swap! app-state next-player))

(defn draw-card! []
  (not-implemented "draw-card!"))

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

(def board-dimensions {:width 700
                       :height 420})

(defn move-name [svg-root n]
  (not-implemented "move-name"))

(defn game-board []
  [:div
   {:id "game-board-area"}
   [:svg
    {:view-box (str "0 0 " (:width board-dimensions) " " (:height board-dimensions))
     :width (:width board-dimensions)
     :height (:height board-dimensions)}
    [:rect
     {:x 0
      :y 0
      :width (:width board-dimensions)
      :height (:height board-dimensions)
      :stroke "Black"
      :stroke-width "0.5"
      :fill "DarkSeaGreen"}]
    (c/player-name {:on-drag (move-name nil nil)} {:x 50 :y 50 :name "Player 1"})
    (c/player-name {:on-drag (move-name nil nil)} {:x 120 :y 120 :name "Player 2"})]])

(defn main-view []
  [:center
   [:h1 "LBPERRYDAY!"]
   [:div
    {:id "gather-players"
     :class (hidden-during-game)}
    [:div
     {:class "row"}
     [:input
      {:id "txt-player-name"
       :style {:margin-right 2}
       :type "text"
       :value (:add-player-name @app-state)
       :on-change #(update-player-name! (-> % .-target .-value))}]
     [:button
      {:id "btn-add-player"
       :style {:margin-right 2}
       :class "btn btn-primary"
       :on-click #(add-player!)}
      "Add Player"]
     [:button
      {:id "btn-start"
       :class (str "btn btn-success")
       :on-click #(start-game!)}
      "Start The Game!"]]
    [:div
     [:text "Players List"]
     (for [player (:players @app-state)]
       (do
         ^{:key player}
         [:div
          {:class "row small"}
          player]))]]

   [:div
    {:id "play-area"
     :class (shown-during-game)}
    [:div
     {:id "buttons"}
     [:div
      {:class "row"}
      (str (current-player @app-state) ", it's your turn.")]
     [:div
      {:class "row"}
      [:button
       {:id "btn-draw-card"
        :style {:margin-right 2}
        :class "btn btn-success"
        :on-click #(draw-card!)}
       "Draw Card"]
      [:button
       {:id "btn-end-turn"
        :style {:margin-right 2}
        :class "btn btn-warning"
        :on-click #(next-player!)}
       "End Turn"]
      [:button
       {:id "btn-end-game"
        :class "btn btn-danger"
        :on-click #(reset-game!)}
       "End Game Without Winner!"]]]
    (game-board)]

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
          roll]))]
    [:div
     {:id "card-display-area"}]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
