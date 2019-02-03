(ns game-of-life.core
  (:gen-class))
(defn neighbours [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ x dx) (+ y dy)]))
(defn step [cells]
  (apply hash-set (map first (filter #(or (= 3 (second %)) (and (= 2 (second %)) (cells (first %))))
                                     (frequencies (apply concat (map neighbours cells)))))))
(defn draw
  [w h step cells]
  (let [state (atom cells)
        run (atom true)
        listener (proxy [java.awt.event.WindowAdapter] []
                   (windowClosing [_] (reset! run false)))
        pane
          (doto (proxy [javax.swing.JPanel] []
                  (paintComponent [^java.awt.Graphics g]
                    (let [g (doto ^java.awt.Graphics2D (.create g)
                              (.setColor java.awt.Color/BLACK)
                              (.fillRect 0 0 (* w 10) (* h 10))
                              (.setColor java.awt.Color/WHITE))]
                      (doseq [[x y] @state]
                        (.fillRect g (inc (* 10 x)) (inc (* 10 y)) 8 8)))))
            (.setPreferredSize (java.awt.Dimension. (* 10 w) (* 10 h))))]
    (doto (javax.swing.JFrame. "Game of Life")
      (.setContentPane pane)
      (.addWindowListener listener)
      .pack
      (.setVisible true))
    (future (while @run
              (Thread/sleep 80)
              (swap! state step)
              (.repaint pane)))))
(defn -main
  [& args]
  (draw 100 100 step #{[15 15] [15 17] [16 16] [15 16]}))
