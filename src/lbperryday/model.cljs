(ns lbperryday.model
  (:require [reagent.core :as reagent]
            [lbperryday.view.components :as components]
            [lbperryday.view.dice :as dice]
            [lbperryday.content.cards :as cards]
            [lbperryday.content.html-colors :as colors]
            [clojure.string :as str]))

(defn shuffle-cards []
  (let [queue #queue []]
    (apply conj queue (shuffle cards/cards))))

(def spiral-positions
  [
   ;; TODO: fill in low-x, high-x, low-y, high-y
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
        (let [space (get spiral-positions i)]
          (assoc space :i i
                       :color (get colors i)
                       :low-x (:x space)
                       :high-x (+ 125 (:x space))
                       :low-y (:y space)
                       :high-y (+ 70 (:y space))))))))

(defn generate-booty-traps [spaces]
  (vec
    (let [traps (vec (take (count spaces) (shuffle cards/trap-cards)))]
      (for [i (range (count spaces))]
        (let [space (get spaces i)
              trap (get traps i)]
          {:low-x (:low-x space)
           :high-x (:high-x space)
           :low-y (:low-y space)
           :high-y (:high-y space)
           :trap-card trap})))))

(defn initialize-game-state []
  (let [spaces (generate-spaces)
        traps (generate-booty-traps (vec (take (min 6 (count cards/trap-cards)) (shuffle (butlast spaces)))))]
    {:current-dice (first dice/dice-specs)
     :history '()
     :roll-history '()
     :add-player-name nil
     :players []
     :player-cycle nil
     :player-data nil
     :dot-data nil
     :draw-pile (shuffle-cards)
     :discard-pile nil
     :board-spaces spaces
     :booty-traps traps
     :final-space-img (rand-nth components/space-images)
     :show-card? false
     :victor nil
     :game-on? false}))

(defonce app-state (reagent/atom (initialize-game-state)))

(defn space-at [x y]
  (first (filter
           (fn [space]
             (and
               (<= (:low-x space) x (:high-x space))
               (<= (:low-y space) y (:high-y space))))
           (:board-spaces @app-state))))

(defn reset-game [_]
  (initialize-game-state))

(defn reset-game! []
  (swap! app-state reset-game))

(def event-type-desc {:start-game " started the game"
                      :roll-dice " rolled: "
                      :draw-card " drew: "
                      :spring-trap " sprung trap: "
                      :end-turn " ended the turn"
                      :move-name " moved to space: "})

(defn append-history
  ([game-state event-type val]
   (assoc game-state :history (conj (:history game-state) (str (first (:player-cycle game-state)) (event-type event-type-desc) val))))
  ([game-state event-type]
   (append-history game-state event-type nil)))

(defmulti record-history (fn [event-type game-data & more] event-type))

(defmethod record-history :start-game [event-type game-state]
  (append-history game-state event-type))

(defmethod record-history :roll-dice [event-type game-state]
  (append-history game-state event-type (get-in game-state [:current-dice :val])))

(defmethod record-history :draw-card [event-type game-state]
  (let [current-card (peek (:discard-pile game-state))]
    (append-history game-state event-type (:title current-card))))

(defmethod record-history :spring-trap [event-type game-state {:keys [trap-card] :as trap}]
  (append-history game-state event-type (:title trap-card)))

(defmethod record-history :move-name [event-type game-state space]
  (println "space" space)
  (append-history game-state event-type (:i space)))

(defmethod record-history :default [_ game-state]
  (println "no history recorded for default events")
  game-state)

(defn record-history! [event-type game-state & more]
  (let [updated-state (record-history event-type game-state (first more))]
    (reset! app-state updated-state)))

(defn current-player []
  (or (first (:player-cycle @app-state))
      "you"))

(defn roll-history-row [roll]
  (str (current-player) " just rolled a " (inc roll)))

(defn roll-dice [game-state]
  (let [roll (rand-int 6)]
    (assoc game-state :current-dice (get dice/dice-specs roll)
                      :roll-history (take 3 (conj (:roll-history game-state) (roll-history-row roll))))))

(defn roll-dice! []
  (->> @app-state
       (roll-dice)
       (record-history :roll-dice)
       (reset! app-state)))

