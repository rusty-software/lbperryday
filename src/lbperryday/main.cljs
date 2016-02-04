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

(defonce app-state (atom {:current-dice (first dice-specs)
                          :roll-history '()
                          :add-player-name nil
                          :players []
                          :game-on? false}))

(defn current-player [game-state]
  (first (:player-cycle game-state)))

(defn roll-history-row [roll]
  (let [current-player (or (current-player @app-state) "you")]
    (str current-player " just rolled a " (inc roll))))

(defn roll-dice [game-state]
  (let [roll (rand-int 6)]
    (assoc game-state :current-dice (get dice-specs roll)
                      :roll-history (take 3 (conj (:roll-history game-state) (roll-history-row roll))))))
(defn roll-dice! []
  (swap! app-state roll-dice))

(defn update-player-name [game-state name]
  (assoc game-state :add-player-name name))

(defn update-player-name! [name ]
  (swap! app-state update-player-name name))

(defn player-queue [players]
  (let [queue #queue []]
    (apply conj queue players)))

(defn initial-player-data-map [players]
  (loop [player-data {}
         players players
         y 20]
    (if (empty? players)
      player-data
      (recur (assoc player-data (first players) {:x 50 :y y}) (rest players) (+ y 20)))))

(defn initial-state [game-state]
  (assoc game-state :game-on? true
                    :player-cycle (player-queue (:players game-state))
                    :player-data (initial-player-data-map (:players game-state))))

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
                    :player-cycle nil
                    :player-data nil
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
    {:class "dice-svg side"
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

(defn get-bcr [svg-root]
  (-> svg-root
      reagent/dom-node
      .getBoundingClientRect))

(defn move-name [player-data bcr x y]
  (.log js/console bcr)
  (println "x" x "y" y)
  (println "left bcr" (.-left bcr) "top bcr" (.-top bcr))
  (assoc player-data :x (- x (.-left bcr)) :y (- y (.-top bcr))))

(defn move-name! [svg-root game-state name]
  (let [player-data (get-in game-state [:player-data name])]
    (fn [x y]
      (let [bcr (get-bcr svg-root)
            updated-player-data (move-name player-data bcr x y)
            updated-game-state (assoc-in game-state [:player-data name] updated-player-data)]
        (reset! app-state updated-game-state)))))

(def board-dimensions {:width 900
                       :height 600})

(def piece-positions
  [{:x 0 :y 60}
   {:x 125 :y 60}
   {:x 250 :y 60}
   {:x 375 :y 60}
   {:x 500 :y 60}
   {:x 625 :y 60}
   {:x 750 :y 60}
   ;; transition 1
   {:x 750 :y 130}
   ;; row 2, right to left
   {:x 750 :y 200}
   {:x 625 :y 200}
   {:x 500 :y 200}
   {:x 375 :y 200}
   {:x 250 :y 200}
   {:x 125 :y 200}
   {:x 0 :y 200}
   ;; transition
   {:x 0 :y 270}
   ;; row 3, left to right
   {:x 0 :y 340}
   {:x 125 :y 340}
   {:x 250 :y 340}
   {:x 375 :y 340}
   {:x 500 :y 340}
   {:x 625 :y 340}
   {:x 750 :y 340}
   ;; transition
   {:x 750 :y 410}
   ;; row 4, right to left
   {:x 750 :y 480}
   {:x 625 :y 480}
   {:x 500 :y 480}
   {:x 375 :y 480}
   {:x 250 :y 480}
   {:x 125 :y 480}
   {:x 0 :y 480}
   ])

(defn game-board []
  [:div
   {:class "board-area"}
   [:svg
    {:id "svg-box"
     :width (:width board-dimensions)
     :height (:height board-dimensions)
     :style {:border "0.5px solid black"}}
    [:rect
       {:x 0
        :y 0
        :width (:width board-dimensions)
        :height (:height board-dimensions)
        :fill "DarkSeaGreen"}]
    (for [i (range 31)]
      (let [{:keys [x y]} (get piece-positions i)]
        (c/board-space x y)))
    (let [root (reagent/current-component)]
      (doall
        (map (fn [[name data]]
               (c/player-name {:on-drag (move-name! root @app-state name)} {:x (:x data) :y (:y data) :name name}))
             (:player-data @app-state))))]])

(defn main-view []
  [:div
   (game-board)
   [:div
    {:class "data-area"}
    [:center
     [:h1 "LBPErryDay!"]
     [:div
      {:id "gather-players"
       :class (hidden-during-game)}
      [:div
       [:input
        {:id "txt-player-name"
         :style {:margin-right 2}
         :type "text"
         :value (:add-player-name @app-state)
         :on-change #(update-player-name! (-> % .-target .-value))}]
       [:button
        {:id "btn-add-player"
         :style {:margin-right 2}
         :on-click #(add-player!)}
        "Add Player"]
       [:button
        {:id "btn-start"
         :on-click #(start-game!)}
        "Start The Game!"]]
      [:div
       (for [player (:players @app-state)]
         (do
           ^{:key player}
           [:div
            player]))]]
     [:div
      {:id "play-area"
       :class (shown-during-game)}
      [:div
       {:id "buttons"}

       [:div
        [:button
         {:id "btn-draw-card"
          :on-click #(draw-card!)}
         "Draw Card"]
        [:button
         {:id "btn-end-turn"
          :on-click #(next-player!)}
         "End Turn"]
        [:button
         {:id "btn-end-game"
          :class "red-button"
          :on-click #(reset-game!)}
         "Give Up!"]]
       [:div
        (str (current-player @app-state) ", it's your turn.")]
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
        (for [roll (:roll-history @app-state)]
          (do
            ^{:key (str roll (rand-int 10000000))}
            [:div
             roll]))]
       ]]]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
