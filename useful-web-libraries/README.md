# useful-web-libraries

Short samples of usage various useful web development libraries
as described in the last part of Chapter 7 in Living Clojure book: *Other Useful Web Development Libraries*

Here is the list of libraries:
 * [Hiccup](https://github.com/weavejester/hiccup) for templating
   * Simple library for generating HTML on server side
 * [Enlive](https://github.com/cgrand/enlive) for templating from static HTML files
   * It also does server side HTML generation but different approach -> it takes the static HTML files as templates and applies transformations on them
   * [Enfocus](https://github.com/ckirkendall/enfocus) was inspired by Enlive
 * [Liberator](https://github.com/clojure-liberator/liberator) for content negotiation and other good things
   * [liberator - getting started](http://clojure-liberator.github.io/liberator/tutorial/getting-started.html)
   * for the liberator demo you can also run ring server: `lein ring server` and go to [http://localhost:3000/cat](http://localhost:3000/cat).
   * CURLs for testing:
     * `curl http://localhost:3000/cat -H 'Accept: text/plain'`
     * `curl http://localhost:3000/cat -H 'Accept: text/html'`
     * `curl http://localhost:3000/cat -H 'Accept: application/json'`
     * unsupported content type: `curl http://localhost:3000/cat -H 'Accept: application/xml'`
 * [Transit](https://github.com/cognitect/transit-clj) for a small, fast JSON alternative
   * Rationale: http://blog.cognitect.com/blog/2014/7/22/transit
 * [Om](https://github.com/omcljs/om) for powerful client-side applications
    * A ClojureScript interface to Facebook's React.
    * It has steeper learning curve, but there are nice tutorials like
    [Basic Tutorial](https://github.com/omcljs/om/wiki/Basic-Tutorial) by David Nolen.
 * [Hoplon](http://hoplon.io/) and [Luminus](http://www.luminusweb.net/) for inclusive, bundled libraries for web development
   * Good tutorial for Hoplon: https://github.com/hoplon/hoplon/wiki/Get-Started
 * Databases
   * [java.jdbc](https://github.com/clojure/java.jdbc)
   * [Korma](http://sqlkorma.com/)
   * [Yesql](https://github.com/krisajenkins/yesql)
     * separate sql queries into the external files
     * doesn't try to wrap SQL into yet another DSL
   * [Luminus](http://www.luminusweb.net/)
   * [Datomic](http://www.datomic.com/)