(defn update-player-name! [name]
  (swap! app-state assoc :add-player-name name))

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

(defn initial-dot-data-map [players]
  (loop [dot-data {}
         dots players
         x 640
         y 20]
    (if (empty? dots)
      dot-data
      (recur (assoc dot-data (first dots) {:x x :y y}) (rest dots) (+ x 50) y))))

(defn initial-state [game-state]
  (let [new-game-state (assoc game-state :game-on? true
                                         :player-cycle (player-queue (:players game-state))
                                         :player-data (initial-player-data-map (:players game-state))
                                         :dot-data (initial-dot-data-map (:players game-state)))]
    new-game-state))


(defn start-game! []
  (->> @app-state
       (initial-state)
       (record-history :start-game)
       (reset! app-state)))

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
  (swap! app-state append-history :end-turn)
  (swap! app-state next-player))



(defn end-game [game-state name]
  (assoc game-state :players []
                    :victor name
                    :discard-pile (conj (:discard-pile game-state) cards/victory-card)
                    :show-card? true))

(defn end-game! [name]
  (.play (.getElementById js/document (get-in components/audio-snippets [:chief :name])))
  (swap! app-state end-game name))

(defn draw-card [game-state]
  (if-let [drawn-card (peek (:draw-pile game-state))]
    (let [updated-draw-pile (pop (:draw-pile game-state))
          updated-discard-pile (conj (:discard-pile game-state) drawn-card)]
      (assoc game-state :draw-pile updated-draw-pile
                        :discard-pile updated-discard-pile
                        :show-card? true))
    (do
      (.play (.getElementById js/document (get-in components/audio-snippets [:chief :name])))
      (assoc game-state :discard-pile (conj (:discard-pile game-state) cards/no-more-cards)))))

(defn draw-card! []
  (->> @app-state
       (draw-card)
       (record-history :draw-card)
       (reset! app-state)))

(defn trap-at [x y]
  (let [traps (:booty-traps @app-state)]
    (first (filter (fn [{:keys [low-x high-x low-y high-y]}]
                     (and (< low-x x high-x)
                          (< low-y y high-y)))
                   traps))))

(defn spring-trap [game-state {:keys [trap-card] :as trap}]
  (let [{:keys [card-style title body]} trap-card]
    (assoc game-state :discard-pile (conj (:discard-pile game-state) {:card-style card-style :title title :body body})
                      :show-card? true
                      :booty-traps (remove #{trap} (:booty-traps game-state)))))

(defn spring-trap! [trap]
  (let [game-state (spring-trap @app-state trap)
        game-state (record-history :spring-trap game-state trap)]
    (reset! app-state game-state)))

(defn about-y [y bcr]
  (+ 8 (- y (.-top bcr))))

(defn about-x [x bcr]
  (- x 20 (.-left bcr)))

(defn move-player-name [player-data bcr x y]
  ;; Approximate offsets for clicking in the middle of a name
  (assoc player-data :x (about-x x bcr) :y (about-y y bcr)))

(defn get-bcr [svg-root]
  (-> svg-root
      reagent/dom-node
      .getBoundingClientRect))

(defn move-name! [svg-root name]
  (let [player-data (get-in @app-state [:player-data name])]
    (fn [x y]
      (let [bcr (get-bcr svg-root)
            updated-player-data (move-player-name player-data bcr x y)
            updated-game-state (assoc-in @app-state [:player-data name] updated-player-data)]
        (reset! app-state updated-game-state)))))

(defn move-dot [dot-data bcr x y]
  (assoc dot-data :x (- x (.-left bcr)) :y (- y (.-top bcr))))

(defn move-dot! [svg-root name]
  (let [dot-data (get-in @app-state [:dot-data name])]
    (fn [x y]
      (let [bcr (get-bcr svg-root)
            updated-dot-data (move-dot dot-data bcr x y)]
        (swap! app-state assoc-in [:dot-data name] updated-dot-data)))))

(defn toggle-history [game-state]
  (assoc game-state :show-history (not (:show-history game-state))))

(defn toggle-history! []
  (swap! app-state toggle-history))


