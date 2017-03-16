;;Namespace
(ns wiring.core (:gen-class) (:use clojure.set))
;;Reading in file in groups of 2 char sequences at a time to get wiring pairs.
(defn get-wiring-groups
  [filename]
  (partition 2 (re-seq #"[a-zA-Z0-9]+" (slurp filename))))
;;Checks each of the sequences for whether or not the first character is a
;;number
;;or letter sequence. Sets letters as variables. Because it only checks first
;;Outputs may be letters aswell. Restricting only middle inputs to be numbers.
(defn get-variable-set
  [wiring-groups]
  (reduce (fn [build-set [variable? number]]
            (if (not (number? (read-string variable?)))
              (conj build-set variable?)
              build-set))
    #{}
    wiring-groups))
;;For putting together wires under same output
(defn merge-wires
  [new-wire wire-list]
  (merge-with (fn [x y] (list x y)) new-wire wire-list))
;;Creates the wiring map using merge-wires on wiring-groups
(defn get-wiring-map-no-defaults
  [wiring-groups]
  (reduce (fn [build-map [from to]] (merge-wires build-map {to from}))
    {}
    wiring-groups))
;;Turns single input gates to true and single input
(defn add-defaults
  [wiring-map]
  (reduce-kv (fn [m k v]
               (if (list? v) (conj m [k v]) (conj m [k (list v true)])))
             {}
             wiring-map))
;;Gets real wiring map
(defn get-wiring-map
  [wiring-groups]
  (-> wiring-groups
      (get-wiring-map-no-defaults)
      (add-defaults)))

;;Checks wiring list to find all inputs, used later for finding which are
;;outputs.
(defn get-input-set
  [wiring-groups]
  (reduce #(conj %1 (first %2)) #{} wiring-groups))
;;Doubles a list by adding a copy to end
(defn double-list [l] (flatten (repeat 2 l)))
;;Doubles a list by repeating each element twice
(defn double-elements [l] (reverse (reduce #(conj %1 %2 %2) '() l)))
;;Creates a list of n possible inputs true or false.
(defn gen-possibilities
  [letter-count]
  (loop [n letter-count
         [start :as all] (list (list true false))]
    (if (> n 1)
      (recur (- n 1) (conj (map double-elements all) (double-list start)))
      all)))
;;Takes the first from each of the possibilities lists and pairs them with
;;their
;;variable in a map
(defn get-current-possibilities [letters all] (zipmap letters (map first all)))
;;Checks if value is boolean
(defn boolean? [v] (or (true? v) (false? v)))
;;Simple nand gate that takes two boolean inputs
(defn nand [in1 in2] (not (and in1 in2)))
;;Checks to see if wire value is known. If so it returns the known value
;;otherwise it returns the wire.
(defn check-known
  [known wire]
  (if (contains? known wire) (get known wire) wire))
;;Interprets a single wire by using nand gate or looking up value
(defn interpret-single
  [[build-new-unknown known] k [wire1 wire2]]
  (if (and (boolean? wire1) (boolean? wire2))
    [build-new-unknown (conj known [k (nand wire1 wire2)])]
    [(conj build-new-unknown
           [k [(check-known known wire1) (check-known known wire2)]]) known]))
;;Interprets one step (either getting nand value or looking up) each wire
(defn interpret
  [unknown known]
  (reduce-kv #(interpret-single %1 %2 %3) [{} known] unknown))
;;Steps till all wire outputs are known
(defn step-all
  [unk kno]
  (loop [[unknown known] [unk kno]]
    (if (not (empty? unknown)) (recur (interpret unknown known)) known)))
;;Calls step all on each possible sequence of inputs
(defn do-all-possibilities
  [wiring letters possibilities-lists]
  (loop [all possibilities-lists
         outputs []]
    (if (not (empty? (first all)))
      (recur (map rest all)
             (conj outputs
                   (step-all wiring (get-current-possibilities letters all))))
      outputs)))
;;Takes in a set and will take those elements from the list of maps
(defn take-results-from
  [s results-list]
  (for [results results-list]
    (filter (fn [[wire output]] (contains? s wire)) results)))
;;Prints the input and output section titles
(defn print-titles
  [inputs]
  (printf (str "%-" (* 10 inputs) "s") "INPUTS")
  (print "OUTPUTS"))
;;Prints the input and output names
(defn print-headers
  [[variables]]
  (doseq [[variable value] variables] (printf "%-10s" variable)))
;;Prints all output and input values
(defn print-vals
  [variables]
  (doseq [[variable value] variables] (printf "%-10s" value)))
;;Driver
(defn driver 
  [input-file]
  (let [wiring-groups (get-wiring-groups input-file)
        wiring-map (get-wiring-map wiring-groups)
        variable-set (get-variable-set wiring-groups)
        output-set (difference (set (keys wiring-map))
                               (get-input-set wiring-groups))
        possibilities (gen-possibilities (count variable-set))
        results-list
          (do-all-possibilities wiring-map variable-set possibilities)
        variables-list (take-results-from variable-set results-list)
        outputs-list (take-results-from output-set results-list)]
    (print-titles (count (first variables-list)))
    (println)
    (print-headers variables-list)
    (print-headers outputs-list)
    (println)
    (doall (map (fn [variables outputs]
                  (print-vals variables)
                  (print-vals outputs)
                  (println))
             variables-list
             outputs-list))))

;;Main for getting input file or running the default file
(defn -main
  ([] (driver "demo.txt"))
  ([input-file] (driver input-file)))
