(ns lbperryday.help)

(defn help-text []
  [:div
   {:style
    {:text-align "left"}}
   [:ul "On your turn, you should:"
    [:li "Roll the dice."]
    [:li "Click and drag your meeple to the appropriate space on the board."]
    [:li "Draw a card."]
    [:li "Do what the card says."]]])