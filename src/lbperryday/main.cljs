(ns lbperryday.main
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [lbperryday.cards :as cards]
            [lbperryday.components :as c]
            [lbperryday.dice :as dice]
            [lbperryday.content :as content]
            [lbperryday.html-colors :as colors]))

(enable-console-print!)
(defn not-implemented [function-name]
  (println function-name "is not implemented!"))

(defn shuffle-cards []
  (let [queue #queue []]
    (apply conj queue (shuffle cards/cards))))

(def spiral-positions
  [
   ;; top row, left to right
   {:x 10 :y 60}
   {:x 135 :y 60}
   {:x 260 :y 60}
   {:x 385 :y 60}
   {:x 510 :y 60}
   {:x 635 :y 60}
   {:x 760 :y 60}
   ;; right column, top to bottom
   {:x 760 :y 130}
   {:x 760 :y 200}
   {:x 760 :y 270}
   {:x 760 :y 340}
   {:x 760 :y 410}
   {:x 760 :y 480}
   ;; bottom row, right to left
   {:x 760 :y 550}
   {:x 635 :y 550}
   {:x 510 :y 550}
   {:x 385 :y 550}
   {:x 260 :y 550}
   {:x 135 :y 550}
   ;; left column, bottom to top
   {:x 10 :y 550}
   {:x 10 :y 480}
   {:x 10 :y 410}
   {:x 10 :y 340}
   {:x 10 :y 270}
   ;; second row, right to left
   {:x 10 :y 200}
   {:x 135 :y 200}
   {:x 260 :y 200}
   {:x 385 :y 200}
   ;;
   {:x 510 :y 200}
   {:x 510 :y 270}
   {:x 510 :y 340}

   {:x 510 :y 410}
   {:x 385 :y 410}
   {:x 260 :y 410}

   {:x 260 :y 340}
   ])



(defn generate-spaces []
  (vec
    (let [n (count spiral-positions)
          colors (vec (take n (cycle (shuffle colors/space-colors))))]
      (for [i (range (count spiral-positions))]
        (assoc (get spiral-positions i) :color (get colors i))))))

(def initial-game-state {:current-dice (first dice/dice-specs)
                         :roll-history '()
                         :add-player-name nil
                         :players []
                         :player-cycle nil
                         :player-data nil
                         :draw-pile (shuffle-cards)
                         :discard-pile nil
                         :board-spaces (generate-spaces)
                         :booty-traps [{:low-x 260 :high-x 385
                                        :low-y 410 :high-y 480
                                        :audio-key :scream
                                        :trap-card (first cards/trap-cards)}]
                         :final-space-img (rand-nth c/space-images)
                         :show-card? false
                         :show-victory? false
                         :victor nil
                         :game-on? false})

(defonce app-state (atom initial-game-state))

(defn reset-game [_]
  (assoc initial-game-state :draw-pile (shuffle-cards)
                            :board-spaces (generate-spaces)))

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
    (assoc game-state :player-cycle (conj updated-queue current-player)
                      :show-card? false)))

(defn next-player! []
  (swap! app-state next-player))

(defn draw-card [game-state]
  (if-let [drawn-card (peek (:draw-pile game-state))]
    (let [updated-draw-pile (pop (:draw-pile game-state))
          updated-discard-pile (conj (:discard-pile game-state) drawn-card)]
      (assoc game-state :draw-pile updated-draw-pile
                        :discard-pile updated-discard-pile
                        :show-card? true))
    (assoc game-state :discard-pile (conj (:discard-pile game-state) cards/no-more-cards))))

(defn draw-card! []
  (swap! app-state draw-card))

(defn end-game [game-state name]
  (assoc game-state :players []
                    :victor name
                    :show-victory? true))

(defn end-game! [name]
  (.play (.getElementById js/document (get-in c/audio-snippets [:chief :name])))
  (swap! app-state end-game name))

(comment
  game-state {...
              :booty-traps [{:low-x 0 :high-x 0
                             :low-y 0 :high-y 0
                             :audio-key :scream
                             :trap-card {:title "" :body ""}      ;random trap card
                             }]
              ...})

(defn trap-at [x y]
  (let [traps (:booty-traps @app-state)]
    (first (filter (fn [{:keys [low-x high-x low-y high-y]}]
                     (and (< low-x x high-x)
                          (< low-y y high-y)))
                   traps))))

(defn display-booty-trap! [name x y]
  (let [trap (trap-at x y)]
    (.play (.getElementById js/document (get-in c/audio-snippets [(:audio-key trap) :name])))
    (println "TODO: remove trap from state"))
  (not-implemented "display-booty-trap!"))

