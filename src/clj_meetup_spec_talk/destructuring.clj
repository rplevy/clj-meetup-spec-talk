(ns clj-meetup-spec-talk.destructuring
  (:require [clojure.spec :as s]
            [clojure.pprint :refer [pprint]]))

(s/conform int? 1)

(s/conform (s/and int? even?) 2)

(s/conform (s/and int? even?) 3)

(s/def ::name-or-id
  (s/or :name (s/nilable string?)
        :id int?))

(s/conform ::name-or-id nil)

(s/conform ::name-or-id 1)

(let [[tag id] (s/conform ::name-or-id "foo")]
  (prn :tag tag :id id))

(let [[tag id] (s/conform ::name-or-id 1)]
  (prn :tag tag :id id))

(let [{:keys [id]} (apply hash-map (s/conform ::name-or-id "foo"))
      {:keys [name]} (apply hash-map (s/conform ::name-or-id "foo"))]
  (prn id name))

;; for or, tag for nil is the first nilable
(s/def ::name-or-id-both-nilable
  (s/or :name (s/nilable string?)
        :id (s/nilable int?)))

(s/conform ::name-or-id-both-nilable nil)


;; sequences

;; doesn't include tags
(s/conform (s/coll-of ::name-or-id []) [1 2 3])

;; doesn't report sub-spec reasons for failing
(pprint (s/explain-data (s/coll-of ::name-or-id []) [1 2 #{3}]))

;; DOES include tags
(s/def ::coll-of-three-fnoid
  (s/cat :first-name-or-id ::name-or-id
         :second-name-or-id ::name-or-id
         :third-name-or-id ::name-or-id))

(s/conform ::coll-of-three-fnoid [1 2 3])

(let [{:keys [first-name-or-id
              second-name-or-id
              third-name-or-id]} (s/conform ::coll-of-three-fnoid
                                            [1 2 3])]
  third-name-or-id)

;; DOES report sub-spec reasons for failing
(pprint (s/explain-data ::coll-of-three-fnoid [1 #{2} 3]))

;; s/*
(s/def ::coll-of-fnoid
  (s/*
   (s/cat :name-or-id ::name-or-id)))

;; Does not include tags or report subspec reasons when failing.
;; returns matches as vector of maps
(s/conform ::coll-of-fnoid [1 2 3])
(let [[{:keys [name-or-id] :as first-result}
       second-result] (s/conform ::coll-of-fnoid [1 2 3])]
  (prn :first-name-or-id name-or-id)
  (prn :first first-result)
  (prn :second second-result))

(pprint (s/explain-data ::coll-of-fnoid [#{1} 2 3]))


;; s/alt

(let [[item1 item2 item3] (s/conform (s/* (s/alt :name string?
                                                 :id int?))
                                     ["foo" 1 1])]
  (prn item1))


;; recursively nested data

(let [[[first-tag first-value] item2] (s/conform (s/coll-of ::coll-of-fnoid [])
                                                 [["name" 1]
                                                  ["hello" 22]])]
  (prn first-value))


(pprint (s/explain-data (s/coll-of ::coll-of-fnoid [])
                        [["name" #{1}]
                         ["hello" 22]]))


;; +

(s/def ::odds-then-maybe-even (s/cat :odds (s/+ odd?)
                                     :even (s/? even?)))

(s/conform ::odds-then-maybe-even [1 3 5 100])

(s/conform ::odds-then-maybe-even [1 100])

(s/conform ::odds-then-maybe-even [1])

(s/explain-data ::odds-then-maybe-even [100])


;; s/&

(s/def ::even-strings (s/& (s/* string?)
                           #(even? (count %))))

(s/conform ::even-strings ["x" "y"])

(pprint (s/explain-data ::even-strings ["x" "y" "z"]))

(pprint (s/explain-data ::even-strings [1 "x" "y" "z"]))
