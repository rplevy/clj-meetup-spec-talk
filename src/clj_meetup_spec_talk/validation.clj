(ns clj-meetup-spec-talk.validation
  (:require [clojure.pprint :refer [pprint]]
            [clojure.spec :as s]))

;; basic validation

(s/valid? #(> % 5) 10)

(s/valid? #(> % 5) 0)


;; registry

(s/def ::greater-than-five #(> % 5))

(s/valid? ::greater-than-five 10)


;; composing predicates

(s/def ::coll-greater-than-five
  (s/and coll?
         #(> (count %) 5)))

(s/valid? ::coll-greater-than-five [1 2 3 4 5 6])

(s/valid? ::coll-greater-than-five [1 2 3 4 5])


;; explain

(s/explain ::coll-greater-than-five [1 2 3 4 5])

(s/explain ::coll-greater-than-five 5)

;; unknown predicate
(s/explain #(> % 6) 5)

(s/explain coll? 5)

;; not unknown
(s/def ::coll coll?)

(s/explain ::coll 5)

(s/explain (s/and coll? #(> (count %) 5)) 5)

;; get explanation as data structure or as string

(s/explain-data ::coll-greater-than-five 5)

(s/explain-str ::coll-greater-than-five [1 2 3 4 5])


;; composing predicates, cotd.

(s/def ::name-or-id
  (s/or :name string?
        :id int?))

(s/valid? ::name-or-id "foo")

(s/valid? ::name-or-id 1)

(s/valid? ::name-or-id #{1})

(s/explain ::name-or-id #{1})

(pprint (s/explain-data ::name-or-id #{1}))