(defn current-dice-transform [key]
  (get-in @app-state [:current-dice :transform key]))

(defn maybe-hidden [hide-switch]
  (if (hide-switch @app-state)
    " hidden"
    ""))

(defn hidden-during-game []
  (maybe-hidden :game-on?))

(defn hidden-during-end-game []
  (maybe-hidden :show-victory?))

(defn maybe-shown [show-switch]
  (if (show-switch @app-state)
    ""
    " hidden"))

(defn shown-during-game []
  (maybe-shown :game-on?))

(defn showing-help []
  (maybe-shown :show-help?))

(defn showing-end-game []
  (maybe-shown :show-victory?))

(defn showing-card []
  (maybe-shown :show-card?))

(defn toggle-help [game-state]
  (assoc game-state :show-help? (not (:show-help? game-state))))

(defn toggle-help! []
  (swap! app-state toggle-help))

(defn get-bcr [svg-root]
  (-> svg-root
      reagent/dom-node
      .getBoundingClientRect))

(def final-space-bounds {:low-x 260 :high-x 385
                         :low-y 340 :high-y 410})

(defn lbp-nirvana? [x y]
  (and (< (:low-x final-space-bounds) x (:high-x final-space-bounds))
       (< (:low-y final-space-bounds) y (:high-y final-space-bounds))))

(defn has-booty-trap? [x y]
  (trap-at x y))

(defn check-for-special-events [name]
  (fn [x y]
    (cond
      (lbp-nirvana? x y)
      (end-game! name)

      (has-booty-trap? x y)
      (display-booty-trap! name x y)

      :else
      nil)))

(defn move-name [player-data bcr x y]
  ;; Approximat offsets for clicking in the middle of a name
  (assoc player-data :x (- x 20 (.-left bcr)) :y (+ 8 (- y (.-top bcr)))))

(defn move-name! [svg-root game-state name]
  (let [player-data (get-in game-state [:player-data name])]
    (fn [x y]
      (let [bcr (get-bcr svg-root)
            updated-player-data (move-name player-data bcr x y)
            updated-game-state (assoc-in game-state [:player-data name] updated-player-data)]
        (reset! app-state updated-game-state)))))

(def board-dimensions {:width 900
                       :height 640})

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
    (for [space (:board-spaces @app-state)]
      (let [{:keys [x y color]} space]
        (c/board-space x y color)))
    (c/space-image (:final-space-img @app-state) (+ 27 (:low-x final-space-bounds)) (:low-y final-space-bounds) 70)
    (let [root (reagent/current-component)]
      (doall
        (map (fn [[name data]]
               (c/player-name {:on-drag (move-name! root @app-state name)
                               :on-start (fn [])
                               :on-end (check-for-special-events name)}
                              {:x (:x data) :y (:y data) :name name}))
             (:player-data @app-state))))]])

(defn embed-audio [{:keys [name source type]}]
  (c/audio-snippet name source type))

(defn embedded-snippets []
  (for [audio-key (keys c/audio-snippets)]
    (embed-audio (audio-key c/audio-snippets))))

(defn main-view []
  [:div
   (game-board)
   (embedded-snippets)
   [:div
    {:class "data-area"}
    [:center
     [:h1 "LBPErryDay!"]
     [:div
      {:id "gather-players"
       :class (hidden-during-game)}
      [:div
       {:id "player-inputs"}
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
       {:id "player-list"}
       (for [player (:players @app-state)]
         (do
           ^{:key player}
           [:div
            player]))]]

     [:div
      {:id "play-area"
       :class (shown-during-game)}
      [:div
       {:id "play-buttons"}
       [:div
        [:button
         {:id "btn-help"
          :on-click #(toggle-help!)}
         "?"]
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
         (if (:show-victory? @app-state)
           "End Game"
           "Give Up!")]]
       [:div
        {:id "help-area"
         :class (showing-help)}
        (content/help-text)]
       [:div
        {:id "end-game-area"
         :class (showing-end-game)}
        (content/end-game-text (:victor @app-state))]
       [:div
        {:id "short-instruction-area"
         :class (str (shown-during-game) (hidden-during-end-game))}
        (str (current-player @app-state) ": Roll, then Draw.")]
       (let [current-card (peek (:discard-pile @app-state))]
         [:div
          {:id "card-area"
           :class (str "card-area" (showing-card) (hidden-during-end-game))}
          [:h3
           (:title current-card)]
          (:body current-card)])]

      [:div
       {:class (hidden-during-end-game)}
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
             roll]))]]]]]])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent/render-component [main-view] app)))

(main)

(defn on-js-reload []
  (main))
