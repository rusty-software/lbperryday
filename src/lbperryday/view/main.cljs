(ns lbperryday.view.main
  (:require [clojure.string :as str]
            [reagent.core :as reagent]
            [lbperryday.view.dice :as dice]
            [lbperryday.view.components :as components]
            [lbperryday.model :as model]))


(defn display-booty-trap! [name x y]
  (let [trap (model/trap-at x y)]
    (.play (.getElementById js/document (get-in components/audio-snippets [(get-in trap [:trap-card :audio]) :name])))
    (swap! model/app-state model/spring-trap trap)))

(defn current-dice-transform [key]
  (get-in @model/app-state [:current-dice :transform key]))

(defn maybe-hidden [hide-switch]
  (if (hide-switch @model/app-state)
    " hidden"
    ""))

(defn hidden-during-game []
  (maybe-hidden :game-on?))

(defn maybe-shown [show-switch]
  (if (show-switch @model/app-state)
    ""
    " hidden"))

(defn shown-during-game []
  (maybe-shown :game-on?))

(defn showing-card []
  (maybe-shown :show-card?))

(def final-space-bounds {:low-x 260 :high-x 385
                         :low-y 340 :high-y 410})

(defn lbp-nirvana? [x y]
  (and (< (:low-x final-space-bounds) x (:high-x final-space-bounds))
       (< (:low-y final-space-bounds) y (:high-y final-space-bounds))))

(defn has-booty-trap? [x y]
  (model/trap-at x y))

(defn check-for-special-events [name]
  (fn [x y]
    (cond
      (lbp-nirvana? x y)
      (model/end-game! name)

      (has-booty-trap? x y)
      (display-booty-trap! name x y)

      :else
      nil)))

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
    (for [space (:board-spaces @model/app-state)]
      (let [{:keys [x y color]} space]
        (components/board-space x y color)))
    (components/space-image (:final-space-img @model/app-state) (+ 27 (:low-x final-space-bounds)) (:low-y final-space-bounds) 70)
    (let [root (reagent/current-component)]
      (doall
        (map (fn [[name data]]
               (components/player-name {:on-drag (model/move-name! root @model/app-state name)
                               :on-start (fn [])
                               :on-end (check-for-special-events name)}
                              {:x (:x data) :y (:y data) :name name}))
             (:player-data @model/app-state))))
    (let [root (reagent/current-component)]
      (doall
        (map (fn [[name data]]
               (components/loser-dot {:on-drag (model/move-dot! root @model/app-state name)
                             :on-start (fn [])
                             :on-end (fn [])}
                            {:x (:x data) :y (:y data) :name name}))
             (:dot-data @model/app-state))))]])

(defn embed-audio [{:keys [name source type]}]
  (components/audio-snippet name source type))

(defn embedded-snippets []
  (for [audio-key (keys components/audio-snippets)]
    (embed-audio (audio-key components/audio-snippets))))

(defn play-area []
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
         :value (:add-player-name @model/app-state)
         :on-change #(model/update-player-name! (-> % .-target .-value))}]
       [:button
        {:id "btn-add-player"
         :style {:margin-right 2}
         :on-click #(model/add-player!)}
        "Add Player"]
       [:button
        {:id "btn-start"
         :on-click #(model/start-game!)}
        "Start The Game!"]]
      [:div
       {:id "player-list"}
       (for [player (:players @model/app-state)]
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
         {:id "btn-draw-card"
          :on-click #(model/draw-card!)}
         "Draw Card"]
        [:button
         {:id "btn-end-turn"
          :on-click #(model/next-player!)}
         "End Turn"]
        [:button
         {:id "btn-end-game"
          :class "red-button"
          :on-click #(model/reset-game!)}
         (if (:show-victory? @model/app-state)
           "End Game"
           "Give Up!")]]
       [:div
        {:id "short-instruction-area"
         :class (str (shown-during-game))}
        (str (model/current-player @model/app-state) ": Roll, then Draw.")]
       (let [current-card (peek (:discard-pile @model/app-state))]
         [:div
          {:id "card-area"
           :class (str "card-area" (showing-card) )}
          [:h3
           (:title current-card)]
          (when (:body current-card)
            (str/replace (:body current-card) #"%s" (:victor @model/app-state)))])]

      [:div
       [:div
        {:id "dice-roll-area"
         :on-click #(model/roll-dice!)}
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
        (for [roll (:roll-history @model/app-state)]
          (do
            ^{:key (str roll (rand-int 10000000))}
            [:div
             roll]))]]]]]])

