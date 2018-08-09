# with

`vemv.with/with` is a 'syntax transformation' macro (like `->`) that greatly simplifies how your code is shaped.

As envisioned, it addresses primarily two problems:

* VCS diffs for Lisps can be noisy when e.g. wrapping an existing form with a `let`, greatly hindering understanding.
* Nested successions of `let`, `when-let`, `let`, `if-let` are awkward, and often leave the programmer
with a desire for something flatter.

If you know the  `for`/`doseq` keyword modifiers, you already know `with`'s API. Congratulations!

More concretely, this is how it looks like:

```clojure
(with [:let      [a 42]
       :when-let [b (when (> (rand) 0.5)
                      (* a 2))]
       :binding  [*print-level* b]
       :doseq    [i (range 10)]
       :future   []] ;; one thread per iteration
  (println (+ *print-level* i)))
```

Namely, for each clause, the right-hand value is passed as the first argument to the left-hand macro.

So, in the example `let` will receive `[a 42]`, and more surprisingly, `future` will receive `[]`, which will be a useless argument.
It's a minor cost for supporting both kind of macros.

I recommend more passing `nil` than `[]` for macros that only expect bodies, rather than args + body.

Macros which receive 2+ arguments + body aren't supported (they could, but I've seen no need so far).

## Usage

The recommended `ns` clause would be either:

```clojure
(:require [vemv.with :refer [with]])
;; or
(:use vemv.with)
```

The latter is in this case acceptable because the `vemv.with` namespace has, and will always have, just one form.
It seems a good pattern for selected namespaces to provide just one form, so they can be consumed concisely.

Now you can use `with`, as per the previous section.

`with` accepts arbitrary keywords. You can also pass symbols, which I recommend for non- `clojure.core` macros, or for namespace-qualified macros.

```clojure
(with [:let                  [redis {}] ;; Recommended: use keywords only for clojure.* macros
       core.async/go         []         ;; Symbol usage is recommended for all qualified symbols
       with-redis-connection redis]     ;; Use symbols for all non- clojure.* macros
  (println ...))
```

Refer to the tests for more examples.

## Emacs settings

The vertical aligment can be achieved by adding `"with"` to `clojure-align-binding-forms`.

You should `(put-clojure-indent 'with 1)`.

## Bonus: letfn advocacy

Given I'd have your attention, I'd invite you to use `letfn` more, which gets nicer with this library.

`letfn` gives us a nice middle ground between inlining would-be functions
at the cost of density, or decomposing code into helper functions at the cost of spareness.

## License

Copyright © 2018 vemv.net

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
