(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [lbperryday.cards :as cards]
            [lbperryday.components :as c]
            [lbperryday.dice :as dice]))

(enable-console-print!)
(defn not-implemented [function-name]
  (println function-name "is not implemented!"))

(defn shuffle-cards []
  (let [queue #queue []]
    (apply conj queue (shuffle cards/cards))))

(def initial-game-state {:current-dice (first dice/dice-specs)
                          :roll-history '()
                          :add-player-name nil
                          :players []
                          :player-cycle nil
                          :player-data nil
                          :draw-pile (shuffle-cards)
                          :discard-pile nil
                          :game-on? false})

(defonce app-state (atom initial-game-state))

(defn reset-game [_]
  (assoc initial-game-state :draw-pile (shuffle-cards)))

(defn reset-game! []
  (swap! app-state reset-game))

(defn current-player [game-state]
  (first (:player-cycle game-state)))

(defn roll-history-row [roll]
  (let [current-player (or (current-player @app-state) "you")]
    (str current-player " just rolled a " (inc roll))))

(defn roll-dice [game-state]
  (let [roll (rand-int 6)]
    (assoc game-state :current-dice (get dice/dice-specs roll)
                      :roll-history (take 3 (conj (:roll-history game-state) (roll-history-row roll))))))
(defn roll-dice! []
  (swap! app-state roll-dice))

(defn update-player-name [game-state name]
  (assoc game-state :add-player-name name))

(defn update-player-name! [name]
  (swap! app-state update-player-name name))

(defn player-queue [players]
  (let [queue #queue []]
    (apply conj queue players)))

(defn initial-player-data-map [players]
  (loop [player-data {}
         players players
         x 50
         y 20]
    (if (empty? players)
      player-data
      (recur (assoc player-data (first players) {:x x :y y}) (rest players) (+ x 50) y))))

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

(defn next-player [game-state]
  (let [current-player (peek (:player-cycle game-state))
        updated-queue (pop (:player-cycle game-state))]
    (assoc game-state :player-cycle (conj updated-queue current-player))))

(defn next-player! []
  (swap! app-state next-player))

(defn draw-card [game-state]
  (let [drawn-card (peek (:draw-pile game-state))
        updated-draw-pile (pop (:draw-pile game-state))
        updated-discard-pile (conj (:discard-pile game-state) drawn-card)]
    (assoc game-state :draw-pile updated-draw-pile
                      :discard-pile updated-discard-pile)))

(defn draw-card! []
  (swap! app-state draw-card))

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

(defn get-bcr [svg-root]
  (-> svg-root
      reagent/dom-node
      .getBoundingClientRect))

(defn move-name [player-data bcr x y]
  (assoc player-data :x (- x (.-left bcr)) :y (- y (.-top bcr))))

(defn move-name! [svg-root game-state name]
  (let [player-data (get-in game-state [:player-data name])]
    (fn [x y]
      (let [bcr (get-bcr svg-root)
            updated-player-data (move-name player-data bcr x y)
            updated-game-state (assoc-in game-state [:player-data name] updated-player-data)]
        (reset! app-state updated-game-state)))))

(def board-dimensions {:width 900
                       :height 640})

(defn generate-spiral-positions []
  (vec
    (concat
      (for [i (range 6)]
        {:x (+ 10 (* i 125))
         :y 60
         #_#_:drop-shadow "blurFilterBottom"})
      (for [i (range 7)]
        {:x 760
         :y (+ 60 (* i 70))
         #_#_:drop-shadow "blurFilterRight"})
      (for [i (range 6 0 -1)]
        {:x (+ 10 (* i 125))
         :y 550
         #_#_:drop-shadow "blurFilterBottom"})
      (for [i (range 6 1 -1)]
        {:x 10
         :y (+ 130 (* i 70))
         #_#_:drop-shadow "blurFilterSurround"})
      (for [i (range 4)]
        {:x (+ 10 (* i 125))
         :y 200
         #_#_:drop-shadow "blurFilterSurround"})
      (for [i (range 1 4)]
        {:x 510
         :y (+ 130 (* i 70))
         #_#_:drop-shadow "blurFilterSurround"})
      (for [i (range 4 1 -1)]
        {:x (+ 10 (* i 125))
         :y 410
         #_#_:drop-shadow "blurFilterSurround"})
      [{:x 260
        :y 340
        #_#_:drop-shadow "blurFilterSurround"}])))

(def bottom-right-drop-shadow
  (str "<filter id=\"blurFilterBottomRight\" x=\"-10\" y=\"-10\" width=\"125\" height=\"70\" >"
                    "  <feOffset in=\"SourceAlpha\" dx=\"3\" dy=\"3\" result=\"offset2\" />"
                    "  <feGaussianBlur in=\"offset2\" stdDeviation=\"3\" result=\"blur2\" />"
                    "  <feMerge>"
                    "    <feMergeNode in=\"blur2\" />"
                    "    <feMergeNode in=\"SourceGraphic\" />"
                    "  </feMerge>"
                    "</filter>"))

(def bottom-drop-shadow
  (str "<filter id=\"blurFilterBottom\" x=\"0\" y=\"0\" width=\"122\" height=\"70\">"
       "  <feOffset in=\"SourceAlpha\" dx=\"0\" dy=\"0\" result=\"offsetBottom\" />"
       "  <feGaussianBlur in=\"offsetBottom\" stdDeviation=\"3\" result=\"blurBottom\" />"
       "  <feMerge>"
       "    <feMergeNode in=\"blurBottom\" />"
       "    <feMergeNode in=\"SourceGraphic\" />"
       "  </feMerge>"
       "</filter>"))

(def right-drop-shadow
  (str "<filter id=\"blurFilterRight\" x=\"0\" y=\"0\" width=\"125\" height=\"70\">"
       "  <feOffset in=\"SourceAlpha\" dx=\"3\" dy=\"0\" result=\"offsetRight\" />"
       "  <feGaussianBlur in=\"offsetRight\" stdDeviation=\"3\" result=\"blurRight\" />"
       "  <feMerge>"
       "    <feMergeNode in=\"blurRight\" />"
       "    <feMergeNode in=\"SourceGraphic\" />"
       "  </feMerge>"
       "</filter>"))

(def surround-drop-shadow
  (str "<filter id=\"blurFilterSurround\" x=\"-1\" y=\"-1\" width=\"126\" height=\"71\">"
       "  <feOffset in=\"SourceAlpha\" dx=\"1\" dy=\"1\" result=\"offsetSurround\" />"
       "  <feGaussianBlur in=\"offsetRight\" stdDeviation=\"3\" result=\"blurSurround\" />"
       "  <feMerge>"
       "    <feMergeNode in=\"blurSurround\" />"
       "    <feMergeNode in=\"SourceGraphic\" />"
       "  </feMerge>"
       "</filter>"))

(defn random-unused-meeple []
  (first (shuffle c/meeples)))

(defn game-board []
  [:div
   {:class "board-area"}
   [:svg
    {:id "svg-box"
     :width (:width board-dimensions)
     :height (:height board-dimensions)
     :style {:border "0.5px solid black"}}
    [:defs
     {:dangerouslySetInnerHTML
      {:__html (str bottom-drop-shadow
                    " "
                    bottom-drop-shadow
                    " "
                    right-drop-shadow
                    " "
                    surround-drop-shadow)}}]
    [:rect
       {:x 0
        :y 0
        :width (:width board-dimensions)
        :height (:height board-dimensions)
        :fill "DarkSeaGreen"}]
    (let [positions (generate-spiral-positions)]
      (for [i (range (count positions))]
        (let [{:keys [x y drop-shadow]} (get positions i)]
          (c/board-space x y drop-shadow))))
    (let [root (reagent/current-component)]
      (doall
        (map (fn [[name data]]
               (let [icon (random-unused-meeple)]
                 (c/meeple-image (:x data) (:y data) (random-unused-meeple) {:on-drag (move-name! root @app-state name)}))
               #_(c/player-name {:on-drag (move-name! root @app-state name)} {:x (:x data) :y (:y data) :name name}))
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
       (let [current-card (peek (:discard-pile @app-state))]
         [:div
          {:id    "card-area"
           :class "card-area"}
          [:h3
           (:title current-card)]
          (:body current-card)])]

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
         (for [side-spec dice/dice-specs]
           (dice/dice-side side-spec))]]
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
